
package mypackage.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.dto.WorkerProfileUpdateDTO;
import mypackage.dto.WorkerRegistrationDTO;
import mypackage.dto.WorkerResponseDTO;
import mypackage.dto.WorkerUpdateDTO;

import mypackage.enums.ComplaintStatus;
import mypackage.service.Worker_Service;

@RestController
@RequestMapping("/api/workers")
public class Worker_Controller {

    @Autowired
    private Worker_Service workerService;


    // ==========================================
    // CHECK ADMIN AUTHORIZATION
    // ==========================================

    private ResponseEntity<?> checkAdmin(HttpSession session) {

        Object userId = session.getAttribute("loggedInUserId");
        Object role = session.getAttribute("loggedInUserRole");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please login first");
        }

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. ADMIN only");
        }

        return null;
    }


    // ==========================================
    // CHECK WORKER AUTHORIZATION
    // ==========================================

    private ResponseEntity<?> checkWorker(HttpSession session) {

        Object userId = session.getAttribute("loggedInUserId");
        Object role = session.getAttribute("loggedInUserRole");

        if (!(userId instanceof Long)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap(
                            "message", "Please login first"));
        }

        if (!"WORKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap(
                            "message", "Access denied. WORKER only"));
        }

        return null;
    }


    // ==========================================
    // 1. REGISTER WORKER - ADMIN ONLY
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?> registerWorker(
            @RequestBody WorkerRegistrationDTO request,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        try {

            WorkerResponseDTO savedWorker =
                    workerService.registerWorkerWithDetails(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedWorker);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());

        } catch (IllegalStateException e) {

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }


    // ==========================================
    // 2. GET ALL WORKERS - ADMIN ONLY
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getAllWorkers(HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        List<WorkerResponseDTO> workers =
                workerService.getAllWorkerDetails();

        return ResponseEntity.ok(workers);
    }


    // ==========================================
    // 3. GET WORKER BY ID - ADMIN ONLY
    // ==========================================

    @GetMapping("/{workerId:\\d+}")
    public ResponseEntity<?> getWorkerById(
            @PathVariable Long workerId,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        return workerService.getWorkerDetailsById(workerId)
                .map(worker -> ResponseEntity.ok((Object) worker))
                .orElseGet(() -> ResponseEntity.status(
                        HttpStatus.NOT_FOUND)
                        .body("Worker not found"));
    }


    // ==========================================
    // 4. UPDATE WORKER - ADMIN ONLY
    // ==========================================

    @PutMapping("/{workerId:\\d+}")
    public ResponseEntity<?> updateWorker(
            @PathVariable Long workerId,
            @RequestBody WorkerUpdateDTO request,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        try {

            WorkerResponseDTO updatedWorker =
                    workerService.updateWorker(workerId, request);

            return ResponseEntity.ok(updatedWorker);

        } catch (IllegalArgumentException e) {

            if ("Worker not found".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(e.getMessage());
            }

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }


    // ==========================================
    // 5. DELETE WORKER - ADMIN ONLY
    // ==========================================

    @DeleteMapping("/{workerId:\\d+}")
    public ResponseEntity<?> deleteWorker(
            @PathVariable Long workerId,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("Worker deletion is not implemented yet");
    }


    // ==========================================
    // 6. GET LOGGED-IN WORKER PROFILE
    // ==========================================

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpSession session) {

        ResponseEntity<?> error = checkWorker(session);

        if (error != null) {
            return error;
        }

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        try {

            return ResponseEntity.ok(
                    workerService.getMyWorkerProfile(userId));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap(
                            "message", e.getMessage()));
        }
    }


    // ==========================================
    // 7. GET WORKER DASHBOARD STATISTICS
    // ==========================================

    @GetMapping("/me/dashboard")
    public ResponseEntity<?> getMyDashboard(HttpSession session) {

        ResponseEntity<?> error = checkWorker(session);

        if (error != null) {
            return error;
        }

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        try {

            return ResponseEntity.ok(
                    workerService.getMyDashboardStats(userId));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap(
                            "message", e.getMessage()));
        }
    }


    // ==========================================
    // 8. GET WORKER'S ASSIGNED COMPLAINTS
    // ==========================================

    @GetMapping("/me/complaints")
    public ResponseEntity<?> getMyComplaints(HttpSession session) {

        ResponseEntity<?> error = checkWorker(session);

        if (error != null) {
            return error;
        }

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        try {

            return ResponseEntity.ok(
                    workerService.getMyComplaints(userId));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap(
                            "message", e.getMessage()));
        }
    }


    // ==========================================
    // 9. UPDATE ASSIGNED COMPLAINT STATUS
    // ==========================================

    @PutMapping("/me/complaints/{complaintId}/status")
    public ResponseEntity<?> updateMyComplaintStatus(
            @PathVariable Long complaintId,
            @RequestBody Map<String, String> request,
            HttpSession session) {

        ResponseEntity<?> error = checkWorker(session);

        if (error != null) {
            return error;
        }

        if (request == null || request.get("status") == null) {

            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap(
                            "message", "Complaint status is required"));
        }

        ComplaintStatus newStatus;

        try {

            newStatus = ComplaintStatus.valueOf(
                    request.get("status").trim().toUpperCase());

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap(
                            "message", "Invalid complaint status"));
        }

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        try {

            return ResponseEntity.ok(
                    workerService.updateMyComplaintStatus(
                            userId,
                            complaintId,
                            newStatus));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap(
                            "message", e.getMessage()));
        }
    }


    // ==========================================
    // 10. GET WORKER'S COLLECTION SCHEDULES
    // ==========================================

    @GetMapping("/me/schedules")
    public ResponseEntity<?> getMySchedules(HttpSession session) {

        ResponseEntity<?> error = checkWorker(session);

        if (error != null) {
            return error;
        }

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        try {

            return ResponseEntity.ok(
                    workerService.getMySchedules(userId));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap(
                            "message", e.getMessage()));
        }
    }


    // ==========================================
    // 11. UPDATE LOGGED-IN WORKER PROFILE
    // ==========================================

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateMyProfile(
            @RequestBody WorkerProfileUpdateDTO request,
            HttpSession session) {

        ResponseEntity<?> error = checkWorker(session);

        if (error != null) {
            return error;
        }

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        try {

            WorkerResponseDTO updatedProfile =
                    workerService.updateMyWorkerProfile(
                            userId,
                            request
                    );

            return ResponseEntity.ok(updatedProfile);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap(
                            "message", e.getMessage()));
        }
    }
}
