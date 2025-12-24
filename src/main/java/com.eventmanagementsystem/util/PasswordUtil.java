package com.eventmanagementsystem.util;
import org.mindrot.jbcrypt.BCrypt;
public final class PasswordUtil {
    private PasswordUtil() {}
    public static String hash(String txt) { return BCrypt.hashpw(txt, BCrypt.gensalt(12)); }
    public static boolean check(String txt, String hash) { return BCrypt.checkpw(txt, hash); }
}