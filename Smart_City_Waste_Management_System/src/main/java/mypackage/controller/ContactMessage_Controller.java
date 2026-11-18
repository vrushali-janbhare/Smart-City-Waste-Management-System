
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.ContactMessage;
import mypackage.service.ContactMessage_Service;

@RestController
@RequestMapping("/api/contact-messages")
@CrossOrigin(origins = "*")
public class ContactMessage_Controller {

    @Autowired
    private ContactMessage_Service service;

    // POST: Submit a new contact message
    @PostMapping
    public ResponseEntity<ContactMessage> saveMessage(
            @RequestBody ContactMessage message) {

        ContactMessage saved = service.saveMessage(message);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // GET: Get all contact messages
    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAllMessages() {

        return ResponseEntity.ok(service.getAllMessages());
    }

    // GET: Get contact message by ID
    @GetMapping("/{id}")
    public ResponseEntity<ContactMessage> getMessageById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getMessageById(id));
    }

    // PUT: Update a contact message
    @PutMapping("/{id}")
    public ResponseEntity<ContactMessage> updateMessage(
            @PathVariable Long id,
            @RequestBody ContactMessage message) {

        ContactMessage updated = service.updateMessage(id, message);

        return ResponseEntity.ok(updated);
    }

    // DELETE: Delete a contact message
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable Long id) {

        service.deleteMessage(id);

        return ResponseEntity.ok("Contact message deleted successfully");
    }
}
