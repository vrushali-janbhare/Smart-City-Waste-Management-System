
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.BulkPickupRequest;
import mypackage.service.BulkPickupRequest_Service;

@RestController
@RequestMapping("/api/bulk-pickup-requests")
public class BulkPickupRequest_Controller {

    @Autowired
    private BulkPickupRequest_Service bulkPickupRequestService;

    // Insert new bulk pickup request
    @PostMapping
    public BulkPickupRequest saveBulkPickupRequest(
            @RequestBody BulkPickupRequest bulkPickupRequest) {

        return bulkPickupRequestService.saveBulkPickupRequest(bulkPickupRequest);
    }

    // Get all bulk pickup requests
    @GetMapping
    public List<BulkPickupRequest> getAllBulkPickupRequests() {
        return bulkPickupRequestService.getAllBulkPickupRequests();
    }

    // Get bulk pickup request by ID
    @GetMapping("/{requestId}")
    public ResponseEntity<BulkPickupRequest> getBulkPickupRequestById(
            @PathVariable Long requestId) {

        return bulkPickupRequestService.getBulkPickupRequestById(requestId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update bulk pickup request by ID
    @PutMapping("/{requestId}")
    public ResponseEntity<BulkPickupRequest> updateBulkPickupRequest(
            @PathVariable Long requestId,
            @RequestBody BulkPickupRequest bulkPickupRequest) {

        return bulkPickupRequestService.getBulkPickupRequestById(requestId)
                .map(existingRequest -> {
                    bulkPickupRequest.setRequestId(requestId);
                    return ResponseEntity.ok(
                            bulkPickupRequestService.saveBulkPickupRequest(
                                    bulkPickupRequest));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete bulk pickup request by ID
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteBulkPickupRequest(
            @PathVariable Long requestId) {

        return bulkPickupRequestService.getBulkPickupRequestById(requestId)
                .map(existingRequest -> {
                    bulkPickupRequestService.deleteBulkPickupRequest(requestId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
