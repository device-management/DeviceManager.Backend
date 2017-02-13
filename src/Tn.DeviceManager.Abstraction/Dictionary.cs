
namespace Tn.DeviceManager
{
    using System.Collections.Generic;
    using System.Collections.ObjectModel;
    public static class Dictionary
    {
        private static IReadOnlyDictionary<string, object> EmptyDictionary = new ReadOnlyDictionary<string, object>(new Dictionary<string, object>());

        public static IReadOnlyDictionary<string, object> EmptyReadOnly()
        {
            return EmptyDictionary;
        }
    }
}
