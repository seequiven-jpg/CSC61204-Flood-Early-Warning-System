package com.group.floodwarning.repository;

import com.group.floodwarning.model.User;
import com.group.floodwarning.model.User.Role;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public final class userRepository {
    public enum AddResult { SAVED, DUPLICATE_EMAIL, ADMIN_ALREADY_EXISTS }

    private final Path file;

    public userRepository(Path file) {
        this.file = file.toAbsolutePath().normalize();
    }

    public Optional<User> findByEmail(String email) throws IOException {
        return readUsers().stream().filter(user -> user.email().equalsIgnoreCase(email)).findFirst();
    }

    public AddResult add(User user) throws IOException {
        // Serialize writers in this JVM and in other running copies of the app.
        synchronized (userRepository.class) {
            Files.createDirectories(file.getParent());
            Path lockFile = file.resolveSibling(file.getFileName() + ".lock");
            try (FileChannel channel = FileChannel.open(lockFile, CREATE, WRITE);
                 FileLock lock = channel.lock()) {
                List<User> users = readUsers();
                if (users.stream().anyMatch(saved -> saved.email().equalsIgnoreCase(user.email()))) {
                    return AddResult.DUPLICATE_EMAIL;
                }
                if (user.role() == Role.ADMIN && users.stream().anyMatch(saved -> saved.role() == Role.ADMIN)) {
                    return AddResult.ADMIN_ALREADY_EXISTS;
                }
                users.add(user);
                saveUsers(users);
                return AddResult.SAVED;
            }
        }
    }

    private List<User> readUsers() throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (NoSuchFileException e) {
            return new ArrayList<>();
        }
        List<User> users = new ArrayList<>();
        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            if (line.isBlank()) {
                continue;
            }
            String[] fields = line.split("\\|", -1);
            try {
                if (fields.length != 4 || fields[0].isBlank() || fields[1].isBlank() || fields[3].isBlank()) {
                    throw new IllegalArgumentException("Incomplete account");
                }
                users.add(new User(fields[0], fields[1], Role.valueOf(fields[2]), fields[3]));
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid account data on line " + lineNumber, e);
            }
        }
        return users;
    }

    private void saveUsers(List<User> users) throws IOException {
        List<String> lines = users.stream()
                .map(user -> String.join("|", user.name(), user.email(), user.role().name(), user.passwordHash()))
                .toList();
        Path temp = Files.createTempFile(file.getParent(), "users-", ".tmp");
        try {
            Files.write(temp, lines);
            Files.move(temp, file, ATOMIC_MOVE, REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
