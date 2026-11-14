package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Feedback;
import mypackage.service.Feedback_Service;

@RestController
@RequestMapping("/api/feedback")
public class Feedback_Controller {

    @Autowired
    private Feedback_Service feedbackService;

    // Add Feedback
    @PostMapping
    public Feedback addFeedback(@RequestBody Feedback feedback) {
        return feedbackService.addFeedback(feedback);
    }

    // Get All Feedback
    @GetMapping
    public List<Feedback> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }

    // Get Feedback By ID
    @GetMapping("/{id}")
    public Feedback getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id);
    }

    // Update Feedback
    @PutMapping("/{id}")
    public Feedback updateFeedback(
            @PathVariable Long id,
            @RequestBody Feedback feedback) {

        return feedbackService.updateFeedback(id, feedback);
    }

    // Delete Feedback
    @DeleteMapping("/{id}")
    public String deleteFeedback(@PathVariable Long id) {

        feedbackService.deleteFeedback(id);

        return "Feedback Deleted Successfully!";
    }
}