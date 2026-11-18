
package mypackage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.Complaint;
import mypackage.enums.ComplaintStatus;

@Repository
public interface Complaint_Repository
        extends JpaRepository<Complaint, Long> {

    // ==========================================
    // EXISTING METHODS
    // ==========================================

    // Get complaints belonging to a particular citizen
    List<Complaint> findByUser_UserId(Long userId);

    // Count complaints by status
    long countByStatus(ComplaintStatus status);

    // Get the latest five complaints
    List<Complaint> findTop5ByOrderByCreatedAtDescComplaintIdDesc();

    // Count complaints assigned to a worker
    long countByWorker_WorkerId(Long workerId);


    // ==========================================
    // NEW METHODS FOR WORKER DASHBOARD
    // ==========================================

    // 1. Get all complaints assigned to a worker
    List<Complaint> findByWorker_WorkerIdOrderByCreatedAtDesc(
            Long workerId
    );

    // 2. Count worker complaints by status
    long countByWorker_WorkerIdAndStatus(
            Long workerId,
            ComplaintStatus status
    );

    // 3. Find a complaint only if assigned
    // to the specified worker
    Optional<Complaint> findByComplaintIdAndWorker_WorkerId(
            Long complaintId,
            Long workerId
    );
}
