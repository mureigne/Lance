package com.example.lance;

public class CurrentUser {
    private static String username;

    public static void setUsername(String u) {
        username = u;
    }

    public static String getUsername() {
        return username;
    }

    public static void clear() {
        username = null;
    }

    public static boolean isLoggedIn() {
        return username != null;
    }
}