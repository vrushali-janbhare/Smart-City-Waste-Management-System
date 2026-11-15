
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.BulkPickupRequest;
import mypackage.repository.BulkPickupRequest_Repository;

@Service
public class BulkPickupRequest_Service {

    @Autowired
    private BulkPickupRequest_Repository bulkPickupRequestRepository;

    // Insert or update bulk pickup request
    public BulkPickupRequest saveBulkPickupRequest(BulkPickupRequest request) {
        return bulkPickupRequestRepository.save(request);
    }

    // Get all bulk pickup requests
    public List<BulkPickupRequest> getAllBulkPickupRequests() {
        return bulkPickupRequestRepository.findAll();
    }

    // Get bulk pickup request by ID
    public Optional<BulkPickupRequest> getBulkPickupRequestById(Long requestId) {
        return bulkPickupRequestRepository.findById(requestId);
    }

    // Delete bulk pickup request by ID
    public void deleteBulkPickupRequest(Long requestId) {
        bulkPickupRequestRepository.deleteById(requestId);
    }
}
