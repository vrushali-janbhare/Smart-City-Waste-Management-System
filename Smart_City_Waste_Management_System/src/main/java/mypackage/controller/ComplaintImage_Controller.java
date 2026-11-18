
package mypackage.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import mypackage.model.Complaint;
import mypackage.model.ComplaintImage;
import mypackage.repository.Complaint_Repository;
import mypackage.service.ComplaintImage_Service;

@RestController
@RequestMapping("/api/complaint-images")
public class ComplaintImage_Controller {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Path UPLOAD_DIRECTORY =
            Paths.get("uploads", "complaints").toAbsolutePath().normalize();

    @Autowired
    private ComplaintImage_Service complaintImageService;

    @Autowired
    private Complaint_Repository complaintRepository;

    // Upload complaint image
    @PostMapping(
        value = "/upload/{complaintId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadComplaintImage(
            @PathVariable Long complaintId,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        try {

            // Check login session
            Long loggedInUserId =
                    (Long) session.getAttribute("loggedInUserId");

            if (loggedInUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Please login first.");
            }

            // Check whether file is selected
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Please select an image.");
            }

            // Check file size
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest()
                        .body("Image size must not exceed 5 MB.");
            }

            // Validate image type
            String contentType = file.getContentType();

            String extension;

            if ("image/jpeg".equalsIgnoreCase(contentType)) {
                extension = ".jpg";
            } else if ("image/png".equalsIgnoreCase(contentType)) {
                extension = ".png";
            } else if ("image/webp".equalsIgnoreCase(contentType)) {
                extension = ".webp";
            } else {
                return ResponseEntity.badRequest()
                        .body("Only JPG, JPEG, PNG and WEBP images are allowed.");
            }

            // Find complaint
            Complaint complaint = complaintRepository
                    .findById(complaintId)
                    .orElse(null);

            if (complaint == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Complaint not found.");
            }

            // Check complaint ownership
            if (complaint.getUser() == null ||
                    !loggedInUserId.equals(
                            complaint.getUser().getUserId())) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You cannot upload images for this complaint.");
            }

            // Create upload directory
            Files.createDirectories(UPLOAD_DIRECTORY);

            // Generate a unique file name
            String fileName = UUID.randomUUID().toString() + extension;

            Path filePath = UPLOAD_DIRECTORY.resolve(fileName).normalize();

            // Prevent saving outside upload directory
            if (!filePath.startsWith(UPLOAD_DIRECTORY)) {
                return ResponseEntity.badRequest()
                        .body("Invalid file path.");
            }

            // Save actual image file
            Files.copy(file.getInputStream(), filePath);

            // Save image path in database
            ComplaintImage complaintImage = new ComplaintImage();

            complaintImage.setComplaint(complaint);

            String imageUrl =
                    "/api/complaint-images/files/" + fileName;

            complaintImage.setImageUrl(imageUrl);

            ComplaintImage savedImage =
                    complaintImageService.saveComplaintImage(complaintImage);

            return ResponseEntity.ok(savedImage);

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to upload image.");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while uploading the image.");
        }
    }

    // Display uploaded complaint image
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getComplaintImage(
            @PathVariable String filename) {

        try {

            // Prevent directory traversal
            if (!StringUtils.cleanPath(filename).equals(filename)
                    || filename.contains("..")
                    || filename.contains("/")
                    || filename.contains("\\")) {

                return ResponseEntity.badRequest().build();
            }

            Path filePath = UPLOAD_DIRECTORY.resolve(filename).normalize();

            if (!filePath.startsWith(UPLOAD_DIRECTORY)
                    || !Files.exists(filePath)
                    || !Files.isRegularFile(filePath)) {

                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);

            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

            if (contentType != null &&
                    contentType.startsWith("image/")) {

                mediaType = MediaType.parseMediaType(contentType);
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filename + "\""
                    )
                    .body(resource);

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
