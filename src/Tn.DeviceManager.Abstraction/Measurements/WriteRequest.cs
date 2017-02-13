

namespace Tn.DeviceManager.Measurements
{
    using System;
    using System.Collections.Generic;

    public class WriteRequest
    {
        public WriteRequest(string name, ICollection<Point> points)
        {
            if (string.IsNullOrEmpty(name))
                throw new ArgumentNullException(nameof(name));

            Name = name;
            Points = points ?? Array.Empty<Point>();
        }

        public string Name { get; }

        public ICollection<Point> Points { get; }
    }
}
