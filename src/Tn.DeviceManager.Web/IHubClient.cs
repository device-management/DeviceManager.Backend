
namespace Tn.DeviceManager
{
    using Tn.DeviceManager.Devices;
    using Tn.DeviceManager.EventDriven.Events;

    public interface IHubClient
    {
        void EventArrived(ChannelEvent channelEvent);
    }
}
