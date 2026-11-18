
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

    // 1. Insert or update complaint
    public Complaint saveComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    // 2. Get all complaints
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // 3. Get complaint by ID
    public Optional<Complaint> getComplaintById(Long complaintId) {
        return complaintRepository.findById(complaintId);
    }

    // 4. Delete complaint by ID
    public void deleteComplaint(Long complaintId) {
        complaintRepository.deleteById(complaintId);
    }

    // 5. Get complaints by user ID
    public List<Complaint> getComplaintsByUserId(Long userId) {
        return complaintRepository.findByUser_UserId(userId);
    }

}
