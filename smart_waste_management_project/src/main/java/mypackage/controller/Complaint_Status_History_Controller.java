package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Complaint_Status_History;
import mypackage.service.Complaint_Status_History_Service;

@RestController
@RequestMapping("/api/complaint-status-history")
public class Complaint_Status_History_Controller {

    @Autowired
    private Complaint_Status_History_Service historyService;

    // Add Complaint Status History
    @PostMapping
    public Complaint_Status_History addHistory(
            @RequestBody Complaint_Status_History history) {

        return historyService.addHistory(history);
    }

    // Get All History
    @GetMapping
    public List<Complaint_Status_History> getAllHistory() {
        return historyService.getAllHistory();
    }

    // Get History By ID
    @GetMapping("/{id}")
    public Complaint_Status_History getHistoryById(@PathVariable Long id) {
        return historyService.getHistoryById(id);
    }

    // Update History
    @PutMapping("/{id}")
    public Complaint_Status_History updateHistory(
            @PathVariable Long id,
            @RequestBody Complaint_Status_History history) {

        return historyService.updateHistory(id, history);
    }

    // Delete History
    @DeleteMapping("/{id}")
    public String deleteHistory(@PathVariable Long id) {

        historyService.deleteHistory(id);

        return "Complaint Status History Deleted Successfully!";
    }
}