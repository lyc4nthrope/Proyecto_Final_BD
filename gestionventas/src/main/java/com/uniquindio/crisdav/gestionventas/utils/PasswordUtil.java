package com.uniquindio.crisdav.gestionventas.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Hashea la contraseña con SHA-256 y retorna el hex string.
     * NO es tan seguro como bcrypt/argon2, pero NO requiere dependencias externas.
     */
    public static String hashPassword(String plain) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(plain.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }

    /**
     * Verifica: hashea `plain` y lo compara con el hash almacenado.
     */
    public static boolean verifyPassword(String plain, String storedHash) {
        if (plain == null || storedHash == null) return false;
        return hashPassword(plain).equalsIgnoreCase(storedHash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
