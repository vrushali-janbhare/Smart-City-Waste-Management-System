package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Complaints;
import mypackage.service.Complaints_Service;

@RestController
@RequestMapping("/api/complaints")
public class Complaints_Controller {

    @Autowired
    private Complaints_Service complaintsService;

    // Add Complaint
    @PostMapping
    public Complaints addComplaint(@RequestBody Complaints complaint) {
        return complaintsService.addComplaint(complaint);
    }

    // Get All Complaints
    @GetMapping
    public List<Complaints> getAllComplaints() {
        return complaintsService.getAllComplaints();
    }

    // Get Complaint By ID
    @GetMapping("/{id}")
    public Complaints getComplaintById(@PathVariable Long id) {
        return complaintsService.getComplaintById(id);
    }

    // Update Complaint
    @PutMapping("/{id}")
    public Complaints updateComplaint(
            @PathVariable Long id,
            @RequestBody Complaints complaint) {

        return complaintsService.updateComplaint(id, complaint);
    }

    // Delete Complaint
    @DeleteMapping("/{id}")
    public String deleteComplaint(@PathVariable Long id) {

        complaintsService.deleteComplaint(id);

        return "Complaint Deleted Successfully!";
    }
}