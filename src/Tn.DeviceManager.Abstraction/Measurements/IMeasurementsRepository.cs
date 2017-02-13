
namespace Tn.DeviceManager.Measurements
{
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public interface IMeasurementsRepository
    {
        Task<ICollection<QueryResult>> Query(ICollection<QueryDescriptor> queries);

        Task Write(WriteRequest request);
    }
}
