
namespace Tn.DeviceManager
{
    using Microsoft.AspNetCore.Builder;

    public static class ApplicationBuilderExtensions
    {
        public static IApplicationBuilder UseHttpException(this IApplicationBuilder app)
        {
            return app.UseMiddleware<HttpExceptionMiddleware>();
        }
    }
}
