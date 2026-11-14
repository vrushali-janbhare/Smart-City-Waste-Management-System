
package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Ai_Classifications;
import mypackage.repository.Ai_Classifications_Repository;

@Service
public class Ai_Classifications_Service {

    @Autowired
    private Ai_Classifications_Repository aiClassificationsRepository;

    // Add AI Classification
    public Ai_Classifications addAiClassification(Ai_Classifications classification) {
        return aiClassificationsRepository.save(classification);
    }

    // Get All AI Classifications
    public List<Ai_Classifications> getAllAiClassifications() {
        return aiClassificationsRepository.findAll();
    }

    // Get AI Classification By ID
    public Ai_Classifications getAiClassificationById(Long id) {
        return aiClassificationsRepository.findById(id).orElse(null);
    }

    // Update AI Classification
    public Ai_Classifications updateAiClassification(Long id, Ai_Classifications classification) {

        Ai_Classifications existingClassification =
                aiClassificationsRepository.findById(id).orElse(null);

        if (existingClassification != null) {

            existingClassification.setComplaint(classification.getComplaint());
            existingClassification.setPredicted_category(classification.getPredicted_category());
            existingClassification.setConfidence_score(classification.getConfidence_score());
            existingClassification.setImage_url(classification.getImage_url());
            existingClassification.setModel_name(classification.getModel_name());

            return aiClassificationsRepository.save(existingClassification);
        }

        return null;
    }

    // Delete AI Classification
    public void deleteAiClassification(Long id) {
        aiClassificationsRepository.deleteById(id);
    }
}
