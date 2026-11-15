
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Feedback;
import mypackage.service.Feedback_Service;

@RestController
@RequestMapping("/api/feedback")
public class Feedback_Controller {

    @Autowired
    private Feedback_Service feedbackService;

    // Insert new feedback
    @PostMapping
    public Feedback saveFeedback(@RequestBody Feedback feedback) {
        return feedbackService.saveFeedback(feedback);
    }

    // Get all feedback
    @GetMapping
    public List<Feedback> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }

    // Get feedback by ID
    @GetMapping("/{feedbackId}")
    public ResponseEntity<Feedback> getFeedbackById(
            @PathVariable Long feedbackId) {

        return feedbackService.getFeedbackById(feedbackId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update feedback by ID
    @PutMapping("/{feedbackId}")
    public ResponseEntity<Feedback> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody Feedback feedback) {

        return feedbackService.getFeedbackById(feedbackId)
                .map(existingFeedback -> {
                    feedback.setFeedbackId(feedbackId);
                    return ResponseEntity.ok(
                            feedbackService.saveFeedback(feedback));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete feedback by ID
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(
            @PathVariable Long feedbackId) {

        return feedbackService.getFeedbackById(feedbackId)
                .map(existingFeedback -> {
                    feedbackService.deleteFeedback(feedbackId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
