using System;
using System.Collections.Generic;
using System.Text;

namespace Tn.DeviceManager.Devices
{
    public class FilterResult
    {
        public FilterResult(ICollection<DeviceDescriptor> devices, long total)
        {
            this.Devices = devices ?? Array.Empty<DeviceDescriptor>();
            this.Total = total;
        }

        public ICollection<DeviceDescriptor> Devices { get; }

        public long Total { get; }
    }
}
