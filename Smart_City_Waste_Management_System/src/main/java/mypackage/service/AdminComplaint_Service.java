
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.enums.ComplaintStatus;
import mypackage.model.Complaint;
import mypackage.model.ComplaintStatusHistory;
import mypackage.model.User;
import mypackage.model.Worker;

import mypackage.repository.Complaint_Repository;
import mypackage.repository.ComplaintStatusHistory_Repository;
import mypackage.repository.Worker_Repository;

@Service
public class AdminComplaint_Service {

    @Autowired
    private Complaint_Repository complaintRepository;

    @Autowired
    private Worker_Repository workerRepository;

    @Autowired
    private ComplaintStatusHistory_Repository historyRepository;

    // 1. Get all complaints
    @Transactional(readOnly = true)
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // 2. Get complaint by ID
    @Transactional(readOnly = true)
    public Optional<Complaint> getComplaintById(Long complaintId) {
        return complaintRepository.findById(complaintId);
    }

    // 3. Get all registered workers
    @Transactional(readOnly = true)
    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    // 4. Assign or reassign worker
    @Transactional
    public Complaint assignWorker(
            Long complaintId,
            Long workerId,
            User admin) {

        Complaint complaint = complaintRepository
                .findById(complaintId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Complaint not found"
                    )
                );

        Worker worker = workerRepository
                .findById(workerId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Worker not found"
                    )
                );

        ComplaintStatus oldStatus = complaint.getStatus();

        if (oldStatus != ComplaintStatus.PENDING
                && oldStatus != ComplaintStatus.ASSIGNED) {

            throw new IllegalStateException(
                "Only PENDING or ASSIGNED complaints can be assigned"
            );
        }

        // Prevent assigning same worker again
        if (oldStatus == ComplaintStatus.ASSIGNED
                && complaint.getWorker() != null
                && workerId.equals(
                    complaint.getWorker().getWorkerId()
                )) {

            throw new IllegalStateException(
                "This worker is already assigned"
            );
        }

        // Assign worker without availability validation
        complaint.setWorker(worker);
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        Complaint savedComplaint =
                complaintRepository.save(complaint);

        // Save assignment history
        ComplaintStatusHistory history =
                new ComplaintStatusHistory();

        history.setComplaint(savedComplaint);
        history.setChangedBy(admin);
        history.setOldStatus(oldStatus);
        history.setNewStatus(ComplaintStatus.ASSIGNED);

        if (oldStatus == ComplaintStatus.ASSIGNED) {
            history.setRemarks(
                "Complaint reassigned to worker ID " + workerId
            );
        } else {
            history.setRemarks(
                "Complaint assigned to worker ID " + workerId
            );
        }

        historyRepository.save(history);

        return savedComplaint;
    }

    // 5. Reject pending complaint
    @Transactional
    public Complaint rejectComplaint(
            Long complaintId,
            String reason,
            User admin) {

        Complaint complaint = complaintRepository
                .findById(complaintId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Complaint not found"
                    )
                );

        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new IllegalStateException(
                "Only PENDING complaints can be rejected"
            );
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Rejection reason is required"
            );
        }

        if (reason.trim().length() > 255) {
            throw new IllegalArgumentException(
                "Rejection reason cannot exceed 255 characters"
            );
        }

        ComplaintStatus oldStatus = complaint.getStatus();

        complaint.setStatus(ComplaintStatus.REJECTED);
        complaint.setWorker(null);

        Complaint savedComplaint =
                complaintRepository.save(complaint);

        // Save rejection history
        ComplaintStatusHistory history =
                new ComplaintStatusHistory();

        history.setComplaint(savedComplaint);
        history.setChangedBy(admin);
        history.setOldStatus(oldStatus);
        history.setNewStatus(ComplaintStatus.REJECTED);
        history.setRemarks(reason.trim());

        historyRepository.save(history);

        return savedComplaint;
    }
}
