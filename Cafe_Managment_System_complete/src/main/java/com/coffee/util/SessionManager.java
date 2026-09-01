package com.coffee.util;

import com.coffee.model.StaffMember;

/**
 * Tracks who is currently logged in, for the lifetime of the running app.
 * Static/in-memory only (resets on restart) — every page and dialog can
 * ask this directly instead of passing the current user through every
 * constructor.
 */
public class SessionManager {

    private static StaffMember currentUser;

    private SessionManager() {
        // static-only utility class
    }

    public static void login(StaffMember user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static StaffMember getCurrentUser() {
        return currentUser;
    }

    /** Falls back to "Guest" if nobody is logged in (shouldn't normally happen). */
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : "Guest";
    }

    /** Falls back to "Guest" if nobody is logged in (shouldn't normally happen). */
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : "Guest";
    }

    public static boolean isAdmin() {
        return "Admin".equals(getCurrentUserRole());
    }
}
