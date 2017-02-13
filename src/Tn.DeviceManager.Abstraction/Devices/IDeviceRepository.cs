
namespace Tn.DeviceManager.Devices
{
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public interface IDeviceRepository
    {
        Task<ICollection<DeviceDescriptor>> Filter(FilterDescriptor filterDescriptor);

        Task Insert(DeviceDescriptor deviceDescriptor);

        Task Update(DeviceDescriptor deviceDescriptor);
    }
}
