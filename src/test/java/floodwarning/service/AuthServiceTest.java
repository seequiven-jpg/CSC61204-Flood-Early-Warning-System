package floodwarning.service;

import floodwarning.model.User;
import floodwarning.model.User.Role;
import floodwarning.repository.userRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    @TempDir Path projectDirectory;
    private Path users;
    private authService service;

    @BeforeEach
    void setUp() {
        users = projectDirectory.resolve("data/users.txt");
        service = new authService(new userRepository(users));
    }

    @Test
    void registersResidentAndReadsTheHashedAccountFromDisk() throws IOException {
        register(" AISYAH@example.com ");
        String row = Files.readString(users).strip();
        assertTrue(row.startsWith("Aisyah|aisyah@example.com|RESIDENT|pbkdf2-sha256$600000$"));
        assertFalse(row.contains(new String(password())));

        authService reopened = new authService(new userRepository(users));
        User user = reopened.login("aisyah@example.com", password(), Role.RESIDENT);
        assertEquals("Aisyah", user.name());
        assertEquals(Role.RESIDENT, user.role());
    }

    @Test
    void rejectsDuplicateEmailRegardlessOfCase() throws IOException {
        register("aisyah@example.com");
        assertEquals("This email address is already registered.", assertThrows(IllegalArgumentException.class,
                () -> register("AISYAH@example.com")).getMessage());
        assertEquals(1, Files.readAllLines(users).size());
    }

    @Test
    void rejectsInvalidRegistrationFields() {
        assertInvalidRegistration(" ", "aisyah@example.com", password(), password());
        assertInvalidRegistration("A|B", "aisyah@example.com", password(), password());
        assertInvalidRegistration("Aisyah", "invalid", password(), password());
        assertInvalidRegistration("Aisyah", "aisyah@example.com", "123".toCharArray(), "123".toCharArray());
        assertInvalidRegistration("Aisyah", "aisyah@example.com", password(), "different".toCharArray());
        assertFalse(Files.exists(users));
    }

    @Test
    void rejectsWrongPassword() throws IOException {
        register("aisyah@example.com");
        assertThrows(IllegalArgumentException.class,
                () -> service.login("aisyah@example.com", "wrong password".toCharArray(), Role.RESIDENT));
    }

    @Test
    void refusesAResidentOnTheAdminForm() throws IOException {
        register("aisyah@example.com");
        assertThrows(IllegalArgumentException.class,
                () -> service.login("aisyah@example.com", password(), Role.ADMIN));
    }

    @Test
    void provisionsTheFirstAdminAndRestrictsLoginToTheAdminRole() throws IOException {
        service.createFirstAdmin("Officer Demo", "officer@example.com", password(), password());
        assertEquals(Role.ADMIN, service.login("officer@example.com", password(), Role.ADMIN).role());
        assertThrows(IllegalArgumentException.class,
                () -> service.login("officer@example.com", password(), Role.RESIDENT));
        assertEquals("The first admin account already exists.", assertThrows(IllegalArgumentException.class,
                () -> service.createFirstAdmin("Second Officer", "other@example.com", password(), password()))
                .getMessage());
    }

    @Test
    void usesADifferentSaltForAccountsWithTheSamePassword() throws IOException {
        register("aisyah@example.com");
        register("another@example.com");
        var lines = Files.readAllLines(users);
        assertNotEquals(lines.get(0).split("\\|")[3], lines.get(1).split("\\|")[3]);
    }

    @Test
    void preservesMalformedFilesInsteadOfOverwritingThem() throws IOException {
        Files.createDirectories(users.getParent());
        Files.writeString(users, "incomplete row");
        assertThrows(IOException.class, () -> register("aisyah@example.com"));
        assertEquals("incomplete row", Files.readString(users));
    }

    private void register(String email) throws IOException {
        service.registerResident("Aisyah", email, password(), password());
    }

    private void assertInvalidRegistration(String name, String email, char[] password, char[] confirmation) {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerResident(name, email, password, confirmation));
    }

    private static char[] password() {
        return "fixture-password".toCharArray();
    }
}
