package floodwarning.service;

import floodwarning.model.User;
import floodwarning.model.User.Role;
import floodwarning.repository.userRepository;
import floodwarning.repository.userRepository.AddResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public final class authService {
    private static final Pattern EMAIL = Pattern.compile(
            "[a-z0-9_%+\\-]+(?:\\.[a-z0-9_%+\\-]+)*@[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"
            + "(?:\\.[a-z0-9](?:[a-z0-9-]*[a-z0-9])?)+");
    private static final int MAX_PASSWORD_LENGTH = 128;
    private final userRepository repository;

    public authService(userRepository repository) {
        this.repository = repository;
    }

    public void registerResident(String name, String email, char[] password, char[] confirmation)
            throws IOException {
        createAccount(name, email, password, confirmation, Role.RESIDENT);
    }

    public void createFirstAdmin(String name, String email, char[] password, char[] confirmation)
            throws IOException {
        createAccount(name, email, password, confirmation, Role.ADMIN);
    }

    public User login(String email, char[] password, Role requiredRole) throws IOException {
        String normalizedEmail = validateEmail(email);
        if (password.length == 0 || password.length > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Enter your password (up to 128 characters).");
        }

        User user = repository.findByEmail(normalizedEmail).orElse(null);
        if (user == null || user.role() != requiredRole || !passwordUtil.matches(password, user.passwordHash())) {
            throw new IllegalArgumentException("Incorrect email or password, or this account does not have access to this sign-in page.");
        }
        return user;
    }

    private void createAccount(String name, String email, char[] password, char[] confirmation, Role role)
            throws IOException {
        String cleanName = name.strip();
        if (cleanName.isBlank() || cleanName.length() > 100 || cleanName.indexOf('|') >= 0
                || cleanName.codePoints().anyMatch(Character::isISOControl)
                || cleanName.contains("\u2028") || cleanName.contains("\u2029")) {
            throw new IllegalArgumentException("Enter a valid name of up to 100 characters without the | symbol.");
        }
        String normalizedEmail = validateEmail(email);
        if (password.length < 8 || password.length > MAX_PASSWORD_LENGTH
                || isWhitespace(password)) {
            throw new IllegalArgumentException("Use 8 to 128 characters. A password cannot contain only whitespace.");
        }
        if (!Arrays.equals(password, confirmation)) {
            throw new IllegalArgumentException("The passwords do not match.");
        }

        if (repository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("This email address is already registered.");
        }

        User user = new User(cleanName, normalizedEmail, role, passwordUtil.hash(password));
        AddResult result = repository.add(user);
        if (result == AddResult.DUPLICATE_EMAIL) {
            throw new IllegalArgumentException("This email address is already registered.");
        }
        if (result == AddResult.ADMIN_ALREADY_EXISTS) {
            throw new IllegalArgumentException("The first admin account already exists.");
        }
    }

    private static boolean isWhitespace(char[] password) {
        for (char character : password) {
            if (!Character.isWhitespace(character)) {
                return false;
            }
        }
        return true;
    }

    private static String validateEmail(String email) {
        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
        if (normalizedEmail.length() > 254 || !EMAIL.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Enter a valid email address, such as name@example.com.");
        }
        return normalizedEmail;
    }
}
