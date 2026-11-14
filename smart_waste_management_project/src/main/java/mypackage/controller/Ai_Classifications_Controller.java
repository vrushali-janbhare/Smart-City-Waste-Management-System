
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Ai_Classifications;
import mypackage.service.Ai_Classifications_Service;

@RestController
@RequestMapping("/api/ai-classifications")
public class Ai_Classifications_Controller {

    @Autowired
    private Ai_Classifications_Service aiClassificationsService;

    // Add AI Classification
    @PostMapping
    public Ai_Classifications addAiClassification(@RequestBody Ai_Classifications classification) {
        return aiClassificationsService.addAiClassification(classification);
    }

    // Get All AI Classifications
    @GetMapping
    public List<Ai_Classifications> getAllAiClassifications() {
        return aiClassificationsService.getAllAiClassifications();
    }

    // Get AI Classification By ID
    @GetMapping("/{id}")
    public Ai_Classifications getAiClassificationById(@PathVariable Long id) {
        return aiClassificationsService.getAiClassificationById(id);
    }

    // Update AI Classification
    @PutMapping("/{id}")
    public Ai_Classifications updateAiClassification(
            @PathVariable Long id,
            @RequestBody Ai_Classifications classification) {

        return aiClassificationsService.updateAiClassification(id, classification);
    }

    // Delete AI Classification
    @DeleteMapping("/{id}")
    public String deleteAiClassification(@PathVariable Long id) {
        aiClassificationsService.deleteAiClassification(id);
        return "AI Classification Deleted Successfully!";
    }
}
