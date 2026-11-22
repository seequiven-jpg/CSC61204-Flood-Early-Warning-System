package floodwarning.app;

import floodwarning.controller.authController;
import floodwarning.repository.userRepository;
import floodwarning.service.authService;

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
