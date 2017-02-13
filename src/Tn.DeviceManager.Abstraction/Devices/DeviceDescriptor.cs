
namespace Tn.DeviceManager.Devices
{
    using System;
    using System.Collections.Generic;

    public class DeviceDescriptor
    {
        public DeviceDescriptor(string deviceId, IReadOnlyDictionary<string, object> properties)
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
