
namespace Tn.DeviceManager
{
    using Microsoft.AspNetCore.SignalR;
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Tn.DeviceManager.Devices;
    using Tn.DeviceManager.EventDriven;
    using Tn.DeviceManager.EventDriven.Events;
    using Tn.DeviceManager.Measurements;
    using Tn.Hosting;

    public class ApplicationRouter : LifecycleSupport
    {
        private readonly IMessageBus _messageBus;
        private readonly IHubContext<DeviceManagerHub, IHubClient> _hubContext;
        private readonly IDeviceRepository _deviceRepository;
        private readonly IMeasurementsRepository _measurementsRepository;

        private readonly Queue<IDisposable> _subscriptions;

        public ApplicationRouter(IMessageBus messageBus, IHubContext<DeviceManagerHub, IHubClient> hubContext, IDeviceRepository deviceRepository, IMeasurementsRepository measurementsRepository)
        {
            _messageBus = messageBus ?? throw new ArgumentNullException(nameof(messageBus));
            _hubContext = hubContext ?? throw new ArgumentNullException(nameof(hubContext));
            _deviceRepository = deviceRepository ?? throw new ArgumentNullException(nameof(deviceRepository));
            _measurementsRepository = measurementsRepository ?? throw new ArgumentNullException(nameof(measurementsRepository));
            _subscriptions = new Queue<IDisposable>();
        }

        protected override Task DoStart()
        {
            _subscriptions.Enqueue(_messageBus.Observe<MeasurementEvent>().Subscribe(async(@event) => await HandleMeasurementEvent(@event)));
            _subscriptions.Enqueue(_messageBus.Observe<RegisterEvent>().Subscribe(async(@event) => await HandleRegisterEvent(@event)));
            _subscriptions.Enqueue(_messageBus.Observe<PropertiesChangedEvent>().Subscribe(async(@event) => await HandleProperiesChangedEvent(@event)));

            return Task.FromResult(0);
        }

        protected override Task DoStop()
        {
            while(_subscriptions.Count > 0)
            {
                _subscriptions.Dequeue().Dispose();
            }

            return Task.FromResult(0);
        }

        private async Task HandleMeasurementEvent(MeasurementEvent measurementEvent)
        {
            await _measurementsRepository.Write(new WriteRequest(measurementEvent.DeviceId, measurementEvent.Points));
            var channelEvent = new ChannelEvent(measurementEvent.DeviceId, Events.MeasurementOccured, measurementEvent);
            _hubContext.Clients.Group(measurementEvent.DeviceId).EventArrived(channelEvent);
        }

        private async Task HandleRegisterEvent(RegisterEvent registerEvent)
        {
            var device = new DeviceDescriptor(registerEvent.DeviceId, registerEvent.Properties);
            await _deviceRepository.InsertOrUpdate(device);
            var channelEvent = new ChannelEvent(registerEvent.DeviceId, Events.DeviceRegistered, registerEvent);
            _hubContext.Clients.Group(device.DeviceId).EventArrived(channelEvent);
        }

        private async Task HandleProperiesChangedEvent(PropertiesChangedEvent propertiesChangedEvent)
        {
            var device = new DeviceDescriptor(propertiesChangedEvent.DeviceId, propertiesChangedEvent.Properties);
            await _deviceRepository.InsertOrUpdate(device);
            var channelEvent = new ChannelEvent(propertiesChangedEvent.DeviceId, Events.DeviceUpdated, propertiesChangedEvent);
            _hubContext.Clients.Group(device.DeviceId).EventArrived(channelEvent);
        }
    }
}
