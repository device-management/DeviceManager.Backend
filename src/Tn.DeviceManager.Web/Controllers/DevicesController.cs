
namespace Tn.DeviceManager.Controllers
{
    using Microsoft.AspNetCore.Mvc;
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Net;
    using System.Threading.Tasks;
    using Tn.DeviceManager.Devices;
    using Tn.DeviceManager.EventDriven;
    using Tn.DeviceManager.EventDriven.Events;
    using Tn.DeviceManager.Measurements;

    [Route("api/[controller]")]
    public class DevicesController : Controller
    {
        private IDeviceRepository _deviceRepository;

        private IMessagePublisher _messagePublisher;

        private IMeasurementsRepository _measurementsRepository;

        public DevicesController(IDeviceRepository deviceRepository, IMessagePublisher messagePublisher, IMeasurementsRepository measurementsRepository)
        {
            _deviceRepository = deviceRepository ?? throw new ArgumentNullException(nameof(deviceRepository));
            _messagePublisher = messagePublisher ?? throw new ArgumentNullException(nameof(messagePublisher));
            _measurementsRepository = measurementsRepository ?? throw new ArgumentNullException(nameof(measurementsRepository));
        }

        [HttpGet]
        public async Task<FilterResult> Get(int? limit = null, int? offset = null)
        {
            var filter = new FilterDescriptor();// new FilterDescriptor(limit, offset);

            try
            {
                return await _deviceRepository.Filter(filter);
            }
            catch (ArgumentException ex)
            {
                throw new HttpException(HttpStatusCode.BadRequest, "Bad request parameters.", ex);
            }
            catch (DeviceRepositoryException ex)
            {
                throw new HttpException(HttpStatusCode.InternalServerError, "Cannot get devices from repository.", ex);
            }
        }

        [HttpGet("{deviceId}")]
        public async Task<DeviceDescriptor> Get(string deviceId)
        {
            //var filter = new FilterDescriptor(new[] { new FilterItem(FilterConstants.DeviceId, deviceId) });
            var filter = new FilterDescriptor();

            try
            {
                FilterResult devices = await _deviceRepository.Filter(filter);
                DeviceDescriptor device = devices.Devices.FirstOrDefault();
                if (device == null)
                {
                    throw new HttpException(HttpStatusCode.NotFound, $"Device identifier {deviceId} not found.");
                }

                return device;
            }
            catch(ArgumentException ex)
            {
                throw new HttpException(HttpStatusCode.BadRequest, "Bad request parameters.", ex);
            }
            catch (DeviceRepositoryException ex)
            {
                throw new HttpException(HttpStatusCode.InternalServerError, "Cannot get device from repository.", ex);
            }
        }

        [HttpPost("find")]
        public async Task<FilterResult> Find([FromBody]FilterDescriptor filter)
        {
            try
            {
                return await _deviceRepository.Filter(filter);
            }
            catch (ArgumentException ex)
            {
                throw new HttpException(HttpStatusCode.BadRequest, "Bad request parameters.", ex);
            }
            catch (DeviceRepositoryException ex)
            {
                throw new HttpException(HttpStatusCode.InternalServerError, "Cannot find devices in repository.", ex);
            }
        }

        [HttpPost("{deviceId}")]
        public void Command(string deviceId, [FromBody]IReadOnlyDictionary<string, object> fields)
        {
            _messagePublisher.Publish(new CommandEvent(deviceId, fields));
        }

        [HttpGet("{deviceId}/measurements")]
        public async Task<QueryResult> GetMeasurements(string deviceId, DateTime? dateFrom = null, DateTime? dateTo = null)
        {
            try
            {
                ICollection<QueryResult> queryResults = await _measurementsRepository.Query(new[] { new QueryDescriptor(deviceId, dateFrom, dateTo) });

                QueryResult queryResult = queryResults.FirstOrDefault();
                if (queryResult == null)
                {
                    throw new HttpException(HttpStatusCode.NotFound, $"Measurements for device identifier {deviceId} not found.");
                }

                return queryResult;
            }
            catch (ArgumentException ex)
            {
                throw new HttpException(HttpStatusCode.BadRequest, "Bad request parameters.", ex);
            }
            catch (MeasurementsRepositoryException ex)
            {
                throw new HttpException(HttpStatusCode.InternalServerError, "Cannot query measurements from repository.", ex);
            }
        }
    }
}
