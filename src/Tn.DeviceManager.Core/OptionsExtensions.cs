
namespace Tn.DeviceManager
{
    using Microsoft.Extensions.Options;
    using System;

    internal static class OptionsExtensions
    {
        public static void EnsureValueNotNull<TOptions>(this IOptions<TOptions> options) where TOptions : class, new()
        {
            if(options.Value == null)
            {
                throw new ArgumentNullException(nameof(options.Value), "The options value is null.");
            }
        }
    }
}
