
namespace Tn.DeviceManager.EventDriven
{
    public interface IMessagePublisher
    {
        void Publish(object message);
    }
}
