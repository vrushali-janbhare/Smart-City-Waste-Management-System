
package mypackage.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.dto.WorkerRegistrationDTO;
import mypackage.dto.WorkerResponseDTO;
import mypackage.dto.WorkerUpdateDTO;
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

    // Deletion is disabled to protect linked
    // complaints and collection records.

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
}
