package org.unidata1.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidata1.service.EncryptionService;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    @Value("${message.encryption.enabled:false}")
    private boolean encryptionEnabled;

    @Value("${message.encryption.algorithm:AES/GCM/NoPadding}")
    private String algorithm;

    @Value("${message.encryption.key:}")
    private String configuredKey;

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private SecretKey getSecretKey() {
        if (configuredKey != null && !configuredKey.isEmpty()) {
            byte[] decodedKey = Base64.getDecoder().decode(configuredKey);
            return new SecretKeySpec(decodedKey, "AES");
        } else {
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(256);
                return keyGenerator.generateKey();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при генерации ключа шифрования", e);
            }
        }
    }

    @Override
    public String encrypt(String plainText) {
        if (!encryptionEnabled || plainText == null) {
            return plainText;
        }

        try {
            SecretKey secretKey = getSecretKey();
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(algorithm);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedText.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при шифровании сообщения", e);
        }
    }

    @Override
    public String decrypt(String encryptedText) {
        if (!encryptionEnabled || encryptedText == null) {
            return encryptedText;
        }

        try {
            SecretKey secretKey = getSecretKey();
            byte[] decodedMessage = Base64.getDecoder().decode(encryptedText);

            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedMessage);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] encryptedTextBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedTextBytes);

            Cipher cipher = Cipher.getInstance(algorithm);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
            return new String(decryptedTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при дешифровании сообщения", e);
        }
    }
}