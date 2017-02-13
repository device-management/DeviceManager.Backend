
namespace Tn.DeviceManager.Devices
{
    using System;

    public class FilterItem
    {
        public FilterItem(string key, string value, bool exact = true)
        {
            if (string.IsNullOrEmpty(key))
                throw new ArgumentNullException(nameof(key));

            if(string.IsNullOrEmpty(value))
                throw new ArgumentNullException(nameof(value));

            Key = key;
            Value = value;
            Exact = exact;
        }

        public string Key { get; set; }

        public string Value { get; set; }

        public bool Exact { get; set; }
    }
}
