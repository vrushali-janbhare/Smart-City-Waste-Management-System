
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.AiClassification;
import mypackage.service.AiClassification_Service;

@RestController
@RequestMapping("/api/ai-classifications")
public class AiClassification_Controller {

    @Autowired
    private AiClassification_Service aiClassificationService;

    // Insert new AI classification
    @PostMapping
    public AiClassification saveAiClassification(
            @RequestBody AiClassification aiClassification) {

        return aiClassificationService.saveAiClassification(aiClassification);
    }

    // Get all AI classifications
    @GetMapping
    public List<AiClassification> getAllAiClassifications() {
        return aiClassificationService.getAllAiClassifications();
    }

    // Get AI classification by ID
    @GetMapping("/{classificationId}")
    public ResponseEntity<AiClassification> getAiClassificationById(
            @PathVariable Long classificationId) {

        return aiClassificationService.getAiClassificationById(classificationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update AI classification by ID
    @PutMapping("/{classificationId}")
    public ResponseEntity<AiClassification> updateAiClassification(
            @PathVariable Long classificationId,
            @RequestBody AiClassification aiClassification) {

        return aiClassificationService.getAiClassificationById(classificationId)
                .map(existingClassification -> {
                    aiClassification.setClassificationId(classificationId);
                    return ResponseEntity.ok(
                            aiClassificationService.saveAiClassification(
                                    aiClassification));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete AI classification by ID
    @DeleteMapping("/{classificationId}")
    public ResponseEntity<Void> deleteAiClassification(
            @PathVariable Long classificationId) {

        return aiClassificationService.getAiClassificationById(classificationId)
                .map(existingClassification -> {
                    aiClassificationService.deleteAiClassification(classificationId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
