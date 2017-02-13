namespace Tn.DeviceManager.EventDriven
{
    using System;

    public interface IMessageObserver
    {
        IObservable<T> Observe<T>();
    }
}
