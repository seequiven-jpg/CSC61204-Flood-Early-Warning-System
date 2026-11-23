package com.group.floodwarning.view;

import com.group.floodwarning.controller.authController;
import com.group.floodwarning.repository.userRepository;
import com.group.floodwarning.service.authService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class AuthFlowTest {
    @TempDir Path projectDirectory;

    @Test
    void followsTheResidentAndAdminFormsThroughToTheDashboard() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Swing requires a graphical environment.");
        authService service = new authService(new userRepository(projectDirectory.resolve("data/users.txt")));
        char[] adminPassword = "fixture-officer".toCharArray();
        service.createFirstAdmin("Officer Demo", "officer@example.com", adminPassword, adminPassword);
        authController controller = new authController(service);

        try {
            SwingUtilities.invokeAndWait(controller::showResidentLogin);
            onEdt(() -> click("Don't have an account yet? Sign up."));
            onEdt(() -> {
                fill("Aisyah", "aisyah@example.com", "fixture-resident", "fixture-resident");
                return click("Register");
            });
            await(() -> hasLabel("Registration is successful!"));

            onEdt(() -> click("Don't have an account yet? Sign up."));
            onEdt(() -> {
                fill("Aisyah", "AISYAH@example.com", "fixture-resident", "fixture-resident");
                return click("Register");
            });
            await(() -> hasLabel("This email address is already registered"));
            onEdt(() -> click("Back to resident login"));

            onEdt(() -> click("Admin login"));
            assertTrue(onEdt(() -> hasLabel("Admin sign in")));
            assertFalse(onEdt(() -> components(visibleFrame(), JButton.class).stream()
                    .anyMatch(button -> button.getAccessibleContext().getAccessibleName().contains("Sign up"))));
            onEdt(() -> {
                fill("aisyah@example.com", "fixture-resident");
                return click("Sign in as admin");
            });
            await(() -> hasLabel("this account does not have access"));
            onEdt(() -> click("Back to resident login"));

            onEdt(() -> {
                fill("aisyah@example.com", "wrong password");
                return click("Login");
            });
            await(() -> hasLabel("Incorrect email or password"));
            onEdt(() -> {
                fill("aisyah@example.com", "fixture-resident");
                return click("Login");
            });
            await(() -> visibleFrame() instanceof DashboardView && hasLabel("Aisyah (RESIDENT)"));

            SwingUtilities.invokeAndWait(controller::showResidentLogin);
            onEdt(() -> click("Admin login"));
            onEdt(() -> {
                fill("officer@example.com", "fixture-officer");
                return click("Sign in as admin");
            });
            await(() -> visibleFrame() instanceof DashboardView && hasLabel("Officer Demo (ADMIN)"));
        } finally {
            SwingUtilities.invokeAndWait(() -> Arrays.stream(Frame.getFrames()).forEach(Frame::dispose));
        }
    }

    private static void fill(String... values) {
        List<JTextField> fields = components(visibleFrame(), JTextField.class);
        assertEquals(values.length, fields.size());
        for (int i = 0; i < values.length; i++) {
            fields.get(i).setText(values[i]);
        }
    }

    private static JButton click(String label) {
        JButton button = components(visibleFrame(), JButton.class).stream()
                .filter(candidate -> label.equals(candidate.getAccessibleContext().getAccessibleName()))
                .findFirst().orElseThrow(() -> new AssertionError("Button not found: " + label));
        assertTrue(button.isEnabled());
        button.doClick(0);
        return button;
    }

    private static boolean hasLabel(String text) {
        return components(visibleFrame(), JLabel.class).stream()
                .map(JLabel::getText).anyMatch(label -> label != null && label.contains(text));
    }

    private static Frame visibleFrame() {
        return Arrays.stream(Frame.getFrames()).filter(Frame::isShowing).findFirst().orElseThrow();
    }

    private static <T extends Component> List<T> components(Component component, Class<T> type) {
        List<T> result = new ArrayList<>();
        if (type.isInstance(component)) {
            result.add(type.cast(component));
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                result.addAll(components(child, type));
            }
        }
        return result;
    }

    private static void await(Supplier<Boolean> condition) throws Exception {
        long deadline = System.nanoTime() + 5_000_000_000L;
        while (System.nanoTime() < deadline) {
            if (onEdt(condition)) {
                return;
            }
            Thread.sleep(30);
        }
        fail("The expected form or feedback did not appear.");
    }

    private static <T> T onEdt(Supplier<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action::get);
        SwingUtilities.invokeAndWait(task);
        return task.get();
    }
}
