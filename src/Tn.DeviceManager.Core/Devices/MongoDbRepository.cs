
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

        public MongoDbRepository(IMongoCollection<BsonDocument> collection, IObjectSerializer serializer, MongoDbSettings settings)
        {
            _collection = collection ?? throw new ArgumentNullException(nameof(collection));
            _serializer = serializer ?? throw new ArgumentNullException(nameof(serializer));
            _settings = settings ?? throw new ArgumentNullException(nameof(settings));
        }

        public async Task<FilterResult> Filter(FilterDescriptor filterDescriptor)
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
                long total = await findFluent.CountAsync();
                return new FilterResult(
                    result.Select(item => (DeviceDescriptor)_serializer.DeserializeObject(item.ToJson(jsonWriterSettings), typeof(DeviceDescriptor))).ToArray(),
                    total);
            }
            catch (Exception ex)
            {
                throw new DeviceRepositoryException("Cannot get devices from database.", ex);
            }
        }

        public async Task InsertOrUpdate(DeviceDescriptor deviceDescriptor)
        {
            var filter = Builders<BsonDocument>.Filter.Eq(ToCamelCase(nameof(deviceDescriptor.DeviceId)), deviceDescriptor.DeviceId);

            try
            {

                var update = Builders<BsonDocument>.Update.Combine(deviceDescriptor.Properties.Select(entry =>
                Builders<BsonDocument>.Update.Set(ToDotNotation(nameof(deviceDescriptor.Properties), entry.Key), entry.Value)).ToArray());
                UpdateResult result = await _collection.UpdateOneAsync(filter, update, new UpdateOptions { IsUpsert = true });
            }
            catch (Exception ex)
            {
                throw new DeviceRepositoryException("Cannot update device in database.", ex);
            }
        }

        private string ToDotNotation(string key, string value)
        {
            return $"{ToCamelCase(key)}.{ToCamelCase(value)}";
        }

        private static FilterDefinition<BsonDocument> ParseFilter(FilterItem filter)
        {
            return filter.Exact ? Builders<BsonDocument>.Filter.Eq(filter.Key, filter.Value)
                                : Builders<BsonDocument>.Filter.Regex(filter.Key, filter.Value);
        }

        private static string ToCamelCase(string str)
        {
            return Char.ToLowerInvariant(str[0]) + str.Substring(1);
        }
    }
}
