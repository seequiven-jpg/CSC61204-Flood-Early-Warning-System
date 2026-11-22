package com.group.floodwarning.view;

import com.group.floodwarning.controller.authController;

import javax.swing.*;

public final class registerFrame extends AuthForm {
    public registerFrame(authController controller) {
        super("Create an account");
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JPasswordField password = new JPasswordField();
        JPasswordField confirmation = new JPasswordField();

        field("Full Name", name);
        field("Email Address", email);
        field("Password (8–128 characters)", password);
        field("Confirm Password", confirmation);
        submit("Register", () -> {
            String enteredName = name.getText();
            String enteredEmail = email.getText();
            char[] enteredPassword = password.getPassword();
            char[] enteredConfirmation = confirmation.getPassword();
            password.setText("");
            confirmation.setText("");
            runTask(() -> {
                controller.registerResident(enteredName, enteredEmail, enteredPassword, enteredConfirmation);
                return null;
            }, ignored -> controller.showRegistrationSuccess());
        });
        link("Back to resident login", controller::showResidentLogin);
    }

    public void showForm() {
        open();
    }
}
