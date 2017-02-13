namespace Tn.DeviceManager.EventDriven.Events
{
    using System;
    using System.Collections.Generic;

    public class StateChangedEvent
    {
        public StateChangedEvent(string deviceId, IReadOnlyDictionary<string, object> state)
        {
            if (string.IsNullOrEmpty(deviceId))
                throw new ArgumentNullException(nameof(deviceId));

            DeviceId = deviceId;
            State = state ?? Dictionary.EmptyReadOnly();
        }

        public string DeviceId { get; }

        public IReadOnlyDictionary<string, object> State { get; }
    }
}
