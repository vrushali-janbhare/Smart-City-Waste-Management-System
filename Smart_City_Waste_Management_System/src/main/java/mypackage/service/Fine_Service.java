
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Fine;
import mypackage.repository.Fine_Repository;

@Service
public class Fine_Service {

    @Autowired
    private Fine_Repository fineRepository;

    // Insert or update fine
    public Fine saveFine(Fine fine) {
        return fineRepository.save(fine);
    }

    // Get all fines
    public List<Fine> getAllFines() {
        return fineRepository.findAll();
    }

    // Get fine by ID
    public Optional<Fine> getFineById(Long fineId) {
        return fineRepository.findById(fineId);
    }

    // Delete fine by ID
    public void deleteFine(Long fineId) {
        fineRepository.deleteById(fineId);
    }
}
