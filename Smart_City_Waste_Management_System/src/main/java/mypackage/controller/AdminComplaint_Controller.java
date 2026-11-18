
package mypackage.controller;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.User;
import mypackage.repository.User_Repository;
import mypackage.service.AdminComplaint_Service;

@RestController
@RequestMapping("/api/admin/complaints")
public class AdminComplaint_Controller {

    @Autowired
    private AdminComplaint_Service adminComplaintService;

    @Autowired
    private User_Repository userRepository;

    // Check ADMIN session
    private ResponseEntity<?> checkAdmin(HttpSession session) {

        Long userId =
            (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap(
                    "message", "Please login first"
                ));
        }

        if (!"ADMIN".equals(
                session.getAttribute("loggedInUserRole"))) {

            return ResponseEntity.status(403)
                .body(Collections.singletonMap(
                    "message", "Administrator access required"
                ));
        }

        return null;
    }

    // 1. Get all complaints
    @GetMapping
    public ResponseEntity<?> getAllComplaints(
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);
        if (error != null) return error;

        return ResponseEntity.ok(
            adminComplaintService.getAllComplaints()
        );
    }

    // 2. Get complaint details
    @GetMapping("/{complaintId}")
    public ResponseEntity<?> getComplaintById(
            @PathVariable Long complaintId,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);
        if (error != null) return error;

        return adminComplaintService
            .getComplaintById(complaintId)
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

    // 3. Get workers for assignment
    @GetMapping("/workers")
    public ResponseEntity<?> getWorkers(
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);
        if (error != null) return error;

        return ResponseEntity.ok(
            adminComplaintService.getAllWorkers()
        );
    }

    // 4. Assign or reassign worker
    @PutMapping("/{complaintId}/assign")
    public ResponseEntity<?> assignWorker(
            @PathVariable Long complaintId,
            @RequestBody Map<String, Long> request,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);
        if (error != null) return error;

        Long workerId = request.get("workerId");

        if (workerId == null) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap(
                    "message", "Worker ID is required"
                ));
        }

        Long adminId =
            (Long) session.getAttribute("loggedInUserId");

        User admin =
            userRepository.findById(adminId).orElse(null);

        if (admin == null) {
            session.invalidate();
            return ResponseEntity.status(401)
                .body(Collections.singletonMap(
                    "message", "Invalid administrator session"
                ));
        }

        try {
            return ResponseEntity.ok(
                adminComplaintService.assignWorker(
                    complaintId, workerId, admin
                )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap(
                    "message", e.getMessage()
                ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                .body(Collections.singletonMap(
                    "message", e.getMessage()
                ));
        }
    }

    // 5. Reject complaint
    @PutMapping("/{complaintId}/reject")
    public ResponseEntity<?> rejectComplaint(
            @PathVariable Long complaintId,
            @RequestBody Map<String, String> request,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);
        if (error != null) return error;

        String reason = request.get("reason");

        Long adminId =
            (Long) session.getAttribute("loggedInUserId");

        User admin =
            userRepository.findById(adminId).orElse(null);

        if (admin == null) {
            session.invalidate();
            return ResponseEntity.status(401)
                .body(Collections.singletonMap(
                    "message", "Invalid administrator session"
                ));
        }

        try {
            return ResponseEntity.ok(
                adminComplaintService.rejectComplaint(
                    complaintId, reason, admin
                )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap(
                    "message", e.getMessage()
                ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                .body(Collections.singletonMap(
                    "message", e.getMessage()
                ));
        }
    }
}
