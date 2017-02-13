

namespace Tn.DeviceManager.Measurements
{
    using Microsoft.Extensions.Options;
    using Microsoft.Extensions.Logging;
    using System;
    using System.Net.Http;

    public class InfluxDbRepositoryFactory
    {
        private readonly ILogger _logger;

        private readonly InfluxDbSettings _settings;

        private readonly HttpClient _httpClient;

        public InfluxDbRepositoryFactory(IOptions<InfluxDbSettings> options, HttpClient httpClient, ILogger<InfluxDbRepositoryFactory> logger)
        {
            if (options == null)
                throw new ArgumentNullException(nameof(options));

            options.EnsureValueNotNull();

            _settings = options.Value;
            _httpClient = httpClient ?? throw new ArgumentNullException(nameof(httpClient));
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
        }

        public InfluxDbRepository CreateRepository(bool ensureDbCreated)
        {
            var influxDbRepositry = new InfluxDbRepository(_settings, _httpClient, _logger);
            if (ensureDbCreated)
            {
                influxDbRepositry.EnsureDbCreated().Wait();
            }
            return influxDbRepositry;
        }
    }
}
