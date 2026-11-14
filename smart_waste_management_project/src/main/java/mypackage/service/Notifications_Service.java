package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Notifications;
import mypackage.repository.Notifications_Repository;

@Service
public class Notifications_Service {

    @Autowired
    private Notifications_Repository notificationsRepository;

    // Add Notification
    public Notifications addNotification(Notifications notification) {
        return notificationsRepository.save(notification);
    }

    // Get All Notifications
    public List<Notifications> getAllNotifications() {
        return notificationsRepository.findAll();
    }

    // Get Notification By ID
    public Notifications getNotificationById(Long id) {
        return notificationsRepository.findById(id).orElse(null);
    }

    // Update Notification
    public Notifications updateNotification(Long id, Notifications notification) {

        Notifications existingNotification =
                notificationsRepository.findById(id).orElse(null);

        if (existingNotification != null) {

            existingNotification.setUsers(notification.getUsers());
            existingNotification.setTitle(notification.getTitle());
            existingNotification.setMessage(notification.getMessage());
            existingNotification.setNotification_types(notification.getNotification_types());
            existingNotification.setIs_read(notification.isIs_read());

            return notificationsRepository.save(existingNotification);
        }

        return null;
    }

    // Delete Notification
    public void deleteNotification(Long id) {
        notificationsRepository.deleteById(id);
    }
}