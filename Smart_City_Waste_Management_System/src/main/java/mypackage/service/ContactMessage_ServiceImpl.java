
package mypackage.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.ContactMessage;
import mypackage.repository.ContactMessage_Repository;

@Service
public class ContactMessage_ServiceImpl
        implements ContactMessage_Service {

    @Autowired
    private ContactMessage_Repository repository;

    // PUBLIC: Submit a contact message
    @Override
    public ContactMessage saveMessage(ContactMessage message) {

        if (message == null) {
            throw new IllegalArgumentException(
                    "Contact message is required");
        }

        if (isBlank(message.getFullName())
                || message.getFullName().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Valid full name is required (maximum 100 characters)");
        }

        if (isBlank(message.getEmail())
                || message.getEmail().trim().length() > 150
                || !message.getEmail().trim().matches(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Valid email address is required");
        }

        if (isBlank(message.getMessage())) {
            throw new IllegalArgumentException(
                    "Message cannot be empty");
        }

        if (message.getSubject() != null
                && message.getSubject().length() > 200) {
            throw new IllegalArgumentException(
                    "Subject cannot exceed 200 characters");
        }

        if (message.getEnquiryType() != null
                && message.getEnquiryType().length() > 50) {
            throw new IllegalArgumentException(
                    "Enquiry type cannot exceed 50 characters");
        }

        message.setId(null);
        message.setFullName(message.getFullName().trim());
        message.setEmail(message.getEmail().trim());
        message.setMessage(message.getMessage().trim());
        message.setStatus("NEW");
        message.setCreatedAt(null);

        return repository.save(message);
    }

    // ADMIN: View all contact messages
    @Override
    public List<ContactMessage> getAllMessages() {
        return repository.findAll();
    }

    // ADMIN: View a specific contact message
    @Override
    public ContactMessage getMessageById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Message ID is required");
        }

        return repository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Contact message not found with id: " + id));
    }

    // ADMIN: Update only message status
    @Override
    public ContactMessage updateMessage(
            Long id, ContactMessage message) {

        ContactMessage existing = getMessageById(id);

        if (message == null || isBlank(message.getStatus())) {
            throw new IllegalArgumentException(
                    "Message status is required");
        }

        String status = message.getStatus()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!status.equals("NEW")
                && !status.equals("READ")
                && !status.equals("RESOLVED")) {
            throw new IllegalArgumentException(
                    "Status must be NEW, READ or RESOLVED");
        }

        existing.setStatus(status);

        return repository.save(existing);
    }

    // ADMIN: Delete a contact message
    @Override
    public void deleteMessage(Long id) {

        ContactMessage existing = getMessageById(id);

        repository.delete(existing);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
