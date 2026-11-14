package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Feedback;
import mypackage.repository.Feedback_Repository;

@Service
public class Feedback_Service {

    @Autowired
    private Feedback_Repository feedbackRepository;

    // Add Feedback
    public Feedback addFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    // Get All Feedback
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // Get Feedback By ID
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    // Update Feedback
    public Feedback updateFeedback(Long id, Feedback feedback) {

        Feedback existingFeedback =
                feedbackRepository.findById(id).orElse(null);

        if (existingFeedback != null) {

            existingFeedback.setUsers(feedback.getUsers());
            existingFeedback.setComplaint(feedback.getComplaint());
            existingFeedback.setRating(feedback.getRating());
            existingFeedback.setComments(feedback.getComments());

            return feedbackRepository.save(existingFeedback);
        }

        return null;
    }

    // Delete Feedback
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}