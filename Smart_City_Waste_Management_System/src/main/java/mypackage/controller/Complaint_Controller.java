
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Complaint;
import mypackage.service.Complaint_Service;

@RestController
@RequestMapping("/api/complaints")
public class Complaint_Controller {

    @Autowired
    private Complaint_Service complaintService;

    // Insert new complaint
    @PostMapping
    public Complaint saveComplaint(@RequestBody Complaint complaint) {
        return complaintService.saveComplaint(complaint);
    }

    // Get all complaints
    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    // Get complaint by ID
    @GetMapping("/{complaintId}")
    public ResponseEntity<Complaint> getComplaintById(
            @PathVariable Long complaintId) {

        return complaintService.getComplaintById(complaintId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update complaint by ID
    @PutMapping("/{complaintId}")
    public ResponseEntity<Complaint> updateComplaint(
            @PathVariable Long complaintId,
            @RequestBody Complaint complaint) {

        return complaintService.getComplaintById(complaintId)
                .map(existingComplaint -> {

                    // Preserve complaint ID
                    complaint.setComplaintId(complaintId);

                    // Preserve original creation date
                    if (complaint.getCreatedAt() == null) {
                        complaint.setCreatedAt(
                                existingComplaint.getCreatedAt());
                    }

                    // Preserve resolution date if not provided
                    if (complaint.getResolvedAt() == null) {
                        complaint.setResolvedAt(
                                existingComplaint.getResolvedAt());
                    }

                    return ResponseEntity.ok(
                            complaintService.saveComplaint(complaint));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete complaint by ID
    @DeleteMapping("/{complaintId}")
    public ResponseEntity<Void> deleteComplaint(
            @PathVariable Long complaintId) {

        return complaintService.getComplaintById(complaintId)
                .map(existingComplaint -> {
                    complaintService.deleteComplaint(complaintId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
