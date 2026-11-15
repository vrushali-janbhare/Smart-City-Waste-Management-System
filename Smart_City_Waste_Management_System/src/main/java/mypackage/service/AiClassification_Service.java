
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.AiClassification;
import mypackage.repository.AiClassification_Repository;

@Service
public class AiClassification_Service {

    @Autowired
    private AiClassification_Repository aiClassificationRepository;

    // Insert or update AI classification
    public AiClassification saveAiClassification(AiClassification classification) {
        return aiClassificationRepository.save(classification);
    }

    // Get all AI classifications
    public List<AiClassification> getAllAiClassifications() {
        return aiClassificationRepository.findAll();
    }

    // Get AI classification by ID
    public Optional<AiClassification> getAiClassificationById(Long classificationId) {
        return aiClassificationRepository.findById(classificationId);
    }

    // Delete AI classification by ID
    public void deleteAiClassification(Long classificationId) {
        aiClassificationRepository.deleteById(classificationId);
    }
}
