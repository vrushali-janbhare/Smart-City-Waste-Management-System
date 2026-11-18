
package mypackage.service;

import java.util.List;
import mypackage.model.ContactMessage;

public interface ContactMessage_Service {

    ContactMessage saveMessage(ContactMessage message);

    List<ContactMessage> getAllMessages();

    ContactMessage getMessageById(Long id);

    ContactMessage updateMessage(Long id, ContactMessage message);

    void deleteMessage(Long id);
}
