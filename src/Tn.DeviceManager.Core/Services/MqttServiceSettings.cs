using System;
using System.Collections.Generic;
using System.Text;

namespace Tn.DeviceManager.Services
{
    public class MqttServiceSettings
    {
        public string BrokerHostName { get; set; }

        public int BrokerPort { get; set; }

        public string ClientId { get; set; }

        public string StateTopic { get; set; }

        public string MeasurmentTopic { get; set; }

        public string RegisterTopic { get; set; }

        public string CommandTopicPattern { get; set; }
    }
}
