
namespace Tn.DeviceManager.EventDriven
{
    using System;
    using System.Reactive.Linq;
    using System.Reactive.Subjects;

    public class MessageBus : IMessageBus, IDisposable
    {
        private Subject<object> _subject;

        public MessageBus()
        {
            _subject = new Subject<object>();
        }

        public IObservable<T> Observe<T>()
        {
            return _subject.OfType<T>();
        }

        public void Publish(object message)
        {
            _subject.OnNext(message);
        }

        public void Dispose()
        {
            _subject.Dispose();
        }
    }
}
