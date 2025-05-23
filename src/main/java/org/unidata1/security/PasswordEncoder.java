package org.unidata1.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;

    public String encode(String rawPassword) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            byte[] hash = hashPassword(rawPassword.toCharArray(), salt, ITERATIONS, ALGORITHM);

            return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Құпиясөзді шифрлау кезінде қате пайда болды", e);
        }
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            String[] parts = encodedPassword.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] originalHash = Base64.getDecoder().decode(parts[2]);

            byte[] newHash = hashPassword(rawPassword.toCharArray(), salt, iterations, ALGORITHM);

            int diff = originalHash.length ^ newHash.length;
            for (int i = 0; i < originalHash.length && i < newHash.length; i++) {
                diff |= originalHash[i] ^ newHash[i];
            }

            return diff == 0;
        } catch (NoSuchAlgorithmException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private byte[] hashPassword(char[] password, byte[] salt, int iterations, String algorithm)
            throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
        digest.update(salt);

        byte[] pwdBytes = new byte[password.length * 2];
        for (int i = 0; i < password.length; i++) {
            pwdBytes[i * 2] = (byte) (password[i] >> 8);
            pwdBytes[i * 2 + 1] = (byte) password[i];
        }

        byte[] hashed = digest.digest(pwdBytes);

        for (int i = 0; i < iterations; i++) {
            digest.reset();
            hashed = digest.digest(hashed);
        }

        return hashed;
    }

    public boolean isPasswordStrong(String password) {
        if (password.length() < 8) {
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }

        return true;
    }
}