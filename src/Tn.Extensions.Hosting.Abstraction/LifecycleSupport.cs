
namespace Tn.Hosting
{
    using System;
    using System.Threading;
    using System.Threading.Tasks;

    /// <summary>
    /// An implementation of application's managed service.
    /// </summary>
    /// <remarks>
    /// This class is thread-safe.
    /// </remarks>
    public abstract class LifecycleSupport : ILifecycle, IDisposable
    {
        /// <summary>
        /// Track whether Dispose has been called.
        /// </summary>
        private bool _disposed = false;

        /// <summary>
        /// A semaphore for Service synchronization.
        /// </summary>
        private readonly SemaphoreSlim _semaphore = new SemaphoreSlim(1);

        /// <summary>
        /// Gets the state of current service.
        /// </summary>
        public LifecycleState LifecycleState { get; private set; } = LifecycleState.Stopped;

        /// <summary>
        /// Starts this instance of a service.
        /// </summary>
        /// <returns>A task representing starting procedure.</returns>
        public virtual async Task Start()
        {
            if (this.LifecycleState == LifecycleState.Started)
            {
                return;
            }

            await this._semaphore.WaitAsync();
            if (this.LifecycleState == LifecycleState.Started)
            {
                return;
            }

            this.LifecycleState = LifecycleState.Starting;

            try
            {
                await this.DoStart();
                this.LifecycleState = LifecycleState.Started;
            }
            catch (Exception startException)
            {
                if (this.ShouldStopOn(startException))
                {
                    try
                    {
                        await this.StopInternal();
                    }
                    catch (Exception stopException)
                    {
                        throw new AggregateException(startException, stopException);
                    }
                }

                throw;
            }
            finally
            {
                this._semaphore.Release();
            }
        }

        /// <summary>
        /// Stops this instance of a service.
        /// </summary>
        /// <returns>A task representing stopping procedure.</returns>
        public virtual async Task Stop()
        {
            if (this.LifecycleState == LifecycleState.Stopped)
            {
                return;
            }

            await this._semaphore.WaitAsync();

            if (this.LifecycleState == LifecycleState.Stopped)
            {
                return;
            }

            try
            {
                await this.StopInternal();
            }
            finally
            {
                this._semaphore.Release();
            } 
        }

        /// <summary>
        /// Implement IDisposable.
        /// </summary>
        public void Dispose()
        {
            this.Dispose(true);
            GC.SuppressFinalize(this);
        }

        /// <summary>
        /// Overloaded implementation of Dispose.
        /// </summary>
        /// <param name="disposing">True to release both managed and unmanaged resources; 
        /// false to release only unmanaged resources.</param>
        protected virtual void Dispose(bool disposing)
        {
            // Check to see if Dispose has already been called.
            if (!this._disposed)
            {
                // If disposing equals true, dispose all managed
                // and unmanaged resources.
                if (disposing)
                {
                    // Dispose managed resources.
                    this._semaphore.Dispose();
                }

                // Dispose unmanaged resources.

                // Note disposing has been done.
                this._disposed = true;
            }
        }

        /// <summary>
        /// Determines whether to stop the service in case of an exception during start.
        /// </summary>
        /// <param name="startException">
        /// An exception occurred during start procedure.
        /// </param>
        /// <returns>
        /// A value indicating whether perform stop in case of exception.
        /// </returns>
        protected virtual bool ShouldStopOn(Exception startException)
        {
            return true;
        }

        /// <summary>
        /// When implemented in derived class, perform service's starting procedure.
        /// </summary>
        /// <returns>A task representing starting procedure.</returns>
        protected abstract Task DoStart();

        /// <summary>
        /// When implemented in derived class, perform service's stopping procedure.
        /// </summary>
        /// <returns>A task representing stopping procedure.</returns>
        protected abstract Task DoStop();

        /// <summary>
        /// Internally stops service.
        /// </summary>
        /// <returns>A task representing stopping procedure.</returns>
        private async Task StopInternal()
        {
            try
            {
                this.LifecycleState = LifecycleState.Stopping;
                await this.DoStop();
            }
            finally
            {
                this.LifecycleState = LifecycleState.Stopped;
            }
        }
    }
}
