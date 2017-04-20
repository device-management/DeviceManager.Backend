

namespace Tn.DeviceManager
{
    using Microsoft.AspNetCore.Builder;
    using Microsoft.AspNetCore.Hosting;
    using Microsoft.Extensions.Configuration;
    using Microsoft.Extensions.DependencyInjection;
    using Microsoft.Extensions.Logging;
    using Newtonsoft.Json;
    using System;
    using System.Linq;
    using System.Threading.Tasks;
    using Tn.DeviceManager.Devices;
    using Tn.DeviceManager.Measurements;
    using Tn.DeviceManager.Services;
    using Tn.Hosting;

    public class Startup
    {
        public Startup(IHostingEnvironment env)
        {
            var builder = new ConfigurationBuilder()
                .SetBasePath(env.ContentRootPath)
                .AddJsonFile("appsettings.json");

            Configuration = builder.Build();
        }

        public IConfigurationRoot Configuration { get; set; }

        // This method gets called by the runtime. Use this method to add services to the container.
        // For more information on how to configure your application, visit https://go.microsoft.com/fwlink/?LinkID=398940
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddCors(options =>
            {
                options.AddPolicy("AllowAllOrigins", builder =>
                {
                    builder.AllowAnyOrigin();
                    builder.AllowAnyMethod();
                    builder.AllowAnyHeader();
                    builder.AllowCredentials();
                });
            });

            services.AddOptions();
            services.Configure<MongoDbSettings>(Configuration.GetSection("mongodb"));
            services.Configure<InfluxDbSettings>(Configuration.GetSection("influxdb"));
            services.Configure<MqttServiceSettings>(Configuration.GetSection("mqtt"));

            var serializerSettings = new JsonSerializerSettings()
            {
                ContractResolver = new SignalRContractResolver()
            };
            var serializer = JsonSerializer.Create(serializerSettings);

            services.Add(new ServiceDescriptor(typeof(JsonSerializer),
                         provider => serializer,
                         ServiceLifetime.Transient));

            services.AddSignalR(options =>
            {
                options.Hubs.EnableDetailedErrors = true;
            });

            services.AddLogging();
            services.AddMvc();
            services.AddDeviceManager();
            services.AddSingleton<ILifecycle, ApplicationRouter>();

        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env, ILoggerFactory loggerFactory, IApplicationLifetime lifetime)
        {
            loggerFactory.AddConsole();

            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseCors("AllowAllOrigins");
            app.UseHttpException();
            app.UseWebSockets();
            app.UseSignalR();
            app.UseMvc();

            lifetime.ApplicationStarted.Register(async() => await StartServices(app, lifetime));
            lifetime.ApplicationStopped.Register(async() => await StopServices(app));
        }

        private async Task StartServices(IApplicationBuilder app, IApplicationLifetime lifetime)
        {
            try
            {
                await Task.WhenAll(app.ApplicationServices.GetServices<ILifecycle>().Select(lifecycle => lifecycle.Start()));
            }
            catch(Exception)
            {
                lifetime.StopApplication();
            }
        }

        private async Task StopServices(IApplicationBuilder app)
        {
            await Task.WhenAll(app.ApplicationServices.GetServices<ILifecycle>().Select(lifecycle => lifecycle.Stop()));
        }
    }
}
