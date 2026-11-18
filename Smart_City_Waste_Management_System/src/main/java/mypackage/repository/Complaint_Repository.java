
package mypackage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.Complaint;
import mypackage.enums.ComplaintStatus;

@Repository
public interface Complaint_Repository
        extends JpaRepository<Complaint, Long> {

    // Get complaints belonging to a particular user
    List<Complaint> findByUser_UserId(Long userId);

    // Count complaints by their status
    long countByStatus(ComplaintStatus status);

    // Get the latest five complaints
    List<Complaint> findTop5ByOrderByCreatedAtDescComplaintIdDesc();

    // Count complaints assigned to a particular worker
    long countByWorker_WorkerId(Long workerId);
}
