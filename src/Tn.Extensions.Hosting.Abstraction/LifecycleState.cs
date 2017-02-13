
namespace Tn.Hosting
{
    /// <summary>
    /// Represents the state of objects implementing the <see cref="ILifecycle"/> interface.
    /// </summary>
    public enum LifecycleState
    {
        /// <summary>
        /// The service is currently being started.
        /// </summary>
        Starting = 0,

        /// <summary>
        /// The service is started and operational.
        /// </summary>
        Started = 1,

        /// <summary>
        /// The service is currently being stopped.
        /// </summary>
        Stopping = 2,

        /// <summary>
        /// The service is stopped.
        /// </summary>
        Stopped = 3
    }
}
