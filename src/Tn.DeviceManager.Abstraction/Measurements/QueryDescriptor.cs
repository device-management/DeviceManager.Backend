

namespace Tn.DeviceManager.Measurements
{
    using System;

    public class QueryDescriptor
    {
        public QueryDescriptor(string name, DateTime? dateFrom = null, DateTime? dateTo = null)
        {
            if (string.IsNullOrEmpty(name))
                throw new ArgumentNullException(nameof(name));

            Name = name;
            DateFrom = dateFrom;
            DateTo = dateTo;
        }

        public string Name { get; }

        public DateTime? DateFrom { get; }

        public DateTime? DateTo { get; }
    }
}
