
namespace Tn.DeviceManager.Devices
{
    using MongoDB.Driver;
    using System;
    using System.Linq;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Tn.DeviceManager.Serialization;
    using MongoDB.Bson;
    using MongoDB.Bson.Serialization;
    using MongoDB.Bson.IO;

    public class MongoDbRepository : IDeviceRepository
    {
        private readonly IMongoCollection<BsonDocument> _collection;

        private readonly IObjectSerializer _serializer;

        private readonly MongoDbSettings _settings;

        private readonly JsonWriterSettings jsonWriterSettings = new JsonWriterSettings { OutputMode = JsonOutputMode.Strict };

        public MongoDbRepository(IMongoCollection<BsonDocument> collection,IObjectSerializer serializer, MongoDbSettings settings)
        {
            _collection = collection ?? throw new ArgumentNullException(nameof(collection));
            _serializer = serializer ?? throw new ArgumentNullException(nameof(serializer));
            _settings = settings ?? throw new ArgumentNullException(nameof(settings));
        }

        public async Task<ICollection<DeviceDescriptor>> Filter(FilterDescriptor filterDescriptor)
        {
            FilterDefinitionBuilder<BsonDocument> filterBuilder = Builders<BsonDocument>.Filter;

            IFindFluent<BsonDocument, BsonDocument> findFluent = null;
            if (filterDescriptor.Filters.Any())
            {
                FilterDefinition<BsonDocument> filterDefinition = null;
                switch (filterDescriptor.Logic)
                {
                    case FilteringLogic.All:
                        filterDefinition = filterBuilder.And(filterDescriptor.Filters.Select(filter => ParseFilter(filter)));
                        break;
                    case FilteringLogic.Any:
                        filterDefinition = filterBuilder.Or(filterDescriptor.Filters.Select(filter => ParseFilter(filter)));
                        break;
                    default:
                        throw new NotSupportedException();
                }

                findFluent = _collection.Find(filterDefinition);
            }
            else
            {
                findFluent = _collection.Find(_ => true);
            }

            if (filterDescriptor.Limit.HasValue)
            {
                findFluent.Limit(filterDescriptor.Limit.Value);
            }

            if (filterDescriptor.Offset.HasValue)
            {
                findFluent.Skip(filterDescriptor.Offset.Value);
            }

            try
            {
                List<BsonDocument> result = await findFluent.ToListAsync();
                return result.Select(item => (DeviceDescriptor)_serializer.DeserializeObject(item.ToJson(jsonWriterSettings), typeof(DeviceDescriptor))).ToArray();
            }
            catch (Exception ex)
            {
                throw new DeviceRepositoryException("Cannot get devices from database.", ex);
            }
        }

        public async Task Insert(DeviceDescriptor deviceDescriptor)
        {
            try
            {
                string json = _serializer.SerializeObject(deviceDescriptor);
                await _collection.InsertOneAsync(BsonSerializer.Deserialize<BsonDocument>(json));
            }
            catch (Exception ex)
            {
                throw new DeviceRepositoryException("Cannot insert device into database.", ex);
            }
        }

        public async Task Update(DeviceDescriptor deviceDescriptor)
        {
            var filter = Builders<BsonDocument>.Filter.Eq(ToCamlCase(nameof(deviceDescriptor.DeviceId)), deviceDescriptor.DeviceId);

            try
            {
                string propertiesAsJson = _serializer.SerializeObject(deviceDescriptor.Properties);
                BsonDocument propertiesAsBson = BsonSerializer.Deserialize<BsonDocument>(propertiesAsJson);
                var update = Builders<BsonDocument>.Update.Set(ToCamlCase(nameof(deviceDescriptor.Properties)), propertiesAsBson);
                UpdateResult result = await _collection.UpdateOneAsync(filter, update);
            }
            catch (Exception ex)
            {
                throw new DeviceRepositoryException("Cannot update device in database.", ex);
            }
        }

        private static FilterDefinition<BsonDocument> ParseFilter(FilterItem filter)
        {
            return filter.Exact ? Builders<BsonDocument>.Filter.Eq(filter.Key, filter.Value)
                                : Builders<BsonDocument>.Filter.Regex(filter.Key, filter.Value);
        }

        private static string ToCamlCase(string str)
        {
            return Char.ToLowerInvariant(str[0]) + str.Substring(1);
        }
    }
}
