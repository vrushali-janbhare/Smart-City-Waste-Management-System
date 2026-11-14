package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Complaints;
import mypackage.repository.Complaints_Repository;

@Service
public class Complaints_Service {

    @Autowired
    private Complaints_Repository complaintsRepository;

    // Add Complaint
    public Complaints addComplaint(Complaints complaint) {
        return complaintsRepository.save(complaint);
    }

    // Get All Complaints
    public List<Complaints> getAllComplaints() {
        return complaintsRepository.findAll();
    }

    // Get Complaint By ID
    public Complaints getComplaintById(Long id) {
        return complaintsRepository.findById(id).orElse(null);
    }

    // Update Complaint
    public Complaints updateComplaint(Long id, Complaints complaint) {

        Complaints existingComplaint =
                complaintsRepository.findById(id).orElse(null);

        if (existingComplaint != null) {

            existingComplaint.setTitle(complaint.getTitle());
            existingComplaint.setDescription(complaint.getDescription());
            existingComplaint.setLocation_address(complaint.getLocation_address());
            existingComplaint.setLatitude(complaint.getLatitude());
            existingComplaint.setLongitude(complaint.getLongitude());
            existingComplaint.setStatus(complaint.getStatus());
            existingComplaint.setPriority(complaint.getPriority());
            existingComplaint.setUpdated_at(complaint.getUpdated_at());
            existingComplaint.setResolved_at(complaint.getResolved_at());

            existingComplaint.setUsers(complaint.getUsers());
            existingComplaint.setAreas(complaint.getAreas());
            existingComplaint.setWaste_categorie(complaint.getWaste_categorie());
            existingComplaint.setWorkers(complaint.getWorkers());

            return complaintsRepository.save(existingComplaint);
        }

        return null;
    }

    // Delete Complaint
    public void deleteComplaint(Long id) {
        complaintsRepository.deleteById(id);
    }
}