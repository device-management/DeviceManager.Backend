
namespace Tn.DeviceManager.Devices
{
    using System;
    using System.Collections.Generic;

    public class FilterDescriptor
    {
        public ICollection<FilterItem> Filters { get; set; }

        public FilteringLogic Logic { get; set; }

        public int? Limit { get; set; }

        public int? Offset { get; set; }
    }
}
