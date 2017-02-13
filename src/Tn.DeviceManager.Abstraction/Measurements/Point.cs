

namespace Tn.DeviceManager.Measurements
{
    using System;

    public class Point
    {
        public Point(decimal value, DateTime? timestamp = null)
        {
            Value = value;
            Timestamp = timestamp;
        }

        public decimal Value { get; }

        public DateTime? Timestamp { get; }
    }
}
