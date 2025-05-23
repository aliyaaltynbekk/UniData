package org.unidata1.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EncryptionUtil {

    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final String AES_ALGORITHM = "AES";

    private final PasswordEncoder құпиясөзКодтаушы;
    private final SecretKey құпияКілт;
    private final SecureRandom кездейсоқГенератор;

    public EncryptionUtil() {
        this.құпиясөзКодтаушы = new BCryptPasswordEncoder();
        this.құпияКілт = жаңаКілтҚұру();
        this.кездейсоқГенератор = new SecureRandom();
    }

    private SecretKey жаңаКілтҚұру() {
        byte[] кілтБайттары = "ҰниДатаҚұпияКілті12345678".getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(кілтБайттары, 0, 16, AES_ALGORITHM);
    }

    public String құпиясөзХэштеу(String құпиясөз) {
        return құпиясөзКодтаушы.encode(құпиясөз);
    }

    public boolean құпиясөзТексеру(String енгізілгенҚұпиясөз, String хэштелгенҚұпиясөз) {
        return құпиясөзКодтаушы.matches(енгізілгенҚұпиясөз, хэштелгенҚұпиясөз);
    }

    public String деректердіШифрлау(String деректер) {
        try {
            byte[] iv = жаңаIVҚұру();
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.ENCRYPT_MODE, құпияКілт, spec);
            byte[] шифрленгенДеректер = cipher.doFinal(деректер.getBytes(StandardCharsets.UTF_8));

            byte[] ivAndEncryptedData = біріктіру(iv, шифрленгенДеректер);
            return Base64.getEncoder().encodeToString(ivAndEncryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Деректерді шифрлау кезінде қате орын алды", e);
        }
    }

    public String деректердіДешифрлау(String шифрленгенМәтін) {
        try {
            byte[] ivAndEncryptedData = Base64.getDecoder().decode(шифрленгенМәтін);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] шифрленгенДеректер = new byte[ivAndEncryptedData.length - GCM_IV_LENGTH];

            System.arraycopy(ivAndEncryptedData, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(ivAndEncryptedData, GCM_IV_LENGTH, шифрленгенДеректер, 0, шифрленгенДеректер.length);

            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.DECRYPT_MODE, құпияКілт, spec);
            byte[] дешифрленгенДеректер = cipher.doFinal(шифрленгенДеректер);

            return new String(дешифрленгенДеректер, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Деректерді дешифрлау кезінде қате орын алды", e);
        }
    }

    private byte[] жаңаIVҚұру() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        кездейсоқГенератор.nextBytes(iv);
        return iv;
    }

    private byte[] біріктіру(byte[] бірінші, byte[] екінші) {
        byte[] нәтиже = new byte[бірінші.length + екінші.length];
        System.arraycopy(бірінші, 0, нәтиже, 0, бірінші.length);
        System.arraycopy(екінші, 0, нәтиже, бірінші.length, екінші.length);
        return нәтиже;
    }
}