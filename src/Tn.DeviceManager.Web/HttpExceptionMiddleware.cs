
namespace Tn.DeviceManager
{
    using Microsoft.AspNetCore.Http;
    using System;
    using System.Threading.Tasks;

    internal class HttpExceptionMiddleware
    {
        private readonly RequestDelegate next;

        public HttpExceptionMiddleware(RequestDelegate next)
        {
            if (next == null)
                throw new ArgumentNullException(nameof(next));

            this.next = next;
        }

        public async Task Invoke(HttpContext context)
        {
            if (context == null)
                throw new ArgumentNullException(nameof(context));

            try
            {
                await this.next.Invoke(context);
            }
            catch (HttpException httpException)
            {
                context.Response.StatusCode = httpException.StatusCode;
                await context.Response.WriteAsync(httpException.Message);
            }
        }
    }
}
