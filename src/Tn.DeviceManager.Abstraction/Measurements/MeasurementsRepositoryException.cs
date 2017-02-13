

namespace Tn.DeviceManager.Measurements
{
    using System;

    public class MeasurementsRepositoryException : Exception
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="MeasurementsRepositoryException"/> class.
        /// </summary>
        public MeasurementsRepositoryException()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="MeasurementsRepositoryException"/> class with a specified
        /// error message.
        /// </summary>
        /// <param name="message">
        /// The error message that explains the reason for the exception.
        /// </param>
        public MeasurementsRepositoryException(string message)
            : base(message)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="MeasurementsRepositoryException"/> class with a specified
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
        public MeasurementsRepositoryException(string message, Exception innerException)
            : base(message, innerException)
        {
        }
    }
}
