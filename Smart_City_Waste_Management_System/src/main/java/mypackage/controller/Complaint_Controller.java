
package mypackage.controller;

import java.util.List;
import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Complaint;
import mypackage.model.User;
import mypackage.model.Area;
import mypackage.model.WasteCategory;

import mypackage.enums.ComplaintStatus;

import mypackage.repository.User_Repository;

import mypackage.service.Complaint_Service;
import mypackage.service.Area_Service;
import mypackage.service.WasteCategory_Service;

@RestController
@RequestMapping("/api/complaints")
public class Complaint_Controller {

    @Autowired
    private Complaint_Service complaintService;

    @Autowired
    private Area_Service areaService;

    @Autowired
    private WasteCategory_Service wasteCategoryService;

    @Autowired
    private User_Repository userRepository;

    // Check whether the session belongs to ADMIN
    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(
            session.getAttribute("loggedInUserRole")
        );
    }

    // Check whether the session belongs to USER
    private boolean isCitizen(HttpSession session) {
        return "USER".equals(
            session.getAttribute("loggedInUserRole")
        );
    }

    // 1. Save complaint for the logged-in CITIZEN
    @PostMapping
    public ResponseEntity<?> saveComplaint(
            @RequestBody Complaint complaint,
            HttpSession session) {

        Long userId =
            (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap(
                    "message", "Please login before reporting waste"
                ));
        }

        if (!isCitizen(session)) {
            return ResponseEntity.status(403)
                .body(Collections.singletonMap(
                    "message", "Only citizens can submit complaints"
                ));
        }

        if (complaint.getArea() == null ||
                complaint.getArea().getAreaId() == null) {

            return ResponseEntity.badRequest()
                .body(Collections.singletonMap(
                    "message", "Please select an area"
                ));
        }

        if (complaint.getCategory() == null ||
                complaint.getCategory().getCategoryId() == null) {

            return ResponseEntity.badRequest()
                .body(Collections.singletonMap(
                    "message", "Please select a waste category"
                ));
        }

        User user =
            userRepository.findById(userId).orElse(null);

        if (user == null) {
            session.invalidate();

            return ResponseEntity.status(401)
                .body(Collections.singletonMap(
                    "message", "Invalid user session"
                ));
        }

        Long areaId = complaint.getArea().getAreaId();

        Area area =
            areaService.getAreaById(areaId).orElse(null);

        if (area == null) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap(
                    "message", "Selected area does not exist"
                ));
        }

        Long categoryId =
            complaint.getCategory().getCategoryId();

        WasteCategory category =
            wasteCategoryService
                .getWasteCategoryById(categoryId)
                .orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap(
                    "message", "Selected waste category does not exist"
                ));
        }

        // Set trusted database relationships
        complaint.setUser(user);
        complaint.setArea(area);
        complaint.setCategory(category);

        // Set initial complaint values
        complaint.setComplaintId(null);
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setCreatedAt(null);
        complaint.setResolvedAt(null);

        // NEW: Citizen cannot assign a worker
        // Worker will be assigned later by ADMIN
        complaint.setWorker(null);

        Complaint savedComplaint =
            complaintService.saveComplaint(complaint);

        return ResponseEntity.ok(savedComplaint);
    }

    // 2. Get all complaints - ADMIN only
    @GetMapping
    public ResponseEntity<?> getAllComplaints(
            HttpSession session) {

        Long userId =
            (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap(
                    "message", "Please login first"
                ));
        }

        if (!isAdmin(session)) {
            return ResponseEntity.status(403)
                .body(Collections.singletonMap(
                    "message", "Administrator access required"
                ));
        }

        return ResponseEntity.ok(
            complaintService.getAllComplaints()
        );
    }

    // 3. Get complaint by ID - ADMIN only
    @GetMapping("/{complaintId}")
    public ResponseEntity<?> getComplaintById(
            @PathVariable Long complaintId,
            HttpSession session) {

        Long userId =
            (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap(
                    "message", "Please login first"
                ));
        }

        if (!isAdmin(session)) {
            return ResponseEntity.status(403)
                .body(Collections.singletonMap(
                    "message", "Administrator access required"
                ));
        }

        return complaintService.getComplaintById(complaintId)
            .map(complaint ->
                ResponseEntity.ok((Object) complaint)
            )
            .orElseGet(() ->
                ResponseEntity.status(404)
                    .body(Collections.singletonMap(
                        "message", "Complaint not found"
                    ))
            );
    }

    // 4. Get complaints of the logged-in CITIZEN
    @GetMapping("/my-complaints")
    public ResponseEntity<?> getMyComplaints(
            HttpSession session) {

        Long userId =
            (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap(
                    "message", "User is not logged in"
                ));
        }

        if (!isCitizen(session)) {
            return ResponseEntity.status(403)
                .body(Collections.singletonMap(
                    "message", "Citizen access required"
                ));
        }

        List<Complaint> complaints =
            complaintService.getComplaintsByUserId(userId);

        return ResponseEntity.ok(complaints);
    }
}
