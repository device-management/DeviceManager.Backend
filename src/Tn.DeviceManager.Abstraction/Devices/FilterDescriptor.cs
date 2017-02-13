
namespace Tn.DeviceManager.Devices
{
    using System;
    using System.Collections.Generic;

    public class FilterDescriptor
    {
        public FilterDescriptor(ICollection<FilterItem> filters, FilteringLogic logic = FilteringLogic.All, int? limit = null, int? offset = null)
        {
            Filters = filters ?? Array.Empty<FilterItem>();
            Logic = logic;
            Limit = limit;
            Offset = offset;
        }

        public FilterDescriptor(int? limit = null, int? offset = null)
        {
            Filters = Array.Empty<FilterItem>();
            Limit = limit;
            Offset = offset;
        }

        public ICollection<FilterItem> Filters { get; }

        public FilteringLogic Logic { get; }

        public int? Limit { get; set; }

        public int? Offset { get; set; }
    }
}
