

namespace Tn.DeviceManager.Controllers
{
    using Microsoft.AspNetCore.Mvc;
    using System;
    using System.Collections.Generic;
    using System.Net;
    using System.Threading.Tasks;
    using Tn.DeviceManager.Measurements;

    [Route("api/[controller]")]
    public class MeasurementsController : Controller
    {
        private IMeasurementsRepository _measurementsRepository;

        public MeasurementsController(IMeasurementsRepository measurementsRepository)
        {
            _measurementsRepository = measurementsRepository ?? throw new ArgumentNullException(nameof(measurementsRepository));
        }

        [HttpPost("query")]
        public Task<ICollection<QueryResult>> Query([FromBody]ICollection<QueryDescriptor> queries)
        {
            try
            {
                return _measurementsRepository.Query(queries);
            }
            catch(ArgumentException ex)
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
