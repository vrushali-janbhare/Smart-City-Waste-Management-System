
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Fine;
import mypackage.service.Fine_Service;

@RestController
@RequestMapping("/api/fines")
public class Fine_Controller {

    @Autowired
    private Fine_Service fineService;

    // Insert new fine
    @PostMapping
    public Fine saveFine(@RequestBody Fine fine) {

        return fineService.saveFine(fine);
    }

    // Get all fines
    @GetMapping
    public List<Fine> getAllFines() {

        return fineService.getAllFines();
    }

    // Get fine by ID
    @GetMapping("/{fineId}")
    public ResponseEntity<Fine> getFineById(
            @PathVariable Long fineId) {

        return fineService.getFineById(fineId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update fine by ID
    @PutMapping("/{fineId}")
    public ResponseEntity<Fine> updateFine(
            @PathVariable Long fineId,
            @RequestBody Fine fine) {

        return fineService.getFineById(fineId)
                .map(existingFine -> {

                    // Preserve primary key
                    fine.setFineId(fineId);

                    // Preserve existing issue date
                    if (fine.getIssuedAt() == null) {
                        fine.setIssuedAt(
                                existingFine.getIssuedAt());
                    }

                    return ResponseEntity.ok(
                            fineService.saveFine(fine));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete fine by ID
    @DeleteMapping("/{fineId}")
    public ResponseEntity<Void> deleteFine(
            @PathVariable Long fineId) {

        return fineService.getFineById(fineId)
                .map(existingFine -> {

                    fineService.deleteFine(fineId);

                    return ResponseEntity.noContent()
                            .<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
