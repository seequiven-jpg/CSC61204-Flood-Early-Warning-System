package com.group.floodwarning.app;

import com.group.floodwarning.controller.authController;
import com.group.floodwarning.repository.userRepository;
import com.group.floodwarning.service.authService;

import javax.swing.SwingUtilities;
import java.nio.file.Path;

public final class Main {
    static final Path USERS_FILE = Path.of("data", "users.txt");

    private Main() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            authService service = new authService(new userRepository(USERS_FILE));
            new authController(service).showResidentLogin();
        });
    }
}
