package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Complaint_Images;
import mypackage.service.Complaint_Images_Service;

@RestController
@RequestMapping("/api/complaint-images")
public class Complaint_Images_Controller {

    @Autowired
    private Complaint_Images_Service complaintImagesService;

    // Add Complaint Image
    @PostMapping
    public Complaint_Images addComplaintImage(@RequestBody Complaint_Images image) {
        return complaintImagesService.addComplaintImage(image);
    }

    // Get All Complaint Images
    @GetMapping
    public List<Complaint_Images> getAllComplaintImages() {
        return complaintImagesService.getAllComplaintImages();
    }

    // Get Complaint Image By ID
    @GetMapping("/{id}")
    public Complaint_Images getComplaintImageById(@PathVariable Long id) {
        return complaintImagesService.getComplaintImageById(id);
    }

    // Update Complaint Image
    @PutMapping("/{id}")
    public Complaint_Images updateComplaintImage(
            @PathVariable Long id,
            @RequestBody Complaint_Images image) {

        return complaintImagesService.updateComplaintImage(id, image);
    }

    // Delete Complaint Image
    @DeleteMapping("/{id}")
    public String deleteComplaintImage(@PathVariable Long id) {
        complaintImagesService.deleteComplaintImage(id);

        return "Complaint Image Deleted Successfully!";
    }
}