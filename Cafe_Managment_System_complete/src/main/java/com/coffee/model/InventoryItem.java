package com.coffee.model;

import com.coffee.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryItem {

    private static final List<InventoryItem> ALL_ITEMS = new ArrayList<>();
    private static boolean loaded = false;
    private static boolean dbAvailable = true;

    private int id; // 0 = not yet saved
    private String name;
    private String category;
    private double quantity;
    private String unit;
    private double reorderLevel;
    private String lastRestocked;
    private String imagePath;

    public InventoryItem(String name, String category, double quantity,
                          String unit, double reorderLevel, String lastRestocked) {
        this(name, category, quantity, unit, reorderLevel, lastRestocked, null);
    }

    public InventoryItem(String name, String category, double quantity,
                          String unit, double reorderLevel, String lastRestocked,
                          String imagePath) {
        this.id = 0;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.reorderLevel = reorderLevel;
        this.lastRestocked = lastRestocked;
        this.imagePath = imagePath;
    }

    private InventoryItem(int id, String name, String category, double quantity, String unit,
                           double reorderLevel, String lastRestocked, String imagePath) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.reorderLevel = reorderLevel;
        this.lastRestocked = lastRestocked;
        this.imagePath = imagePath;
    }

    public static synchronized List<InventoryItem> getAllItems() {
        if (!loaded) {
            loadFromDatabase();
            loaded = true;
        }
        return ALL_ITEMS;
    }

    private static void loadFromDatabase() {
        String sql = "SELECT id, name, category, quantity, unit, reorder_level, last_restocked, image_path FROM inventory_items";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ALL_ITEMS.add(new InventoryItem(
                    rs.getInt("id"), rs.getString("name"), rs.getString("category"),
                    rs.getDouble("quantity"), rs.getString("unit"), rs.getDouble("reorder_level"),
                    rs.getString("last_restocked"), rs.getString("image_path")
                ));
            }

            if (ALL_ITEMS.isEmpty()) {
                seedStarterItems();
            }
        } catch (SQLException e) {
            dbAvailable = false;
            System.err.println("Could not connect to database (" + e.getMessage() + ") — "
                + "inventory will NOT be saved. Check db.properties.");
            seedInMemoryOnly();
        }
    }

    private static void seedStarterItems() {
        addItem(new InventoryItem("Coffee Beans - Arabica", "Coffee & Beans", 12,  "kg",  5,   "01 Jul 2026"));
        addItem(new InventoryItem("Coffee Beans - Robusta", "Coffee & Beans", 3,   "kg",  5,   "05 Jul 2026"));
        addItem(new InventoryItem("Fresh Milk",             "Dairy",          8,   "L",   10,  "10 Jul 2026"));
        addItem(new InventoryItem("Oat Milk",               "Dairy",          15,  "L",   8,   "08 Jul 2026"));
        addItem(new InventoryItem("Whipped Cream",          "Dairy",          0,   "L",   5,   "20 Jun 2026"));
        addItem(new InventoryItem("Paper Cups (12oz)",      "Packaging",      0,   "pcs", 100, "20 Jun 2026"));
        addItem(new InventoryItem("Paper Cups (8oz)",       "Packaging",      250, "pcs", 100, "15 Jul 2026"));
        addItem(new InventoryItem("Napkins (pack)",         "Packaging",      40,  "pcs", 50,  "12 Jul 2026"));
        addItem(new InventoryItem("Sugar Sachets",          "Other",          500, "pcs", 200, "01 Jul 2026"));
        addItem(new InventoryItem("Vanilla Syrup",          "Other",          2,   "L",   3,   "18 Jul 2026"));
    }

    private static void seedInMemoryOnly() {
        ALL_ITEMS.add(new InventoryItem("Coffee Beans - Arabica", "Coffee & Beans", 12, "kg", 5, "01 Jul 2026"));
        ALL_ITEMS.add(new InventoryItem("Fresh Milk",             "Dairy",          8,  "L",  10, "10 Jul 2026"));
    }

    public static void addItem(InventoryItem item) {
        if (dbAvailable) {
            String sql = "INSERT INTO inventory_items (name, category, quantity, unit, reorder_level, last_restocked, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, item.name);
                ps.setString(2, item.category);
                ps.setDouble(3, item.quantity);
                ps.setString(4, item.unit);
                ps.setDouble(5, item.reorderLevel);
                ps.setString(6, item.lastRestocked);
                ps.setString(7, item.imagePath);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) item.id = keys.getInt(1);
                }
            } catch (SQLException e) {
                System.err.println("Could not save inventory item to database: " + e.getMessage());
            }
        }
        if (!ALL_ITEMS.contains(item)) ALL_ITEMS.add(item);
    }

    public static void removeItem(InventoryItem item) {
        if (dbAvailable && item.id != 0) {
            String sql = "DELETE FROM inventory_items WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, item.id);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Could not delete inventory item from database: " + e.getMessage());
            }
        }
        ALL_ITEMS.remove(item);
    }

    private void persistString(String column, String value) {
        if (!dbAvailable || id == 0) return;
        String sql = "UPDATE inventory_items SET " + column + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not update inventory item in database: " + e.getMessage());
        }
    }

    private void persistDouble(String column, double value) {
        if (!dbAvailable || id == 0) return;
        String sql = "UPDATE inventory_items SET " + column + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not update inventory item in database: " + e.getMessage());
        }
    }

    public boolean isOutOfStock() { return quantity <= 0; }
    public boolean isLowStock() { return quantity > 0 && quantity <= reorderLevel; }

    public String getStatus() {
        if (isOutOfStock()) return "Out of Stock";
        if (isLowStock()) return "Low Stock";
        return "In Stock";
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; persistString("name", name); }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; persistString("category", category); }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; persistDouble("quantity", quantity); }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; persistString("unit", unit); }

    public double getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(double reorderLevel) { this.reorderLevel = reorderLevel; persistDouble("reorder_level", reorderLevel); }

    public String getLastRestocked() { return lastRestocked; }
    public void setLastRestocked(String lastRestocked) { this.lastRestocked = lastRestocked; persistString("last_restocked", lastRestocked); }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; persistString("image_path", imagePath); }

    public boolean hasImage() { return imagePath != null && !imagePath.isEmpty(); }
}