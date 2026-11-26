package com.group.floodwarning.notification.sender;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleNotificationSenderTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void send_validRecipientSubjectAndMessage_writesFormattedLinesToConsole() {
        ConsoleNotificationSender sender = new ConsoleNotificationSender();

        sender.send("resident@example.com", "Flood DANGER alert: Gombak", "Evacuate immediately.");

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertAll(
            () -> assertTrue(output.contains("Notification for: resident@example.com")),
            () -> assertTrue(output.contains("Subject: Flood DANGER alert: Gombak")),
            () -> assertTrue(output.contains("Evacuate immediately."))
        );
    }
}
