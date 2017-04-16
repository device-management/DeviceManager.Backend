
namespace Tn.DeviceManager.Devices
{
    using System.Threading.Tasks;

    public interface IDeviceRepository
    {
        Task<FilterResult> Filter(FilterDescriptor filterDescriptor);

        Task InsertOrUpdate(DeviceDescriptor deviceDescriptor);
    }
}
