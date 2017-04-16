
namespace Tn.DeviceManager
{
    using System;

    public class ChannelEvent
    {
        public ChannelEvent(string channelName, string eventName, object data)
        {
            if (string.IsNullOrEmpty(channelName))
                throw new ArgumentNullException(nameof(channelName));

            if (string.IsNullOrEmpty(eventName))
                throw new ArgumentNullException(nameof(eventName));

            ChannelName = channelName;
            EventName = eventName;
            Data = data ?? throw new ArgumentNullException(nameof(data));
        }

        public string ChannelName { get; }

        public string EventName { get; }

        public object Data { get; }
    }
}
