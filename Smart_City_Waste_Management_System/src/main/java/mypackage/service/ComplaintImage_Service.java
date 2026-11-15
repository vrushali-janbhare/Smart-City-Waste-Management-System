
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.ComplaintImage;
import mypackage.repository.ComplaintImage_Repository;

@Service
public class ComplaintImage_Service {

    @Autowired
    private ComplaintImage_Repository complaintImageRepository;

    // Insert or update complaint image
    public ComplaintImage saveComplaintImage(ComplaintImage complaintImage) {
        return complaintImageRepository.save(complaintImage);
    }

    // Get all complaint images
    public List<ComplaintImage> getAllComplaintImages() {
        return complaintImageRepository.findAll();
    }

    // Get complaint image by ID
    public Optional<ComplaintImage> getComplaintImageById(Long imageId) {
        return complaintImageRepository.findById(imageId);
    }

    // Delete complaint image by ID
    public void deleteComplaintImage(Long imageId) {
        complaintImageRepository.deleteById(imageId);
    }
}
