package edu.icet.util;

public class SessionManager {
    private static String currentUsername;
    private static String currentUserRole;
    private static int currentUserId;

    public static void setCurrentUser(String username, String role, int userId) {
        currentUsername = username;
        currentUserRole = role;
        currentUserId = userId;
    }

    public static String getCurrentUsername() {
        return currentUsername != null ? currentUsername : "Admin";
    }

    public static String getCurrentUserRole() {
        return currentUserRole != null ? currentUserRole : "ADMIN";
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void clearSession() {
        currentUsername = null;
        currentUserRole = null;
        currentUserId = 0;
    }

    public static boolean isLoggedIn() {
        return currentUsername != null;
    }
}