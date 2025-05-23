package org.unidata1.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidationUtil {

    private static final Pattern ЭЛЕКТРОНДЫҚПОШТАҮЛГІСІ =
            Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    private static final Pattern ТЕЛЕФОННӨМІРҮЛГІСІ =
            Pattern.compile("^\\+?[0-9]{10,15}$");

    private static final Pattern ПАЙДАЛАНУШЫАТЫҮЛГІСІ =
            Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");

    private static final Pattern ҚҰПИЯСӨЗҮЛГІСІ =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public boolean электрондықПоштаТексеру(String электрондықПошта) {
        if (электрондықПошта == null || электрондықПошта.isBlank()) {
            return false;
        }
        return ЭЛЕКТРОНДЫҚПОШТАҮЛГІСІ.matcher(электрондықПошта).matches();
    }

    public boolean телефонНөміріТексеру(String телефонНөмірі) {
        if (телефонНөмірі == null || телефонНөмірі.isBlank()) {
            return false;
        }
        return ТЕЛЕФОННӨМІРҮЛГІСІ.matcher(телефонНөмірі).matches();
    }

    public boolean пайдаланушыАтыТексеру(String пайдаланушыАты) {
        if (пайдаланушыАты == null || пайдаланушыАты.isBlank()) {
            return false;
        }
        return ПАЙДАЛАНУШЫАТЫҮЛГІСІ.matcher(пайдаланушыАты).matches();
    }

    public boolean құпиясөзТексеру(String құпиясөз) {
        if (құпиясөз == null || құпиясөз.isBlank()) {
            return false;
        }
        return ҚҰПИЯСӨЗҮЛГІСІ.matcher(құпиясөз).matches();
    }

    public boolean құпиясөздерСәйкестігінТексеру(String құпиясөз, String құпиясөзҚайталау) {
        if (құпиясөз == null || құпиясөзҚайталау == null) {
            return false;
        }
        return құпиясөз.equals(құпиясөзҚайталау);
    }

    public boolean мәтінБосЕместігінТексеру(String мәтін) {
        return мәтін != null && !мәтін.isBlank();
    }

    public boolean санШектеуінТексеру(int сан, int минимум, int максимум) {
        return сан >= минимум && сан <= максимум;
    }

    public boolean объектБарынТексеру(Object объект) {
        return объект != null;
    }

    public String қауіпсізМәтінЖасау(String мәтін) {
        if (мәтін == null) {
            return "";
        }
        return мәтін
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("&", "&amp;");
    }
}