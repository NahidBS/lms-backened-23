package com.library.management.controller;

import com.library.management.entity.Notification;
import com.library.management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class InAppNotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/create")
    public String createNotification(@RequestParam String message, @RequestParam String recipient) {
        notificationService.createNotification(message, recipient);
        return "Notification created";
    }

    @GetMapping("/unread/{recipient}")
    public List<Notification> getUnreadNotifications(@PathVariable String recipient) {
        return notificationService.getUnreadNotifications(recipient);
    }

    @PostMapping("/mark-as-read/{id}")
    public String markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "Notification marked as read";
    }

}
