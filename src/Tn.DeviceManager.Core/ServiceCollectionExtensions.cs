


namespace Tn.DeviceManager
{
    using Microsoft.Extensions.DependencyInjection;
    using System;

    using Devices;
    using EventDriven;
    using Measurements;
    using Serialization;
    using Services;
    using Hosting;
    using System.Net.Http;

    public static class ServiceCollectionExtensions
    {
        public static IServiceCollection AddDeviceManager(this IServiceCollection serviceCollection)
        {
            if (serviceCollection == null)
                throw new ArgumentNullException(nameof(serviceCollection));

            serviceCollection.AddSingleton<MongoDbRepositoryFactory>();
            serviceCollection.AddSingleton<IDeviceRepository>(serviceProvider =>
            {
                var factory = serviceProvider.GetRequiredService<MongoDbRepositoryFactory>();
                return factory.CreateRepository();
            });

            serviceCollection.AddSingleton<IMessageBus, MessageBus>();
            serviceCollection.AddSingleton<IMessagePublisher>(serviceProvider => serviceProvider.GetRequiredService<IMessageBus>());
            serviceCollection.AddSingleton<IMessageObserver>(serviceProvider => serviceProvider.GetRequiredService<IMessageBus>());

            serviceCollection.AddSingleton<HttpClient>();
            serviceCollection.AddSingleton<InfluxDbRepositoryFactory>();
            serviceCollection.AddSingleton<IMeasurementsRepository>(serviceProvider =>
            {
                var factory = serviceProvider.GetRequiredService<InfluxDbRepositoryFactory>();
                return factory.CreateRepository(true);
            });

            serviceCollection.AddSingleton<IObjectSerializer, JsonSerializer>();

            serviceCollection.AddSingleton<MqttServiceFactory>();
            serviceCollection.AddSingleton<ILifecycle>(serviceProvider =>
            {
                var factory = serviceProvider.GetRequiredService<MqttServiceFactory>();
                return factory.CreateService();
            });

            return serviceCollection;
        }
    }
}
