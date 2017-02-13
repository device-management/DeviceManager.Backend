
namespace Tn.DeviceManager.Test
{
    using Microsoft.Extensions.Logging;
    using Microsoft.Extensions.Logging.Debug;
    using Microsoft.Extensions.Options;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Net;
    using System.Net.Http;
    using System.Text;
    using System.Threading.Tasks;
    using Tn.DeviceManager.Measurements;

    [TestClass]
    public class InfluxDbRepositoryTest
    {
        private InfluxDbSettings _dbSettings;
        private IOptions<InfluxDbSettings> _dbOptions;

        [TestInitialize]
        public void SetUp()
        {
            this._dbSettings = new InfluxDbSettings { DatabaseName = "mydb", DatabaseAddress = "http://localhost:8086" };
            this._dbOptions = Options.Create(_dbSettings);
        }

        [TestMethod]
        public void Query_ArgumentNull_ExceptionThrown()
        {
            // Arange
            var repository = new InfluxDbRepository(this._dbOptions.Value, new HttpClient(), GetMockLogger<InfluxDbRepository>());

            // Act & Assert
            Assert.ThrowsExceptionAsync<ArgumentNullException>(() => repository.Query(null));
        }

        [TestMethod]
        public async Task Query_RequestSent_ProperRequestGenerated()
        {
            // Arange
            var filter1 = new QueryDescriptor("filter1");
            var filter2 = new QueryDescriptor("filter2", DateTime.Now);

            var expectedQuery = new StringBuilder()
                        .Append($"?db={this._dbSettings.DatabaseName}")
                        .Append("&q=")
                        .Append(WebUtility.UrlEncode($"SELECT * FROM \"{filter1.Name}\";"))
                        .Append(WebUtility.UrlEncode($"SELECT * FROM \"{filter2.Name}\" WHERE time > {filter2.DateFrom.Value.ToUnixNanoseconds()}"))
                        .ToString();

            Uri expectedUri = new UriBuilder(this._dbSettings.DatabaseAddress)
            {
                Path = "query",
                Query = expectedQuery
            }.Uri;
            Uri actualUri = null;

            var mockResponseHandler = new MockResponseHandler(request =>
            {
                actualUri = request.RequestUri;
                return new HttpResponseMessage(HttpStatusCode.OK) { Content = new StringContent(InfluxDbResources.ReadResponseJson) };
            });

            var repository = new InfluxDbRepository(this._dbOptions.Value, new HttpClient(mockResponseHandler), GetMockLogger<InfluxDbRepository>());

            // Act
            ICollection<QueryResult> set = await repository.Query(new[] { filter1, filter2 });

            // Assert
            Assert.AreEqual(expectedUri, actualUri);
        }

        [TestMethod]
        public void EnsureDbCreated_RequestSent_ProperRequestGenerated()
        {
            // Arange
            var expectedQuery = new StringBuilder()
                        .Append("?q=")
                        .Append(WebUtility.UrlEncode($"CREATE DATABASE {this._dbSettings.DatabaseName}"))
                        .ToString();

            Uri expectedUri = new UriBuilder(this._dbSettings.DatabaseAddress)
            {
                Path = "query",
                Query = expectedQuery
            }.Uri;
            Uri actualUri = null;

            var mockResponseHandler = new MockResponseHandler(request =>
            {
                actualUri = request.RequestUri;
                return new HttpResponseMessage(HttpStatusCode.OK);
            });

            var factory = new InfluxDbRepositoryFactory(this._dbOptions, new HttpClient(mockResponseHandler), GetMockLogger<InfluxDbRepositoryFactory>());

            // Act
            var repository = factory.CreateRepository(true);

            // Assert
            Assert.AreEqual(expectedUri, actualUri);
        }

        [TestMethod]
        public void Write_ArgumentNull_ExceptionThrown()
        {
            // Arange
            var repository = new InfluxDbRepository(this._dbOptions.Value, new HttpClient(), GetMockLogger<InfluxDbRepository>());

            // Act & Assert
            Assert.ThrowsExceptionAsync<ArgumentNullException>(() => repository.Write(null));
        }

        [TestMethod]
        public async Task Write_RequestSent_ProperRequestGenerated()
        {
            // Arange
            var p1 = new Point(312.42m);
            var p2 = new Point(-24412.355m, DateTime.Now);

            var expectedContent = new StringBuilder()
                        .Append($"Measurement value={p1.Value.ToString(new CultureInfo("en-US"))}\n")
                        .Append($"Measurement value={p2.Value.ToString(new CultureInfo("en-US"))} {p2.Timestamp.Value.ToUnixNanoseconds()}")
                        .ToString();

            string expectedQuery = $"?db={this._dbSettings.DatabaseName}";
            Uri expectedUri = new UriBuilder(this._dbSettings.DatabaseAddress)
            {
                Path = "write",
                Query = expectedQuery
            }.Uri;
            Uri actualUri = null;
            string actualContent = null;

            var mockResponseHandler = new MockResponseHandler(request =>
            {
                actualUri = request.RequestUri;
                actualContent = request.Content.ReadAsStringAsync().Result;
                return new HttpResponseMessage(HttpStatusCode.OK) { Content = new StringContent(InfluxDbResources.ReadResponseJson) };
            });

            var repository = new InfluxDbRepository(this._dbOptions.Value, new HttpClient(mockResponseHandler), GetMockLogger<InfluxDbRepository>());

            // Act
            await repository.Write(new WriteRequest("Measurement", new[] { p1, p2 }));

            // Assert
            Assert.AreEqual(expectedUri, actualUri);
            Assert.AreEqual(expectedContent, actualContent);
        }

        private static ILogger<T> GetMockLogger<T>()
        {
            var logger = new Mock<ILogger<T>>();
            return logger.Object;
        }
    }

    public class MockResponseHandler : DelegatingHandler
    {
        private readonly Func<HttpRequestMessage, HttpResponseMessage> _requestHandler;
        public MockResponseHandler(Func<HttpRequestMessage, HttpResponseMessage> requestHandler)
        {
            _requestHandler = requestHandler ?? throw new ArgumentNullException(nameof(requestHandler));
        }
        protected async override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, System.Threading.CancellationToken cancellationToken)
        {
            return _requestHandler(request);
        }
    }
}
