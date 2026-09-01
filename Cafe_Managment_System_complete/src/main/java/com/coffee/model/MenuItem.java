package com.coffee.model;

import com.coffee.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A single menu item (drink/food) sold at the cafe. Persisted in the
 * menu_items table — every setter here writes straight through to the
 * database, so callers (MenuView, OrderView) don't need to do anything
 * extra to save changes. Falls back to an in-memory-only list (with a
 * console warning) if the database can't be reached, so the UI still works
 * even before db.properties is configured correctly.
 */
public class MenuItem {

    private static final List<MenuItem> ALL_ITEMS = new ArrayList<>();
    private static boolean loaded = false;
    private static boolean dbAvailable = true;

    private int id; // 0 = not yet saved to the database
    private String name;
    private String price;
    private String category;
    private String status;
    private String imagePath;

    public MenuItem(String name, String price, String category, String status) {
        this(name, price, category, status, null);
    }

    public MenuItem(String name, String price, String category, String status, String imagePath) {
        this.id = 0;
        this.name = name;
        this.price = price;
        this.category = category;
        this.status = status;
        this.imagePath = imagePath;
    }

    private MenuItem(int id, String name, String price, String category, String status, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.status = status;
        this.imagePath = imagePath;
    }

    /** All menu items. Loads from the database on first call. Never null. */
    public static synchronized List<MenuItem> getAllItems() {
        if (!loaded) {
            loadFromDatabase();
            loaded = true;
        }
        return ALL_ITEMS;
    }

    private static void loadFromDatabase() {
        String sql = "SELECT id, name, price, category, status, image_path FROM menu_items";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ALL_ITEMS.add(new MenuItem(
                    rs.getInt("id"), rs.getString("name"), rs.getString("price"),
                    rs.getString("category"), rs.getString("status"), rs.getString("image_path")
                ));
            }

            if (ALL_ITEMS.isEmpty()) {
                seedStarterItems();
            }
        } catch (SQLException e) {
            dbAvailable = false;
            System.err.println("Could not connect to database (" + e.getMessage() + ") — "
                + "menu items will NOT be saved. Check db.properties.");
            seedInMemoryOnly();
        }
    }

    private static void seedStarterItems() {
        addItem(new MenuItem("Espresso",       "RM 5.00",  "Hot Drinks",  "Available"));
        addItem(new MenuItem("Latte",          "RM 7.00",  "Hot Drinks",  "Available"));
        addItem(new MenuItem("Cappuccino",     "RM 7.00",  "Hot Drinks",  "Available"));
        addItem(new MenuItem("Americano",      "RM 6.00",  "Hot Drinks",  "Available"));
        addItem(new MenuItem("Croissant",      "RM 4.50",  "Food",        "Sold Out"));
        addItem(new MenuItem("Cheesecake",     "RM 8.00",  "Food",        "Available"));
        addItem(new MenuItem("Iced Coffee",    "RM 6.50",  "Cold Drinks", "Available"));
        addItem(new MenuItem("Iced Latte",     "RM 7.50",  "Cold Drinks", "Available"));
        addItem(new MenuItem("Iced Mocha",     "RM 8.00",  "Cold Drinks", "Sold Out"));
        addItem(new MenuItem("Chocolate Cake", "RM 9.00",  "Desserts",    "Available"));
        addItem(new MenuItem("Tiramisu",       "RM 10.00", "Desserts",    "Available"));
    }

    private static void seedInMemoryOnly() {
        ALL_ITEMS.add(new MenuItem("Espresso",   "RM 5.00", "Hot Drinks",  "Available"));
        ALL_ITEMS.add(new MenuItem("Latte",      "RM 7.00", "Hot Drinks",  "Available"));
        ALL_ITEMS.add(new MenuItem("Iced Coffee","RM 6.50", "Cold Drinks", "Available"));
    }

    public static void addItem(MenuItem item) {
        if (dbAvailable) {
            String sql = "INSERT INTO menu_items (name, price, category, status, image_path) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, item.name);
                ps.setString(2, item.price);
                ps.setString(3, item.category);
                ps.setString(4, item.status);
                ps.setString(5, item.imagePath);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) item.id = keys.getInt(1);
                }
            } catch (SQLException e) {
                System.err.println("Could not save menu item to database: " + e.getMessage());
            }
        }
        if (!ALL_ITEMS.contains(item)) ALL_ITEMS.add(item);
    }

    public static void removeItem(MenuItem item) {
        if (dbAvailable && item.id != 0) {
            String sql = "DELETE FROM menu_items WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, item.id);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Could not delete menu item from database: " + e.getMessage());
            }
        }
        ALL_ITEMS.remove(item);
    }

    private void persist(String column, String value) {
        if (!dbAvailable || id == 0) return;
        String sql = "UPDATE menu_items SET " + column + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not update menu item in database: " + e.getMessage());
        }
    }

    /** Short fallback label shown when an item has no picture yet, e.g. "ES". */
    public String getFallbackIcon() {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) return "?";
        String[] words = trimmed.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0)));
            if (sb.length() >= 2) break;
        }
        return sb.toString();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; persist("name", name); }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; persist("price", price); }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; persist("category", category); }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; persist("status", status); }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; persist("image_path", imagePath); }

    public boolean hasImage() { return imagePath != null && !imagePath.isEmpty(); }
}