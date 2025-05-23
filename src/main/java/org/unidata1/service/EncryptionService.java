package org.unidata1.service;

public interface EncryptionService {

    String encrypt(String plainText);
    String decrypt(String encryptedText);

}