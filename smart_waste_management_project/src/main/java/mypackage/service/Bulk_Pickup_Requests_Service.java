
package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Bulk_Pickup_Requests;
import mypackage.repository.Bulk_Pickup_Requests_Repository;

@Service
public class Bulk_Pickup_Requests_Service {

    @Autowired
    private Bulk_Pickup_Requests_Repository bulkPickupRepository;

    // Add Bulk Pickup Request
    public Bulk_Pickup_Requests addRequest(Bulk_Pickup_Requests request) {
        return bulkPickupRepository.save(request);
    }

    // Get Request By ID
    public Bulk_Pickup_Requests getRequestById(Long id) {
        return bulkPickupRepository.findById(id).orElse(null);
    }

    // Get All Requests
    public List<Bulk_Pickup_Requests> getAllRequests() {
        return bulkPickupRepository.findAll();
    }

    // Update Request
    public Bulk_Pickup_Requests updateRequest(Long id, Bulk_Pickup_Requests request) {

        Bulk_Pickup_Requests existingRequest =
                bulkPickupRepository.findById(id).orElse(null);

        if (existingRequest != null) {

            existingRequest.setUsers(request.getUsers());
            existingRequest.setAreas(request.getAreas());
            existingRequest.setWaste_description(request.getWaste_description());
            existingRequest.setEstimated_quantity(request.getEstimated_quantity());
            existingRequest.setPickup_address(request.getPickup_address());
            existingRequest.setRequested_date(request.getRequested_date());
            existingRequest.setStatus(request.getStatus());

            return bulkPickupRepository.save(existingRequest);
        }

        return null;
    }

    // Delete Request
    public String deleteRequest(Long id) {

        bulkPickupRepository.deleteById(id);

        return "Bulk Pickup Request Deleted Successfully";
    }
}
