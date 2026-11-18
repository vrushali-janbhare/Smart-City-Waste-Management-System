
package mypackage.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.dto.FeedbackRequestDTO;
import mypackage.dto.FeedbackResponseDTO;
import mypackage.service.Feedback_Service;

@RestController
@RequestMapping("/api/feedback")
public class Feedback_Controller {

    @Autowired
    private Feedback_Service feedbackService;


    // Submit feedback for logged-in citizen
    @PostMapping
    public ResponseEntity<?> submitFeedback(
            @RequestBody FeedbackRequestDTO request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute(
                "loggedInUserId"
        );

        String role = (String) session.getAttribute(
                "loggedInUserRole"
        );

        if (userId == null) {
            return ResponseEntity.status(401).body(
                    Collections.singletonMap(
                            "message",
                            "Please log in first"
                    )
            );
        }

        if (!"USER".equals(role)) {
            return ResponseEntity.status(403).body(
                    Collections.singletonMap(
                            "message",
                            "Only citizens can submit feedback"
                    )
            );
        }

        try {

            FeedbackResponseDTO result =
                    feedbackService.submitFeedback(userId, request);

            return ResponseEntity.ok(result);

        } catch (SecurityException e) {

            return ResponseEntity.status(403).body(
                    Collections.singletonMap(
                            "message",
                            e.getMessage()
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Collections.singletonMap(
                            "message",
                            e.getMessage()
                    )
            );
        }
    }


    // Get logged-in citizen's feedback history
    @GetMapping("/my-feedback")
    public ResponseEntity<?> getMyFeedback(
            HttpSession session) {

        Long userId = (Long) session.getAttribute(
                "loggedInUserId"
        );

        String role = (String) session.getAttribute(
                "loggedInUserRole"
        );

        if (userId == null) {
            return ResponseEntity.status(401).body(
                    Collections.singletonMap(
                            "message",
                            "Please log in first"
                    )
            );
        }

        if (!"USER".equals(role)) {
            return ResponseEntity.status(403).body(
                    Collections.singletonMap(
                            "message",
                            "Only citizens can view their feedback"
                    )
            );
        }

        List<FeedbackResponseDTO> feedbackList =
                feedbackService.getMyFeedback(userId);

        return ResponseEntity.ok(feedbackList);
    }
}
