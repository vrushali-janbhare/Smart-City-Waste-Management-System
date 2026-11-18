
package mypackage.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.ContactMessage;
import mypackage.service.ContactMessage_Service;

@RestController
@RequestMapping("/api/contact-messages")
public class ContactMessage_Controller {

    @Autowired
    private ContactMessage_Service service;

    // ==========================================
    // CHECK ADMIN AUTHORIZATION
    // ==========================================

    private ResponseEntity<?> checkAdmin(HttpSession session) {

        Object userId =
                session.getAttribute("loggedInUserId");

        Object role =
                session.getAttribute("loggedInUserRole");

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap(
                            "message",
                            "Please login first"));
        }

        if (!"ADMIN".equals(role)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap(
                            "message",
                            "Access denied. ADMIN only"));
        }

        return null;
    }

    // ==========================================
    // 1. SUBMIT CONTACT MESSAGE - PUBLIC
    // ==========================================

    @PostMapping
    public ResponseEntity<?> saveMessage(
            @RequestBody ContactMessage message) {

        try {

            ContactMessage saved =
                    service.saveMessage(message);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Collections.singletonMap(
                            "message",
                            "Your message has been submitted successfully"));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap(
                            "message",
                            e.getMessage()));
        }
    }

    // ==========================================
    // 2. GET ALL MESSAGES - ADMIN ONLY
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getAllMessages(
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        List<ContactMessage> messages =
                service.getAllMessages();

        return ResponseEntity.ok(messages);
    }

    // ==========================================
    // 3. GET MESSAGE BY ID - ADMIN ONLY
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getMessageById(
            @PathVariable Long id,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        try {

            return ResponseEntity.ok(
                    service.getMessageById(id));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap(
                            "message",
                            e.getMessage()));
        }
    }

    // ==========================================
    // 4. UPDATE MESSAGE STATUS - ADMIN ONLY
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        if (request == null ||
                request.get("status") == null) {

            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap(
                            "message",
                            "Message status is required"));
        }

        try {

            ContactMessage update =
                    new ContactMessage();

            update.setStatus(request.get("status"));

            ContactMessage updated =
                    service.updateMessage(id, update);

            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {

            HttpStatus status =
                    e.getMessage() != null &&
                    e.getMessage().startsWith(
                            "Contact message not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity
                    .status(status)
                    .body(Collections.singletonMap(
                            "message",
                            e.getMessage()));
        }
    }

    // ==========================================
    // 5. DELETE MESSAGE - ADMIN ONLY
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Long id,
            HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        try {

            service.deleteMessage(id);

            return ResponseEntity.ok(
                    Collections.singletonMap(
                            "message",
                            "Contact message deleted successfully"));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap(
                            "message",
                            e.getMessage()));
        }
    }
}
