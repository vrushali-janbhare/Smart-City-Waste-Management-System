
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Bulk_Pickup_Requests;
import mypackage.service.Bulk_Pickup_Requests_Service;

@RestController
@RequestMapping("/api/bulk-pickup-requests")
public class Bulk_Pickup_Requests_Controller {

    @Autowired
    private Bulk_Pickup_Requests_Service bulkPickupService;

    // Add Request
    @PostMapping
    public Bulk_Pickup_Requests addRequest(
            @RequestBody Bulk_Pickup_Requests request) {

        return bulkPickupService.addRequest(request);
    }

    // Get Request By ID
    @GetMapping("/{id}")
    public Bulk_Pickup_Requests getRequestById(
            @PathVariable Long id) {

        return bulkPickupService.getRequestById(id);
    }

    // Get All Requests
    @GetMapping
    public List<Bulk_Pickup_Requests> getAllRequests() {

        return bulkPickupService.getAllRequests();
    }

    // Update Request
    @PutMapping("/{id}")
    public Bulk_Pickup_Requests updateRequest(
            @PathVariable Long id,
            @RequestBody Bulk_Pickup_Requests request) {

        return bulkPickupService.updateRequest(id, request);
    }

    // Delete Request
    @DeleteMapping("/{id}")
    public String deleteRequest(@PathVariable Long id) {

        return bulkPickupService.deleteRequest(id);
    }
}
