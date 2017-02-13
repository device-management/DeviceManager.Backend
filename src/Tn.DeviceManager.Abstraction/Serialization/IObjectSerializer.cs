

namespace Tn.DeviceManager.Serialization
{
    using System;

    public interface IObjectSerializer
    {
        /// <summary>
        /// Serializes object to string.
        /// </summary>
        /// <param name="value">Object to serialize.</param>
        /// <returns>Serialized object as string.</returns>
        /// <exception cref="SerializationException">
        /// Could not serialize <paramref name="value"/>.
        /// </exception>
        /// <exception cref="ArgumentNullException">
        /// <paramref name="value"/> cannot be <c>null</c>.
        /// </exception>
        string SerializeObject(object value);

        /// <summary>
        /// Deserializes an object from string.
        /// </summary>
        /// <param name="objAsString">The string that holds serialized object's state.</param>
        /// <param name="type">The type to deserialize. Cannot be <c>null</c>.</param>
        /// <returns>
        /// An instance of type <paramref name="type"/> deserialized from the <paramref name="objAsString"/>.
        /// </returns>
        /// <exception cref="SerializationException">
        /// Could not deserialize the object due to a reason lying in either <paramref name="objAsString"/> or <paramref name="type"/>.
        /// </exception>
        /// <exception cref="ArgumentNullException">
        /// <paramref name="objAsString"/> and <paramref name="type"/> cannot be <c>null</c>.
        /// </exception>
        object DeserializeObject(string objAsString, Type type);
    }
}
