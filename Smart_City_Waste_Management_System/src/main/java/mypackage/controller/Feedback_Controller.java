
package mypackage.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.dto.FeedbackRequestDTO;
import mypackage.dto.FeedbackResponseDTO;
import mypackage.dto.AdminFeedbackDTO;
import mypackage.dto.PublicFeedbackDTO;
import mypackage.service.Feedback_Service;

@RestController
@RequestMapping("/api/feedback")
public class Feedback_Controller {

    @Autowired
    private Feedback_Service feedbackService;

    // ==========================================
    // COMMON HELPERS
    // ==========================================

    private ResponseEntity<?> errorResponse(
            int status,
            String message) {

        return ResponseEntity.status(status).body(
                Collections.singletonMap(
                        "message",
                        message
                )
        );
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUserId") != null;
    }

    private boolean hasRole(
            HttpSession session,
            String requiredRole) {

        return requiredRole.equals(
                session.getAttribute("loggedInUserRole")
        );
    }

    // ==========================================
    // CITIZEN: SUBMIT FEEDBACK
    // ==========================================

    @PostMapping
    public ResponseEntity<?> submitFeedback(
            @RequestBody FeedbackRequestDTO request,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return errorResponse(
                    401,
                    "Please log in first"
            );
        }

        if (!hasRole(session, "USER")) {
            return errorResponse(
                    403,
                    "Only citizens can submit feedback"
            );
        }

        try {

            Long userId = (Long) session.getAttribute(
                    "loggedInUserId"
            );

            FeedbackResponseDTO result =
                    feedbackService.submitFeedback(
                            userId,
                            request
                    );

            return ResponseEntity.ok(result);

        } catch (SecurityException e) {

            return errorResponse(403, e.getMessage());

        } catch (IllegalArgumentException e) {

            return errorResponse(400, e.getMessage());
        }
    }

    // ==========================================
    // CITIZEN: MY FEEDBACK
    // ==========================================

    @GetMapping("/my-feedback")
    public ResponseEntity<?> getMyFeedback(
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return errorResponse(
                    401,
                    "Please log in first"
            );
        }

        if (!hasRole(session, "USER")) {
            return errorResponse(
                    403,
                    "Only citizens can view their feedback"
            );
        }

        Long userId = (Long) session.getAttribute(
                "loggedInUserId"
        );

        List<FeedbackResponseDTO> feedbackList =
                feedbackService.getMyFeedback(userId);

        return ResponseEntity.ok(feedbackList);
    }

    // ==========================================
    // ADMIN: GET ALL FEEDBACK
    // ==========================================

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllFeedback(
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return errorResponse(
                    401,
                    "Please log in first"
            );
        }

        if (!hasRole(session, "ADMIN")) {
            return errorResponse(
                    403,
                    "Only administrators can view all feedback"
            );
        }

        List<AdminFeedbackDTO> feedbackList =
                feedbackService.getAllFeedback();

        return ResponseEntity.ok(feedbackList);
    }

    // ==========================================
    // ADMIN: FEEDBACK STATISTICS
    // ==========================================

    @GetMapping("/admin/stats")
    public ResponseEntity<?> getFeedbackStatistics(
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return errorResponse(
                    401,
                    "Please log in first"
            );
        }

        if (!hasRole(session, "ADMIN")) {
            return errorResponse(
                    403,
                    "Only administrators can view feedback statistics"
            );
        }

        Map<String, Object> statistics = new HashMap<>();

        statistics.put(
                "totalFeedback",
                feedbackService.getTotalFeedback()
        );

        statistics.put(
                "averageRating",
                feedbackService.getAverageRating()
        );

        return ResponseEntity.ok(statistics);
    }

    // ==========================================
    // ADMIN: APPROVE / REMOVE PUBLIC FEEDBACK
    // ==========================================

    @PutMapping("/admin/{feedbackId}/visibility")
    public ResponseEntity<?> updatePublicVisibility(
            @PathVariable Long feedbackId,
            @RequestParam boolean isPublic,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return errorResponse(
                    401,
                    "Please log in first"
            );
        }

        if (!hasRole(session, "ADMIN")) {
            return errorResponse(
                    403,
                    "Only administrators can change feedback visibility"
            );
        }

        try {

            AdminFeedbackDTO updated =
                    feedbackService.updatePublicStatus(
                            feedbackId,
                            isPublic
                    );

            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {

            return errorResponse(
                    400,
                    e.getMessage()
            );
        }
    }

    // ==========================================
    // PUBLIC: APPROVED FEEDBACK
    // ==========================================

    @GetMapping("/public")
    public ResponseEntity<List<PublicFeedbackDTO>>
            getPublicFeedback() {

        List<PublicFeedbackDTO> feedbackList =
                feedbackService.getPublicFeedback();

        return ResponseEntity.ok(feedbackList);
    }
}
