
namespace Tn.Hosting
{
    using System;

    /// <summary>
    /// Represents errors occurred druing testing.
    /// </summary>
    public class HostException : Exception
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="HostException"/> class.
        /// </summary>
        public HostException()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="HostException"/> class with a specified
        /// error message.
        /// </summary>
        /// <param name="message">
        /// The error message that explains the reason for the exception.
        /// </param>
        public HostException(string message)
            : base(message)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="HostException"/> class with a specified
        /// error message and a reference to the inner exception that is the cause of
        /// this exception.
        /// </summary>
        /// <param name="message">
        /// The error message that explains the reason for the exception.
        /// </param>
        /// <param name="innerException">
        /// The exception that is the cause of the current exception, or a null reference
        /// (Nothing in Visual Basic) if no inner exception is specified.
        /// </param>
        public HostException(string message, Exception innerException)
            : base(message, innerException)
        {
        }
    }
}
