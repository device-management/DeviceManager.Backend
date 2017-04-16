

namespace Tn.DeviceManager
{
    using Microsoft.AspNetCore.SignalR;
    using Newtonsoft.Json.Linq;
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Tn.DeviceManager.EventDriven;
    using Tn.DeviceManager.EventDriven.Events;

    public class DeviceManagerHub : Hub
    {
        private readonly IMessagePublisher _publisher;

        private readonly IDictionary<string, Type> _typeDictionary; 

        public DeviceManagerHub(IMessagePublisher publisher)
        {
            _publisher = publisher ?? throw new ArgumentNullException(nameof(publisher));
            _typeDictionary = new Dictionary<string, Type>
            {
                { Events.DeviceCommand, typeof(CommandEvent) }
            };
        }

        public async Task Subscribe(string channel)
        {
            await Groups.Add(Context.ConnectionId, channel);
        }

        public async Task Unsubscribe(string channel)
        {
            await Groups.Remove(Context.ConnectionId, channel);
        }

        public void Publish(ChannelEvent channelEvent)
        {
            if (_typeDictionary.TryGetValue(channelEvent.EventName, out Type eventType))
            {
                var token = (JToken)channelEvent.Data;
                var eventData = token.ToObject(eventType);
                _publisher.Publish(eventData);
            }
            else
            {
                _publisher.Publish(new ErrorEvent("Unknown published message."));
            }
        }
    }
}
