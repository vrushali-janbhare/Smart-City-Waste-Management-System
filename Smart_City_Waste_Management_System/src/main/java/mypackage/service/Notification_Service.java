
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Notification;
import mypackage.repository.Notification_Repository;

@Service
public class Notification_Service {

    @Autowired
    private Notification_Repository notificationRepository;

    // Insert or update notification
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    // Get all notifications
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    // Get notification by ID
    public Optional<Notification> getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }

    // Delete notification by ID
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
