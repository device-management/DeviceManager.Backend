
namespace Tn.DeviceManager.Serialization
{
    using System;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Serialization;

    /// <summary>
    /// Serializes and deserializes objects as json strings.
    /// </summary>
    public class JsonSerializer : IObjectSerializer
    {
        /// <summary>
        /// Serializer settings.
        /// </summary>
        private readonly JsonSerializerSettings _jsonSerializerSettings;

        /// <summary>
        /// Initializes a new instance of the <see cref="JsonSerializer"/> class.
        /// </summary>
        public JsonSerializer()
        {
            this._jsonSerializerSettings = new JsonSerializerSettings()
            {
                MissingMemberHandling = MissingMemberHandling.Ignore,
                ContractResolver = new CamelCasePropertyNamesContractResolver()
            };
        }

        /// <inheritdoc />
        public string SerializeObject(object value)
        {
            if (value == null)
                throw new ArgumentNullException(nameof(value));

            try
            {
                return JsonConvert.SerializeObject(value, _jsonSerializerSettings);
            }
            catch(Exception ex)
            {
                throw new SerializationException("Cannot serialize an object.", ex);
            }
        }

        /// <inheritdoc />
        public object DeserializeObject(string objAsString, Type type)
        {
            if (string.IsNullOrEmpty(objAsString))
                throw new ArgumentNullException(nameof(objAsString));

            if (type == null)
                throw new ArgumentNullException(nameof(type));

            try
            {
                return JsonConvert.DeserializeObject(objAsString, type, _jsonSerializerSettings);
            }
            catch (Exception ex)
            {
                throw new SerializationException("Cannot deserialize an object.", ex);
            }
            
        }
    }
}
