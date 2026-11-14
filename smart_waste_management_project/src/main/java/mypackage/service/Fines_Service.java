package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Fines;
import mypackage.repository.Fines_Repository;

@Service
public class Fines_Service {

    @Autowired
    private Fines_Repository finesRepository;

    // Add Fine
    public Fines addFine(Fines fine) {
        return finesRepository.save(fine);
    }

    // Get All Fines
    public List<Fines> getAllFines() {
        return finesRepository.findAll();
    }

    // Get Fine By ID
    public Fines getFineById(Long id) {
        return finesRepository.findById(id).orElse(null);
    }

    // Update Fine
    public Fines updateFine(Long id, Fines fine) {

        Fines existingFine = finesRepository.findById(id).orElse(null);

        if (existingFine != null) {

            existingFine.setUsers(fine.getUsers());
            existingFine.setPerson_name(fine.getPerson_name());
            existingFine.setComplaint(fine.getComplaint());
            existingFine.setFine_amount(fine.getFine_amount());
            existingFine.setReason(fine.getReason());
            existingFine.setStatus(fine.getStatus());
            existingFine.setPaid_at(fine.getPaid_at());

            return finesRepository.save(existingFine);
        }

        return null;
    }

    // Delete Fine
    public void deleteFine(Long id) {
        finesRepository.deleteById(id);
    }
}