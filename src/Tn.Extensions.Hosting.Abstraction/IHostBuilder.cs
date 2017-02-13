
namespace Tn.Hosting
{
    using Microsoft.Extensions.DependencyInjection;
    using System;

    public interface IHostBuilder
    {
        IHost Build();

        IHostBuilder ConfigureServices(Action<IServiceCollection> configureServices);
    }
}
