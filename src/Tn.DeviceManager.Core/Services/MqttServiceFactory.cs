
namespace Tn.DeviceManager.Services
{
    using Microsoft.Extensions.Logging;
    using Microsoft.Extensions.Options;
    using M2Mqtt;
    using System;
    using Tn.DeviceManager.EventDriven;
    using Tn.DeviceManager.Serialization;

    public class MqttServiceFactory
    {
        private readonly ILogger _logger;

        private readonly IMessageBus _messageBus;

        private readonly IObjectSerializer _serializer;

        private readonly MqttServiceSettings _settings;

        public MqttServiceFactory(IOptions<MqttServiceSettings> options, ILogger<MqttServiceFactory> logger, IMessageBus messageBus, IObjectSerializer serializer)
        {
            if (options == null)
                throw new ArgumentNullException(nameof(options));

            options.EnsureValueNotNull();

            _settings = options.Value;
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
            _messageBus = messageBus ?? throw new ArgumentNullException(nameof(messageBus));
            _serializer = serializer ?? throw new ArgumentNullException(nameof(serializer));
        }

        public MqttService CreateService()
        {
            var client = new MqttClient(_settings.BrokerHostName, _settings.BrokerPort, false, null, null, MqttSslProtocols.None);
            return new MqttService(client, _settings, _messageBus, _serializer, _logger);
        }
    }
}
