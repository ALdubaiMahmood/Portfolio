package com.coffee.model;

import com.coffee.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StaffMember {

    private static final List<StaffMember> ALL_STAFF = new ArrayList<>();
    private static boolean loaded = false;
    private static boolean dbAvailable = true;

    private int id; // 0 = not yet saved
    private String name;
    private String role;
    private String shift;
    private boolean onDuty;
    private String username;
    private String password;
    private String email;

    public StaffMember(String name, String role, String shift, boolean onDuty,
                        String username, String password, String email) {
        this.id = 0;
        this.name = name;
        this.role = role;
        this.shift = shift;
        this.onDuty = onDuty;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    private StaffMember(int id, String name, String role, String shift, boolean onDuty,
                         String username, String password, String email) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.shift = shift;
        this.onDuty = onDuty;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public static synchronized List<StaffMember> getAllStaff() {
        if (!loaded) {
            loadFromDatabase();
            loaded = true;
        }
        return ALL_STAFF;
    }

    private static void loadFromDatabase() {
        String sql = "SELECT id, name, role, shift, on_duty, username, password, email FROM staff";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ALL_STAFF.add(new StaffMember(
                    rs.getInt("id"), rs.getString("name"), rs.getString("role"), rs.getString("shift"),
                    rs.getBoolean("on_duty"), rs.getString("username"), rs.getString("password"), rs.getString("email")
                ));
            }

            if (ALL_STAFF.isEmpty()) {
                seedStarterStaff();
            }
        } catch (SQLException e) {
            dbAvailable = false;
            System.err.println("Could not connect to database (" + e.getMessage() + ") — "
                + "staff changes will NOT be saved. Check db.properties.");
            seedInMemoryOnly();
        }
    }

    private static void seedStarterStaff() {
        addStaff(new StaffMember("Mahmoud Kadir", "Admin",   "Morning", true,
            "admin_mahmoud",  "admin123",   "mahmoud@kaffe.com"));
        addStaff(new StaffMember("Ali Hassan",    "Cashier", "Morning", true,
            "cashier_ali",    "cashier123", "ali@kaffe.com"));
        addStaff(new StaffMember("Siti Rahimah",  "Cashier", "Evening", false,
            "cashier_siti",   "siti123",    "siti@kaffe.com"));
        addStaff(new StaffMember("Hafiz Azman",   "Barista", "Morning", true,
            "barista_hafiz",  "barista123", "hafiz@kaffe.com"));
        addStaff(new StaffMember("Nora Idris",    "Barista", "Evening", false,
            "barista_nora",   "nora123",    "nora@kaffe.com"));
        addStaff(new StaffMember("Reza Malik",    "Cashier", "Evening", true,
            "cashier_reza",   "reza123",    "reza@kaffe.com"));
    }

    private static void seedInMemoryOnly() {
        ALL_STAFF.add(new StaffMember("Mahmoud Kadir", "Admin", "Morning", true,
            "admin_mahmoud", "admin123", "mahmoud@kaffe.com"));
    }

    public static void addStaff(StaffMember staff) {
        if (dbAvailable) {
            String sql = "INSERT INTO staff (name, role, shift, on_duty, username, password, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, staff.name);
                ps.setString(2, staff.role);
                ps.setString(3, staff.shift);
                ps.setBoolean(4, staff.onDuty);
                ps.setString(5, staff.username);
                ps.setString(6, staff.password);
                ps.setString(7, staff.email);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) staff.id = keys.getInt(1);
                }
            } catch (SQLException e) {
                System.err.println("Could not save staff member to database: " + e.getMessage());
            }
        }
        if (!ALL_STAFF.contains(staff)) ALL_STAFF.add(staff);
    }

    public static void removeStaff(StaffMember staff) {
        if (dbAvailable && staff.id != 0) {
            String sql = "DELETE FROM staff WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, staff.id);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Could not delete staff member from database: " + e.getMessage());
            }
        }
        ALL_STAFF.remove(staff);
    }

    private void persistString(String column, String value) {
        if (!dbAvailable || id == 0) return;
        String sql = "UPDATE staff SET " + column + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not update staff member in database: " + e.getMessage());
        }
    }

    private void persistBoolean(String column, boolean value) {
        if (!dbAvailable || id == 0) return;
        String sql = "UPDATE staff SET " + column + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not update staff member in database: " + e.getMessage());
        }
    }

    public static StaffMember findByCredentials(String username, String password, String role) {
        for (StaffMember s : getAllStaff()) {
            if (s.username.equals(username) && s.password.equals(password) && s.role.equals(role)) {
                return s;
            }
        }
        return null;
    }

    public static boolean isUsernameTaken(String username) {
        for (StaffMember s : getAllStaff()) {
            if (s.username.equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    public String getInitials() {
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) sb.append(Character.toUpperCase(part.charAt(0)));
            if (sb.length() >= 2) break;
        }
        return sb.toString();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; persistString("name", name); }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; persistString("role", role); }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; persistString("shift", shift); }

    public boolean isOnDuty() { return onDuty; }
    public void setOnDuty(boolean onDuty) { this.onDuty = onDuty; persistBoolean("on_duty", onDuty); }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; persistString("username", username); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; persistString("password", password); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; persistString("email", email); }
}