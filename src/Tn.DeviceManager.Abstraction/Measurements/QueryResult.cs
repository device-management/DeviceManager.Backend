
namespace Tn.DeviceManager.Measurements
{
    using System;
    using System.Collections.Generic;

    public class QueryResult
    {
        public QueryResult(string name, ICollection<Point> points)
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
