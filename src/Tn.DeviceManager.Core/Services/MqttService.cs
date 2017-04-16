

namespace Tn.DeviceManager.Services
{
    using M2Mqtt;
    using Hosting;
    using System;
    using System.Threading.Tasks;
    using M2Mqtt.Messages;
    using System.Threading;
    using Tn.DeviceManager.EventDriven;
    using Tn.DeviceManager.EventDriven.Events;
    using Tn.DeviceManager.Serialization;
    using System.Text;
    using System.Text.RegularExpressions;
    using Microsoft.Extensions.Logging;

    public class MqttService : LifecycleSupport
    {
        /// <summary>
        /// A semaphore for stopping procedure synchronization.
        /// </summary>
        private readonly SemaphoreSlim _stopSemaphore = new SemaphoreSlim(1);

        private readonly IMessageBus _messageBus;

        private readonly IObjectSerializer _serializer;

        private readonly ILogger _logger;

        private readonly MqttClient _mqttClient;

        private readonly MqttServiceSettings _settings;

        private readonly TopicMatcher _topicMatcher;

        private IDisposable _subscription;

        public MqttService(MqttClient mqttClient, MqttServiceSettings settings, IMessageBus messageBus, IObjectSerializer serializer, ILogger logger)
        {
            _mqttClient = mqttClient ?? throw new ArgumentNullException(nameof(mqttClient));
            _settings = settings ?? throw new ArgumentNullException(nameof(settings));
            _messageBus = messageBus ?? throw new ArgumentNullException(nameof(messageBus));
            _serializer = serializer ?? throw new ArgumentNullException(nameof(serializer));
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
            _topicMatcher = new TopicMatcher(settings);
            _mqttClient.ConnectionClosed += ConnectionClosed;
            _mqttClient.MqttMsgPublishReceived += MessageReceived;
        }

        protected override Task DoStart()
        {
            _mqttClient.Connect(_settings.ClientId);
            _mqttClient.Subscribe(
                new[] { _settings.StateTopic, _settings.MeasurmentTopic, _settings.RegisterTopic }, 
                new byte[] { 1, 1, 1 });
            _subscription = _messageBus.Observe<CommandEvent>().Subscribe(command => HandleCommandEvent(command));

            return Task.FromResult(0);
        }

        protected override async Task DoStop()
        {
            _subscription?.Dispose();
            if(_mqttClient.IsConnected)
            {
                _mqttClient.Disconnect();
                await _stopSemaphore.WaitAsync();
            }
        }

        protected override void Dispose(bool disposing)
        {
            _mqttClient.ConnectionClosed -= ConnectionClosed;
            _mqttClient.MqttMsgPublishReceived -= MessageReceived;
            _stopSemaphore.Dispose();
            base.Dispose(disposing);
        }

        private void HandleCommandEvent(CommandEvent command)
        {
            try
            {
                string serializedEvent = _serializer.SerializeObject(command);
                _mqttClient.Publish(string.Format(_settings.CommandTopicPattern, command.DeviceId), Encoding.UTF8.GetBytes(serializedEvent));
            }
            catch(SerializationException ex)
            {
                _logger.LogError(0, ex, "Cannot serialize a command event.");
            }
        }

        private void ConnectionClosed(object sender, EventArgs e)
        {
            _stopSemaphore.Release();
        }

        private void MessageReceived(object sender, MqttMsgPublishEventArgs e)
        {
            Type messageType;
            if(_topicMatcher.MatchState(e.Topic))
            {
                messageType = typeof(PropertiesChangedEvent);
            }
            else if(_topicMatcher.MatchMeasurment(e.Topic))
            {
                messageType = typeof(MeasurementEvent);
            }
            else if(_topicMatcher.MatchRegister(e.Topic))
            {
                messageType = typeof(RegisterEvent);
            }
            else
            {
                _logger.LogError("Unrecognized topic from the mqtt broker. Topic: {0}.", e.Topic);
                return;
            }

            string payload = Encoding.UTF8.GetString(e.Message);

            try
            {
                object serializedObject = _serializer.DeserializeObject(payload, messageType);
                _messageBus.Publish(serializedObject);
            }
            catch(SerializationException ex)
            {
                _logger.LogError(0, ex, "Cannot serialize a message. Topic: {0}, Payload: {1}.", e.Topic, payload);
            }
        }

        private class TopicMatcher
        {
            private readonly string _stateRegex;

            private readonly string _measurmentRegex;

            private readonly string _registerRegex;

            public TopicMatcher(MqttServiceSettings settings)
            {
                _stateRegex = ParseTopicToRegex(settings.StateTopic);
                _measurmentRegex = ParseTopicToRegex(settings.MeasurmentTopic);
                _registerRegex = ParseTopicToRegex(settings.RegisterTopic);
            }

            public bool MatchState(string topic)
            {
                return Regex.IsMatch(topic, _stateRegex);
            }

            public bool MatchMeasurment(string topic)
            {
                return Regex.IsMatch(topic, _measurmentRegex);
            }

            public bool MatchRegister(string topic)
            {
                return Regex.IsMatch(topic, _registerRegex);
            }

            private static string ParseTopicToRegex(string topic)
            {
                return topic.Replace("+", "[^/]+").Replace("#", ".+");
            }
        }
    }
}
