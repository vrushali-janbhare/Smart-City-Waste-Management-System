
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Complaint;
import mypackage.repository.Complaint_Repository;

@Service
public class Complaint_Service {

    @Autowired
    private Complaint_Repository complaintRepository;

    // Insert or update complaint
    public Complaint saveComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    // Get all complaints
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // Get complaint by ID
    public Optional<Complaint> getComplaintById(Long complaintId) {
        return complaintRepository.findById(complaintId);
    }

    // Delete complaint by ID
    public void deleteComplaint(Long complaintId) {
        complaintRepository.deleteById(complaintId);
    }
}
