package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Notifications;
import mypackage.service.Notifications_Service;

@RestController
@RequestMapping("/api/notifications")
public class Notifications_Controller {

    @Autowired
    private Notifications_Service notificationsService;

    // Add Notification
    @PostMapping
    public Notifications addNotification(@RequestBody Notifications notification) {
        return notificationsService.addNotification(notification);
    }

    // Get All Notifications
    @GetMapping
    public List<Notifications> getAllNotifications() {
        return notificationsService.getAllNotifications();
    }

    // Get Notification By ID
    @GetMapping("/{id}")
    public Notifications getNotificationById(@PathVariable Long id) {
        return notificationsService.getNotificationById(id);
    }

    // Update Notification
    @PutMapping("/{id}")
    public Notifications updateNotification(
            @PathVariable Long id,
            @RequestBody Notifications notification) {

        return notificationsService.updateNotification(id, notification);
    }

    // Delete Notification
    @DeleteMapping("/{id}")
    public String deleteNotification(@PathVariable Long id) {

        notificationsService.deleteNotification(id);

        return "Notification Deleted Successfully!";
    }
}