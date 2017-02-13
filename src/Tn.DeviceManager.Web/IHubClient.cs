
namespace Tn.DeviceManager
{
    using Tn.DeviceManager.Devices;
    using Tn.DeviceManager.EventDriven.Events;

    public interface IHubClient
    {
        void DeviceUpdated(DeviceDescriptor deviceDescriptor);

        void DeviceRegistered(DeviceDescriptor deviceDescriptor);

        void MeasurementOccured(MeasurementEvent measurment);
    }
}
