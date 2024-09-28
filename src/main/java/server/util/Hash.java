package server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static String hashMD2(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD2");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка алгоритма хэширования", e);
        }
    }
}
