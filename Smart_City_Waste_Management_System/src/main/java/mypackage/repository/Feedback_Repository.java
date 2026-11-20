
package mypackage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mypackage.model.Feedback;

public interface Feedback_Repository
        extends JpaRepository<Feedback, Long> {

    // Check whether feedback already exists for a complaint
    boolean existsByComplaint_ComplaintId(Long complaintId);

    // Find feedback for a particular complaint
    Optional<Feedback> findByComplaint_ComplaintId(
            Long complaintId
    );

    // Get feedback submitted by the logged-in citizen
    List<Feedback> findByComplaint_User_UserIdOrderByFeedbackIdDesc(
            Long userId
    );

    // ADMIN: Get all submitted feedback, newest first
    List<Feedback> findAllByOrderByFeedbackIdDesc();

    // PUBLIC: Get only admin-approved feedback
    List<Feedback> findByIsPublicTrueOrderByFeedbackIdDesc();
}
