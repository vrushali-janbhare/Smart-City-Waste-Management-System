
package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.ContactMessage;
import mypackage.repository.ContactMessage_Repository;

@Service
public class ContactMessage_ServiceImpl
        implements ContactMessage_Service {

    @Autowired
    private ContactMessage_Repository repository;

    @Override
    public ContactMessage saveMessage(ContactMessage message) {

        message.setId(null);
        message.setStatus("NEW");
        message.setCreatedAt(null);

        return repository.save(message);
    }

    @Override
    public List<ContactMessage> getAllMessages() {
        return repository.findAll();
    }

    @Override
    public ContactMessage getMessageById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Contact message not found with id: " + id));
    }

    @Override
    public ContactMessage updateMessage(Long id, ContactMessage message) {

        ContactMessage existing = getMessageById(id);

        existing.setFullName(message.getFullName());
        existing.setEmail(message.getEmail());
        existing.setSubject(message.getSubject());
        existing.setEnquiryType(message.getEnquiryType());
        existing.setMessage(message.getMessage());

        if (message.getStatus() != null &&
                !message.getStatus().trim().isEmpty()) {
            existing.setStatus(message.getStatus());
        }

        return repository.save(existing);
    }

    @Override
    public void deleteMessage(Long id) {

        ContactMessage existing = getMessageById(id);
        repository.delete(existing);
    }
}
