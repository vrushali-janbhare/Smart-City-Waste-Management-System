
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.ComplaintImage;
import mypackage.service.ComplaintImage_Service;

@RestController
@RequestMapping("/api/complaint-images")
public class ComplaintImage_Controller {

    @Autowired
    private ComplaintImage_Service complaintImageService;

    // Insert new complaint image
    @PostMapping
    public ComplaintImage saveComplaintImage(
            @RequestBody ComplaintImage complaintImage) {

        return complaintImageService.saveComplaintImage(complaintImage);
    }

    // Get all complaint images
    @GetMapping
    public List<ComplaintImage> getAllComplaintImages() {
        return complaintImageService.getAllComplaintImages();
    }

    // Get complaint image by ID
    @GetMapping("/{imageId}")
    public ResponseEntity<ComplaintImage> getComplaintImageById(
            @PathVariable Long imageId) {

        return complaintImageService.getComplaintImageById(imageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update complaint image by ID
    @PutMapping("/{imageId}")
    public ResponseEntity<ComplaintImage> updateComplaintImage(
            @PathVariable Long imageId,
            @RequestBody ComplaintImage complaintImage) {

        return complaintImageService.getComplaintImageById(imageId)
                .map(existingImage -> {
                    complaintImage.setImageId(imageId);
                    return ResponseEntity.ok(
                            complaintImageService.saveComplaintImage(complaintImage));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete complaint image by ID
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteComplaintImage(
            @PathVariable Long imageId) {

        return complaintImageService.getComplaintImageById(imageId)
                .map(existingImage -> {
                    complaintImageService.deleteComplaintImage(imageId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
