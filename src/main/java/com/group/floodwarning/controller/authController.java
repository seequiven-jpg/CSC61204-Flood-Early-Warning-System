package com.group.floodwarning.controller;

import com.group.floodwarning.model.User;
import com.group.floodwarning.model.User.Role;
import com.group.floodwarning.service.authService;
import com.group.floodwarning.view.DashboardView;
import com.group.floodwarning.view.loginFrame;
import com.group.floodwarning.view.registerFrame;

import javax.swing.JFrame;
import java.io.IOException;
import java.util.Arrays;

public final class authController {
    private final authService service;
    private JFrame currentView;

    public authController(authService service) {
        this.service = service;
    }

    public void showResidentLogin() {
        showLogin(Role.RESIDENT, null);
    }

    public void showAdminLogin() {
        showLogin(Role.ADMIN, null);
    }

    public void showRegister() {
        registerFrame view = new registerFrame(this);
        changeView(view);
        view.showForm();
    }

    public void showRegistrationSuccess() {
        showLogin(Role.RESIDENT, "Registration is successful!");
    }

    public void registerResident(String name, String email, char[] password, char[] confirmation)
            throws IOException {
        try {
            service.registerResident(name, email, password, confirmation);
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');
        }
    }

    public User login(String email, char[] password, Role role) throws IOException {
        try {
            return service.login(email, password, role);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public void showDashboard(User user) {
        DashboardView view = new DashboardView();
        DashboardController dashboard = new DashboardController(view);
        dashboard.showMessage("Welcome, " + user.name() + " (" + user.role() + ")");
        changeView(view);
        view.setVisible(true);
    }

    private void showLogin(Role role, String message) {
        loginFrame view = new loginFrame(this, role, message);
        changeView(view);
        view.showForm();
    }

    private void changeView(JFrame nextView) {
        if (currentView != null) {
            currentView.dispose();
        }
        currentView = nextView;
    }
}
