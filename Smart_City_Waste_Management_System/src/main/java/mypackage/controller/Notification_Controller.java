
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Notification;
import mypackage.service.Notification_Service;

@RestController
@RequestMapping("/api/notifications")
public class Notification_Controller {

    @Autowired
    private Notification_Service notificationService;

    // Insert new notification
    @PostMapping
    public Notification saveNotification(
            @RequestBody Notification notification) {

        return notificationService.saveNotification(notification);
    }

    // Get all notifications
    @GetMapping
    public List<Notification> getAllNotifications() {

        return notificationService.getAllNotifications();
    }

    // Get notification by ID
    @GetMapping("/{notificationId}")
    public ResponseEntity<Notification> getNotificationById(
            @PathVariable Long notificationId) {

        return notificationService.getNotificationById(notificationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update notification by ID
    @PutMapping("/{notificationId}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable Long notificationId,
            @RequestBody Notification notification) {

        return notificationService.getNotificationById(notificationId)
                .map(existingNotification -> {

                    // Preserve primary key
                    notification.setNotificationId(notificationId);

                    // Preserve existing creation date
                    if (notification.getCreatedAt() == null) {
                        notification.setCreatedAt(
                                existingNotification.getCreatedAt());
                    }

                    return ResponseEntity.ok(
                            notificationService.saveNotification(
                                    notification));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete notification by ID
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId) {

        return notificationService.getNotificationById(notificationId)
                .map(existingNotification -> {

                    notificationService.deleteNotification(notificationId);

                    return ResponseEntity.noContent()
                            .<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
