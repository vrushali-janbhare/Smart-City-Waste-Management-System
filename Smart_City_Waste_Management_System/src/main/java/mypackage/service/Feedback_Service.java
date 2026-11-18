
package mypackage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.dto.FeedbackRequestDTO;
import mypackage.dto.FeedbackResponseDTO;
import mypackage.enums.ComplaintStatus;
import mypackage.model.Complaint;
import mypackage.model.Feedback;
import mypackage.repository.Complaint_Repository;
import mypackage.repository.Feedback_Repository;

@Service
public class Feedback_Service {

    @Autowired
    private Feedback_Repository feedbackRepository;

    @Autowired
    private Complaint_Repository complaintRepository;


    // Convert entity into response DTO
    private FeedbackResponseDTO convertToDTO(Feedback feedback) {

        FeedbackResponseDTO dto = new FeedbackResponseDTO();

        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setComplaintId(
                feedback.getComplaint().getComplaintId()
        );
        dto.setComplaintDescription(
                feedback.getComplaint().getDescription()
        );
        dto.setLocationAddress(
                feedback.getComplaint().getLocationAddress()
        );
        dto.setRating(feedback.getRating());
        dto.setComments(feedback.getComments());

        return dto;
    }


    // Submit feedback for logged-in citizen
    @Transactional
    public FeedbackResponseDTO submitFeedback(
            Long userId,
            FeedbackRequestDTO request) {

        if (request == null || request.getComplaintId() == null) {
            throw new IllegalArgumentException(
                    "Please select a complaint"
            );
        }

        Integer rating = request.getRating();

        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5"
            );
        }

        Complaint complaint = complaintRepository
                .findById(request.getComplaintId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Complaint not found"
                        )
                );

        // Check complaint ownership
        if (complaint.getUser() == null ||
                !userId.equals(
                        complaint.getUser().getUserId()
                )) {

            throw new SecurityException(
                    "You can submit feedback only for your own complaint"
            );
        }

        // Only resolved complaints
        if (complaint.getStatus() != ComplaintStatus.RESOLVED) {
            throw new IllegalArgumentException(
                    "Feedback is allowed only for resolved complaints"
            );
        }

        // Prevent duplicate feedback
        if (feedbackRepository.existsByComplaint_ComplaintId(
                complaint.getComplaintId())) {

            throw new IllegalArgumentException(
                    "Feedback has already been submitted for this complaint"
            );
        }

        Feedback feedback = new Feedback();

        feedback.setComplaint(complaint);
        feedback.setRating(rating);

        String comments = request.getComments();

        feedback.setComments(
                comments == null || comments.trim().isEmpty()
                        ? null
                        : comments.trim()
        );

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return convertToDTO(savedFeedback);
    }


    // Get only logged-in citizen's feedback
    @Transactional(readOnly = true)
    public List<FeedbackResponseDTO> getMyFeedback(Long userId) {

        return feedbackRepository
                .findByComplaint_User_UserIdOrderByFeedbackIdDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
