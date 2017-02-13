namespace Tn.DeviceManager.EventDriven.Events
{
    using System;
    using System.Collections.Generic;

    public class CommandEvent
    {
        public CommandEvent(string deviceId, IReadOnlyDictionary<string, object> fields)
        {
            if (string.IsNullOrEmpty(deviceId))
                throw new ArgumentNullException(nameof(deviceId));

            DeviceId = deviceId;
            Fields = fields ?? Dictionary.EmptyReadOnly();
        }

        public string DeviceId { get; }

        public IReadOnlyDictionary<string, object> Fields { get; }
    }
}
