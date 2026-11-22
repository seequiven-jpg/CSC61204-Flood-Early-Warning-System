package floodwarning.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hashes passwords before storage and verifies them during login.
 * additional features(more safety for our app) code mostly from ChatGPT for this feature. -bagas
 */

public final class passwordUtil {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String FORMAT = "pbkdf2-sha256";
    private static final int ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    private passwordUtil() {}

    public static String hash(char[] password) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        Base64.Encoder encoder = Base64.getEncoder();
        return String.join("$", FORMAT, Integer.toString(ITERATIONS),
                encoder.encodeToString(salt), encoder.encodeToString(derive(password, salt, ITERATIONS)));
    }

    public static boolean matches(char[] password, String storedHash) {
        String[] parts = storedHash.split("\\$", -1);
        if (parts.length != 4 || !FORMAT.equals(parts[0])) {
            return false;
        }
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            return iterations >= ITERATIONS && iterations <= 2_000_000
                    && salt.length == SALT_BYTES && expected.length == HASH_BYTES
                    && MessageDigest.isEqual(expected, derive(password, salt, iterations));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derive(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_BYTES * Byte.SIZE);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing is unavailable.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
