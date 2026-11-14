package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Complaint_Status_History;
import mypackage.repository.Complaint_Status_History_Repository;

@Service
public class Complaint_Status_History_Service {

    @Autowired
    private Complaint_Status_History_Repository historyRepository;

    // Add Complaint Status History
    public Complaint_Status_History addHistory(Complaint_Status_History history) {
        return historyRepository.save(history);
    }

    // Get All History
    public List<Complaint_Status_History> getAllHistory() {
        return historyRepository.findAll();
    }

    // Get History By ID
    public Complaint_Status_History getHistoryById(Long id) {
        return historyRepository.findById(id).orElse(null);
    }

    // Update History
    public Complaint_Status_History updateHistory(Long id, Complaint_Status_History history) {

        Complaint_Status_History existingHistory =
                historyRepository.findById(id).orElse(null);

        if (existingHistory != null) {

            existingHistory.setRemarks(history.getRemarks());
            existingHistory.setStatus(history.getStatus());
            existingHistory.setComplaint(history.getComplaint());
            existingHistory.setUsers(history.getUsers());
            existingHistory.setChanged_at(history.getChanged_at());

            return historyRepository.save(existingHistory);
        }

        return null;
    }

    // Delete History
    public void deleteHistory(Long id) {
        historyRepository.deleteById(id);
    }
}