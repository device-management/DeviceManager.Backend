

namespace Tn.DeviceManager.Measurements
{
    using Microsoft.Extensions.Logging;
    using Newtonsoft.Json.Linq;
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Linq;
    using System.Net;
    using System.Net.Http;
    using System.Text;
    using System.Threading.Tasks;

    public class InfluxDbRepository : IMeasurementsRepository
    {
        private readonly ILogger _logger;

        private readonly InfluxDbSettings _settings;

        private readonly HttpClient _httpClient;

        public InfluxDbRepository(InfluxDbSettings settings, HttpClient httpClient, ILogger logger)
        {
            _settings = settings ?? throw new ArgumentNullException(nameof(settings));
            _httpClient = httpClient ?? throw new ArgumentNullException(nameof(httpClient));
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
        }

        public async Task<ICollection<QueryResult>> Query(ICollection<QueryDescriptor> queries)
        {
            if (queries == null)
            {
                throw new ArgumentNullException(nameof(queries));
            }

            if(queries.Count == 0)
            {
                throw new ArgumentException("The queries collection is empty.");
            }

            string command = CreateSqlCommand(queries);

            var query = new Dictionary<string, string>();
            query.Add("db", this._settings.DatabaseName);
            query.Add("q", command);

            string queryAsString = BuildQuery(query);
            var uriBuilder = new UriBuilder(_settings.DatabaseAddress)
            {
                Path = "query",
                Query = queryAsString
            };

            HttpRequestMessage request = PrepareRequest(HttpMethod.Get, uriBuilder.Uri);

            _logger.LogDebug("Query HTTP request | Method: {0}, Uri: {1}", request.Method, request.RequestUri);
            
            try
            {
                HttpResponseMessage response = await this._httpClient.SendAsync(request);
                await response.EnsureSuccessStatusCodeAsync();
                string responseContent = await response.Content.ReadAsStringAsync();
                return DeserializeQueryResults(responseContent);
            }
            catch (Exception ex)
            {
                throw new MeasurementsRepositoryException("Cannot query measurements from database.", ex);
            }
        }

        public async Task Write(WriteRequest writeRequest)
        {
            if (writeRequest == null)
            {
                throw new ArgumentNullException(nameof(writeRequest));
            }

            var query = new Dictionary<string, string>();
            query.Add("db", this._settings.DatabaseName);

            string queryAsString = BuildQuery(query);

            var uriBuilder = new UriBuilder(_settings.DatabaseAddress)
            {
                Path = "write",
                Query = queryAsString
            };

            HttpRequestMessage request = PrepareRequest(HttpMethod.Post, uriBuilder.Uri);
            
            try
            {
                string serializedRequest = SerializeWriteRequest(writeRequest);
                _logger.LogDebug("Write HTTP request | Method: {0}, Uri: {1}, Content {2}", request.Method, request.RequestUri, serializedRequest);
                request.Content = new StringContent(serializedRequest, Encoding.UTF8, "text/plain");

                HttpResponseMessage response = await this._httpClient.SendAsync(request);

                await response.EnsureSuccessStatusCodeAsync();
            }
            catch (Exception ex)
            {
                throw new MeasurementsRepositoryException("Cannot write measurements into database.", ex);
            }
        }

        public async Task EnsureDbCreated()
        {
            string command = $"CREATE DATABASE {this._settings.DatabaseName}";

            var query = new Dictionary<string, string>();
            query.Add("q", command);

            string queryAsString = BuildQuery(query);

            var uriBuilder = new UriBuilder(_settings.DatabaseAddress)
            {
                Path = "query",
                Query = queryAsString
            };

            HttpRequestMessage request = PrepareRequest(HttpMethod.Get, uriBuilder.Uri);

            _logger.LogDebug("Create database HTTP request | Method: {0}, Uri: {1}", request.Method, request.RequestUri);

            try
            {
                HttpResponseMessage response = await this._httpClient.SendAsync(request);

                await response.EnsureSuccessStatusCodeAsync();
            }
            catch(Exception ex)
            {
                throw new MeasurementsRepositoryException("Cannot create database.", ex);
            }
        }

