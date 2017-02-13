
namespace Tn.Hosting
{ 
    using System.Threading.Tasks;

    /// <summary>
    /// Represents a lifecycle.
    /// </summary>
    public interface ILifecycle
    {
        /// <summary>
        /// Gets the state of this service.
        /// </summary>
        LifecycleState LifecycleState { get; }

        /// <summary>
        /// Starts this instance of a lifecycle.
        /// </summary>
        /// <param name="token">A cancellation token.</param>
        /// <returns>A task representing the starting procedure.</returns>
        Task Start();

        /// <summary>
        /// Stops this instance of a lifecycle.
        /// </summary>
        /// <returns>A task representing the stopping procedure.</returns>
        Task Stop();
    }
}
