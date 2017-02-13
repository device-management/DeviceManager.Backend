
namespace Tn.DeviceManager.Measurements
{
    using System.Net.Http;
    using System.Threading.Tasks;
    using Tn.DeviceManager.Measurements;

    public static class HttpResponseMessageExtensions
    {
        public static async Task EnsureSuccessStatusCodeAsync(this HttpResponseMessage response)
        {
            if (response.IsSuccessStatusCode)
            {
                return;
            }

            using (response.Content)
            {
                var content = await response.Content.ReadAsStringAsync();
                throw new HttpException(response.StatusCode, content);
            }
        }
    }
}