        private static HttpRequestMessage PrepareRequest(HttpMethod httpMethod, Uri requestUri)
        {
            var request = new HttpRequestMessage(httpMethod, requestUri);
            request.Headers.Add("User-Agent", "DeviceManager");
            request.Headers.Add("Accept", "application/json");
            return request;
        }

        private static string SerializeWriteRequest(WriteRequest request)
        {
            var queryList = new List<string>();

            foreach (Point point in request.Points)
            {
                string value = $" value={point.Value.ToString(new CultureInfo("en-US"))}";
                string timestamp = point.Timestamp.HasValue ? $" { point.Timestamp.Value.ToUnixNanoseconds().ToString() }" : string.Empty;
                queryList.Add($"{ request.Name }{ value }{ timestamp }");
            }

            return string.Join("\n", queryList);
        }

        private static string CreateSqlCommand(ICollection<QueryDescriptor> queries)
        {
            var queriesBuilder = new List<string>(queries.Count());
            foreach (QueryDescriptor query in queries)
            {
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.Append($"SELECT * FROM \"{query.Name}\"");
                var sqlQuery = new List<string>();
                if (query.DateFrom.HasValue)
                {
                    sqlQuery.Add($"time > {query.DateFrom.Value.ToUnixNanoseconds()}");
                }

                if (query.DateTo.HasValue)
                {
                    sqlQuery.Add($"time < {query.DateTo.Value.ToUnixNanoseconds()}");
                }

                if (sqlQuery.Count > 0)
                {
                    queryBuilder.Append($" WHERE {string.Join(" AND ", sqlQuery)}");
                }

                queriesBuilder.Add(queryBuilder.ToString());
            }

            return string.Join(";", queriesBuilder);
        }

        private static string BuildQuery(IDictionary<string, string> query)
        {
            var keyValues = new List<string>(query.Count);
            keyValues.AddRange(query.Select(param => $"{param.Key}={WebUtility.UrlEncode(param.Value)}"));
            return keyValues.Count > 0 ? $"?{string.Join("&", keyValues)}" : string.Empty;
        }

        private static string FormatValueEntry(object value)
        {
            // Format and escape the values
            var result = value.ToString();

            // surround strings with quotes
            if (value.GetType() == typeof(string))
            {
                result = "\"" + value.ToString() + "\"";
            }
            // api needs lowercase booleans
            else if (value.GetType() == typeof(bool))
            {
                result = value.ToString().ToLower();
            }
            // InfluxDb does not support a datetime type for fields or tags
            // convert datetime to unix long
            else if (value.GetType() == typeof(DateTime))
            {
                result = ((DateTime)value).ToUnixNanoseconds().ToString();
            }
            // For cultures using other decimal characters than '.'
            else if (value.GetType() == typeof(decimal))
            {
                result = ((decimal)value).ToString("0.0###################", CultureInfo.InvariantCulture);
            }
            else if (value.GetType() == typeof(float))
            {
                result = ((float)value).ToString("0.0###################", CultureInfo.InvariantCulture);
            }
            else if (value.GetType() == typeof(double))
            {
                result = ((double)value).ToString("0.0###################", CultureInfo.InvariantCulture);
            }
            else if (value.GetType() == typeof(long) || value.GetType() == typeof(int))
            {
                result = result + "i";
            }

            return result;
        }

        private static List<QueryResult> DeserializeQueryResults(string json)
        {
            List<QueryResult> results = new List<QueryResult>();
            var jResults = JObject.Parse(json);
            foreach(JToken jResult in jResults["results"].ToArray())
            {
                JToken jValues = jResult.SelectToken("series[0].values");
                if (jValues == null)
                {
                    continue;
                }
                List<Point> points = new List<Point>();
                foreach (JToken jValue in jValues.ToArray())
                {
                    JToken[] jValueEntries = jValue.ToArray();
                    var value = decimal.Parse(jValueEntries[1].ToString());
                    var timestamp = DateTime.Parse(jValueEntries[0].ToString());
                    points.Add(new Point(value, timestamp));
                }
                string name = jResult.SelectToken("series[0].name").ToString();
                results.Add(new QueryResult(name, points));
            }

            return results;
        }
    }
}
