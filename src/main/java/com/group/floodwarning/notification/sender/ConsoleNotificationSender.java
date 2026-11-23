package com.group.floodwarning.notification.sender;

/** Local demo sender; writes the notification to the application console. */
public final class ConsoleNotificationSender implements NotificationSender {
    @Override
    public void send(String recipientEmail, String subject, String message) {
        System.out.println("Notification for: " + recipientEmail);
        System.out.println("Subject: " + subject);
        System.out.println(message);
        System.out.println("------------------------------");
    }
}
