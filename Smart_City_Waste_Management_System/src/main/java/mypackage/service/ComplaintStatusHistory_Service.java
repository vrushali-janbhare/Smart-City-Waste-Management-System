
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.ComplaintStatusHistory;
import mypackage.repository.ComplaintStatusHistory_Repository;

@Service
public class ComplaintStatusHistory_Service {

    @Autowired
    private ComplaintStatusHistory_Repository complaintStatusHistoryRepository;

    // Insert or update complaint status history
    public ComplaintStatusHistory saveComplaintStatusHistory(ComplaintStatusHistory history) {
        return complaintStatusHistoryRepository.save(history);
    }

    // Get all complaint status histories
    public List<ComplaintStatusHistory> getAllComplaintStatusHistories() {
        return complaintStatusHistoryRepository.findAll();
    }

    // Get complaint status history by ID
    public Optional<ComplaintStatusHistory> getComplaintStatusHistoryById(Long historyId) {
        return complaintStatusHistoryRepository.findById(historyId);
    }

    // Delete complaint status history by ID
    public void deleteComplaintStatusHistory(Long historyId) {
        complaintStatusHistoryRepository.deleteById(historyId);
    }
}
