namespace Tn.DeviceManager.EventDriven.Events
{
    using System;
    using System.Collections.Generic;

    public class PropertiesChangedEvent
    {
        public PropertiesChangedEvent(string deviceId, IReadOnlyDictionary<string, object> properties)
        {
            if (string.IsNullOrEmpty(deviceId))
                throw new ArgumentNullException(nameof(deviceId));

            DeviceId = deviceId;
            Properties = properties ?? Dictionary.EmptyReadOnly();
        }

        public string DeviceId { get; }

        public IReadOnlyDictionary<string, object> Properties { get; }
    }
}
