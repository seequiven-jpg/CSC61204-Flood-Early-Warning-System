package com.group.floodwarning.service;

/** Delivery boundary for flood alerts. Implement with an email/SMS provider for external delivery. */
public interface NotificationSender {
    void send(String recipientEmail, String subject, String message);
}
