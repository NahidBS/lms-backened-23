package com.library.management.controller;

import com.library.management.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private EmailNotificationService emailNotificationService;

    // Endpoint to trigger email notification
    @PostMapping("/send-email")
    public String sendEmailNotification(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        emailNotificationService.sendEmail(to, subject, text);
        return "Notification sent to " + to;
    }
}