
namespace Tn.DeviceManager.EventDriven.Events
{
    using Measurements;
    using System;
    using System.Collections.Generic;
    using System.Linq;

    public class MeasurementEvent
    {
        public MeasurementEvent(string deviceId, Point[] points)
        {
            if (string.IsNullOrEmpty(deviceId))
                throw new ArgumentNullException(nameof(deviceId));

            DeviceId = deviceId;
            Points = points ?? Array.Empty<Point>();
        }

        public string DeviceId { get; }

        public Point[] Points { get; }
    }
}
