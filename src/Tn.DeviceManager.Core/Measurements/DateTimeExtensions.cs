

namespace Tn.DeviceManager.Measurements
{
    using System;

    public static class DateTimeExtensions
    {
        private static DateTime Epoch = new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc);

        public static long ToUnixNanoseconds(this DateTime date)
        {
            return (date - Epoch).Ticks * 100;
        }
    }
}
