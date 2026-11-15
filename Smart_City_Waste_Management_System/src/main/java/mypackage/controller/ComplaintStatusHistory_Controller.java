
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.ComplaintStatusHistory;
import mypackage.service.ComplaintStatusHistory_Service;

@RestController
@RequestMapping("/api/complaint-status-history")
public class ComplaintStatusHistory_Controller {

    @Autowired
    private ComplaintStatusHistory_Service complaintStatusHistoryService;

    // Insert new complaint status history
    @PostMapping
    public ComplaintStatusHistory saveComplaintStatusHistory(
            @RequestBody ComplaintStatusHistory complaintStatusHistory) {

        return complaintStatusHistoryService.saveComplaintStatusHistory(
                complaintStatusHistory);
    }

    // Get all complaint status histories
    @GetMapping
    public List<ComplaintStatusHistory> getAllComplaintStatusHistories() {

        return complaintStatusHistoryService.getAllComplaintStatusHistories();
    }

    // Get complaint status history by ID
    @GetMapping("/{historyId}")
    public ResponseEntity<ComplaintStatusHistory> getComplaintStatusHistoryById(
            @PathVariable Long historyId) {

        return complaintStatusHistoryService
                .getComplaintStatusHistoryById(historyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update complaint status history by ID
    @PutMapping("/{historyId}")
    public ResponseEntity<ComplaintStatusHistory> updateComplaintStatusHistory(
            @PathVariable Long historyId,
            @RequestBody ComplaintStatusHistory complaintStatusHistory) {

        return complaintStatusHistoryService
                .getComplaintStatusHistoryById(historyId)
                .map(existingHistory -> {

                    // Preserve primary key
                    complaintStatusHistory.setHistoryId(historyId);

                    // Preserve existing changedAt
                    if (complaintStatusHistory.getChangedAt() == null) {
                        complaintStatusHistory.setChangedAt(
                                existingHistory.getChangedAt());
                    }

                    return ResponseEntity.ok(
                            complaintStatusHistoryService
                                    .saveComplaintStatusHistory(
                                            complaintStatusHistory));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete complaint status history by ID
    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> deleteComplaintStatusHistory(
            @PathVariable Long historyId) {

        return complaintStatusHistoryService
                .getComplaintStatusHistoryById(historyId)
                .map(existingHistory -> {

                    complaintStatusHistoryService
                            .deleteComplaintStatusHistory(historyId);

                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
