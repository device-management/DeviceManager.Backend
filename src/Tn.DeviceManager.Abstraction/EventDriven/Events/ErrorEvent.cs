
namespace Tn.DeviceManager.EventDriven.Events
{
    using System;

    public class ErrorEvent
    {
        public ErrorEvent(string message)
        {
            if (string.IsNullOrEmpty(message))
                throw new ArgumentNullException(nameof(message));

            this.Message = message;
        }

        public string Message { get; }
    }
}
