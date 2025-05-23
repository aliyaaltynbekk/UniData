package org.unidata1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

@Configuration
public class EncryptionConfig {

    private final String encryptionPassword = "strong-encryption-password";
    private final String salt = KeyGenerators.string().generateKey();

    @Bean
    public TextEncryptor textEncryptor() {
        return Encryptors.text(encryptionPassword, salt);
    }

}