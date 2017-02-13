
namespace Tn.DeviceManager.Devices
{
    using Microsoft.Extensions.Logging;
    using Microsoft.Extensions.Options;
    using MongoDB.Bson;
    using MongoDB.Bson.Serialization.Conventions;
    using MongoDB.Driver;
    using System;
    using Tn.DeviceManager.Serialization;

    public class MongoDbRepositoryFactory
    {
        private readonly IObjectSerializer _serializer;
        private readonly ILogger _logger;

        private readonly MongoDbSettings _settings;

        public MongoDbRepositoryFactory(IObjectSerializer serializer, IOptions<MongoDbSettings> options, ILogger<MongoDbRepositoryFactory> logger)
        {
            if (options == null)
                throw new ArgumentNullException(nameof(options));

            options.EnsureValueNotNull();

            _settings = options.Value;
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
            _serializer = serializer ?? throw new ArgumentNullException(nameof(serializer));
        }

        public MongoDbRepository CreateRepository()
        {
            try
            {
                var pack = new ConventionPack();
                pack.Add(new CamelCaseElementNameConvention());
                ConventionRegistry.Register("CamelCase", pack, t => true);

                var client = new MongoClient(_settings.ConnectionString);
                var database = client.GetDatabase(_settings.DatabaseName);
                var collection = database.GetCollection<BsonDocument>(_settings.DevicesCollectionName);
                //collection.Indexes.CreateOne(Builders<DeviceDescriptor>.IndexKeys.Text(field => field.DeviceId));
                return new MongoDbRepository(collection, _serializer, _settings);
            }
            catch(Exception ex)
            {
                throw new DeviceRepositoryException("Cannot create the mongo db repository.", ex);
            }
        }
    }
}
