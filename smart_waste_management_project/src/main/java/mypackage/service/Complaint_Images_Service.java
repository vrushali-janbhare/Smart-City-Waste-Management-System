package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Complaint_Images;
import mypackage.repository.Complaint_Images_Repository;

@Service
public class Complaint_Images_Service {

    @Autowired
    private Complaint_Images_Repository complaintImagesRepository;

    // Add Complaint Image
    public Complaint_Images addComplaintImage(Complaint_Images image) {
        return complaintImagesRepository.save(image);
    }

    // Get All Complaint Images
    public List<Complaint_Images> getAllComplaintImages() {
        return complaintImagesRepository.findAll();
    }

    // Get Complaint Image By ID
    public Complaint_Images getComplaintImageById(Long id) {
        return complaintImagesRepository.findById(id).orElse(null);
    }

    // Update Complaint Image
    public Complaint_Images updateComplaintImage(Long id, Complaint_Images image) {

        Complaint_Images existingImage =
                complaintImagesRepository.findById(id).orElse(null);

        if (existingImage != null) {

            existingImage.setImage_url(image.getImage_url());
            existingImage.setUploaded_at(image.getUploaded_at());
            existingImage.setComplaint(image.getComplaint());

            return complaintImagesRepository.save(existingImage);
        }

        return null;
    }

    // Delete Complaint Image
    public void deleteComplaintImage(Long id) {
        complaintImagesRepository.deleteById(id);
    }
}