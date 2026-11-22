package floodwarning.view;

import floodwarning.controller.authController;
import floodwarning.model.User.Role;

import javax.swing.*;

public final class loginFrame extends AuthForm {
    public loginFrame(authController controller, Role role, String message) {
        super(role == Role.ADMIN ? "Admin sign in" : "Welcome Back");
        JTextField email = new JTextField();
        JPasswordField password = new JPasswordField();

        field("Email Address", email);
        field("Password", password);
        submit(role == Role.ADMIN ? "Sign in as admin" : "Login", () -> {
            String enteredEmail = email.getText();
            char[] enteredPassword = password.getPassword();
            password.setText("");
            runTask(() -> controller.login(enteredEmail, enteredPassword, role), controller::showDashboard);
        });

        if (role == Role.ADMIN) {
            link("Back to resident login", controller::showResidentLogin);
        } else {
            link("Don't have an account yet? Sign up.", controller::showRegister);
            link("Admin login", controller::showAdminLogin);
        }

        if (message != null) {
            showFeedback(message, false);
        }
    }

    public void showForm() {
        open();
    }
}
