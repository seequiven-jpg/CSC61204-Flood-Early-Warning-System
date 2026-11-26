package com.group.floodwarning.app;

import com.group.floodwarning.repository.userRepository;
import com.group.floodwarning.service.authService;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;

public final class CreateFirstAdmin {
    private CreateFirstAdmin() {}

    public static void main(String[] args) {
        Console console = System.console();
        if (console == null) {
            System.err.println("Run this command in the IntelliJ Terminal or PowerShell so you can enter your password without displaying it.");
            System.exit(1);
        }
        if (!createAdmin(console)) {
            System.exit(1);
        }
    }

    private static boolean createAdmin(Console console) {
        char[] password = null;
        char[] confirmation = null;
        try {
            String name = console.readLine("Admin name: ");
            if (name == null) {
                return false;
            }
            String email = console.readLine("Admin email: ");
            if (email == null) {
                return false;
            }
            password = console.readPassword("Admin password: ");
            confirmation = console.readPassword("Confirm password: ");
            if (password == null || confirmation == null) {
                return false;
            }

            authService service = new authService(new userRepository(Main.USERS_FILE));
            service.createFirstAdmin(name, email, password, confirmation);
            console.printf("Admin account created in %s.%n", Main.USERS_FILE.toAbsolutePath());
            return true;
        } catch (IllegalArgumentException e) {
            console.printf("%s%n", e.getMessage());
            return false;
        } catch (IOException e) {
            console.printf("Could not read or save account data.%n");
            return false;
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            if (confirmation != null) {
                Arrays.fill(confirmation, '\0');
            }
        }
    }
}
