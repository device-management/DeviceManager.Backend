
namespace Tn.Hosting
{
    using System;

    public interface IHost : ILifecycle, IDisposable
    {
        IServiceProvider Services { get; }
    }
}
