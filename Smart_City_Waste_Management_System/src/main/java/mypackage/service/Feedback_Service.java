
package mypackage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.dto.FeedbackRequestDTO;
import mypackage.dto.FeedbackResponseDTO;
import mypackage.dto.AdminFeedbackDTO;
import mypackage.dto.PublicFeedbackDTO;
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

    // ==========================================
    // EXISTING CITIZEN FEEDBACK FUNCTIONALITY
    // ==========================================

    private FeedbackResponseDTO convertToDTO(
            Feedback feedback) {

        FeedbackResponseDTO dto =
                new FeedbackResponseDTO();

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

    @Transactional
    public FeedbackResponseDTO submitFeedback(
            Long userId,
            FeedbackRequestDTO request) {

        if (request == null ||
                request.getComplaintId() == null) {

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

        if (complaint.getUser() == null ||
                !userId.equals(
                        complaint.getUser().getUserId()
                )) {

            throw new SecurityException(
                    "You can submit feedback only for your own complaint"
            );
        }

        if (complaint.getStatus()
                != ComplaintStatus.RESOLVED) {

            throw new IllegalArgumentException(
                    "Feedback is allowed only for resolved complaints"
            );
        }

        if (feedbackRepository
                .existsByComplaint_ComplaintId(
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

        // New feedback is private by default
        feedback.setIsPublic(false);

        Feedback savedFeedback =
                feedbackRepository.save(feedback);

        return convertToDTO(savedFeedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponseDTO> getMyFeedback(
            Long userId) {

        return feedbackRepository
                .findByComplaint_User_UserIdOrderByFeedbackIdDesc(
                        userId
                )
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ADMIN FEEDBACK FUNCTIONALITY
    // ==========================================

    private AdminFeedbackDTO convertToAdminDTO(
            Feedback feedback) {

        AdminFeedbackDTO dto =
                new AdminFeedbackDTO();

        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setRating(feedback.getRating());
        dto.setComments(feedback.getComments());

        dto.setIsPublic(
                Boolean.TRUE.equals(feedback.getIsPublic())
        );

        return dto;
    }

    // Admin: Get all submitted feedback
    @Transactional(readOnly = true)
    public List<AdminFeedbackDTO> getAllFeedback() {

        return feedbackRepository
                .findAllByOrderByFeedbackIdDesc()
                .stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
    }

    // Admin: Approve or remove public visibility
    @Transactional
    public AdminFeedbackDTO updatePublicStatus(
            Long feedbackId,
            boolean isPublic) {

        Feedback feedback = feedbackRepository
                .findById(feedbackId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Feedback not found"
                        )
                );

        // Do not publish feedback without comments
        if (isPublic &&
                (feedback.getComments() == null ||
                 feedback.getComments().trim().isEmpty())) {

            throw new IllegalArgumentException(
                    "Feedback without comments cannot be published"
            );
        }

        feedback.setIsPublic(isPublic);

        Feedback updated =
                feedbackRepository.save(feedback);

        return convertToAdminDTO(updated);
    }

    // Admin: Total number of feedback records
    @Transactional(readOnly = true)
    public long getTotalFeedback() {
        return feedbackRepository.count();
    }

    // Admin: Average feedback rating
    @Transactional(readOnly = true)
    public double getAverageRating() {

        List<Feedback> feedbackList =
                feedbackRepository.findAll();

        if (feedbackList.isEmpty()) {
            return 0.0;
        }

        double average = feedbackList.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        return Math.round(average * 10.0) / 10.0;
    }

    // ==========================================
    // PUBLIC HOMEPAGE FEEDBACK
    // ==========================================

    private PublicFeedbackDTO convertToPublicDTO(
            Feedback feedback) {

        PublicFeedbackDTO dto =
                new PublicFeedbackDTO();

        dto.setRating(feedback.getRating());
        dto.setComments(feedback.getComments());

        return dto;
    }

    // Public: Retrieve approved testimonials only
    @Transactional(readOnly = true)
    public List<PublicFeedbackDTO> getPublicFeedback() {

        return feedbackRepository
                .findByIsPublicTrueOrderByFeedbackIdDesc()
                .stream()
                .map(this::convertToPublicDTO)
                .collect(Collectors.toList());
    }
}
