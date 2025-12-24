package com.eventmanagementsystem.util;

import org.mindrot.jbcrypt.BCrypt;

public class GeneratePass {
    public static void main(String[] args) {
        String adminPass = "admin123";
        String staffPass = "staff123";

        String adminHash = BCrypt.hashpw(adminPass, BCrypt.gensalt(12));
        String staffHash = BCrypt.hashpw(staffPass, BCrypt.gensalt(12));

        System.out.println(adminHash);
        System.out.println(staffHash);

        System.out.println("UPDATE users SET password = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("UPDATE users SET password = '" + staffHash + "' WHERE username = 'staff';");
    }
}