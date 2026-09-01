package com.coffee.model;

import com.coffee.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderRecord {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM, h:mm a");

    private static int nextOrderNumber = 1043;
    private static final List<OrderRecord> ALL_ORDERS = new ArrayList<>();
    private static boolean loaded = false;
    private static boolean dbAvailable = true;

    public static class LineItem {
        private final String name;
        private final int quantity;
        private final double unitPrice;

        public LineItem(String name, int quantity, double unitPrice) {
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getLineTotal() { return quantity * unitPrice; }
    }

    private int id; // 0 = not yet saved (database row id, different from orderNumber)
    private final int orderNumber;
    private final String dateTime;
    private final int hour24;
    private final String cashier;
    private final List<LineItem> lineItems;
    private final double total;
    private String status;

    public OrderRecord(int orderNumber, String dateTime, int hour24, String cashier,
                        List<LineItem> lineItems, double total, String status) {
        this.id = 0;
        this.orderNumber = orderNumber;
        this.dateTime = dateTime;
        this.hour24 = hour24;
        this.cashier = cashier;
        this.lineItems = lineItems;
        this.total = total;
        this.status = status;
    }

    private OrderRecord(int id, int orderNumber, String dateTime, int hour24, String cashier,
                         List<LineItem> lineItems, double total, String status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.dateTime = dateTime;
        this.hour24 = hour24;
        this.cashier = cashier;
        this.lineItems = lineItems;
        this.total = total;
        this.status = status;
    }

    public static synchronized List<OrderRecord> getAllOrders() {
        if (!loaded) {
            loadFromDatabase();
            loaded = true;
        }
        return ALL_ORDERS;
    }

    private static void loadFromDatabase() {
        String sql = "SELECT id, order_number, date_time, hour24, cashier, total, status FROM orders ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int maxOrderNumber = 1042;
            while (rs.next()) {
                int dbId = rs.getInt("id");
                int orderNum = rs.getInt("order_number");
                maxOrderNumber = Math.max(maxOrderNumber, orderNum);

                List<LineItem> items = loadLineItems(conn, dbId);

                ALL_ORDERS.add(new OrderRecord(
                    dbId, orderNum, rs.getString("date_time"), rs.getInt("hour24"),
                    rs.getString("cashier"), items, rs.getDouble("total"), rs.getString("status")
                ));
            }
            nextOrderNumber = maxOrderNumber + 1;

            if (ALL_ORDERS.isEmpty()) {
                seedHistoricalOrders();
            }
        } catch (SQLException e) {
            dbAvailable = false;
            System.err.println("Could not connect to database (" + e.getMessage() + ") — "
                + "orders will NOT be saved. Check db.properties.");
        }
    }

    private static List<LineItem> loadLineItems(Connection conn, int orderId) throws SQLException {
        List<LineItem> items = new ArrayList<>();
        String sql = "SELECT name, quantity, unit_price FROM order_line_items WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new LineItem(rs.getString("name"), rs.getInt("quantity"), rs.getDouble("unit_price")));
                }
            }
        }
        return items;
    }

    private static void seedHistoricalOrders() {
        insertSeeded(1042, "30 Jun, 2:45 PM", 14, "Ali", listOf(
            new LineItem("Latte", 2, 7.00), new LineItem("Muffin", 1, 4.00)
        ), 18.00, "Completed");
        insertSeeded(1041, "30 Jun, 2:30 PM", 14, "Ali", listOf(
            new LineItem("Americano", 1, 6.00)
        ), 6.00, "Completed");
        insertSeeded(1040, "30 Jun, 2:15 PM", 14, "Siti", listOf(
            new LineItem("Cappuccino", 3, 7.00)
        ), 21.00, "In Progress");
        insertSeeded(1039, "30 Jun, 1:58 PM", 13, "Siti", listOf(
            new LineItem("Espresso", 1, 5.00), new LineItem("Cake", 1, 10.00)
        ), 15.00, "Completed");
        insertSeeded(1038, "30 Jun, 1:40 PM", 13, "Hafiz", listOf(
            new LineItem("Latte", 1, 7.00)
        ), 7.00, "Cancelled");
        insertSeeded(1037, "30 Jun, 1:20 PM", 13, "Hafiz", listOf(
            new LineItem("Cheesecake", 2, 8.00)
        ), 16.00, "Completed");
        insertSeeded(1036, "30 Jun, 1:05 PM", 13, "Ali", listOf(
            new LineItem("Croissant", 3, 4.50)
        ), 13.50, "Completed");
    }

    private static void insertSeeded(int orderNumber, String dateTime, int hour24, String cashier,
                                      List<LineItem> lineItems, double total, String status) {
        OrderRecord order = new OrderRecord(orderNumber, dateTime, hour24, cashier, lineItems, total, status);
        saveToDatabase(order);
        ALL_ORDERS.add(order);
    }

    private static List<LineItem> listOf(LineItem... items) {
        List<LineItem> list = new ArrayList<>();
        for (LineItem i : items) list.add(i);
        return list;
    }

    public static OrderRecord recordNewOrder(String cashier, List<LineItem> lineItems, double total) {
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(DATE_FORMAT);
        OrderRecord order = new OrderRecord(
            nextOrderNumber++, formatted, now.getHour(), cashier, lineItems, total, "Completed"
        );
        saveToDatabase(order);
        ALL_ORDERS.add(0, order);
        return order;
    }

    private static void saveToDatabase(OrderRecord order) {
        if (!dbAvailable) return;
        String sql = "INSERT INTO orders (order_number, date_time, hour24, cashier, total, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.orderNumber);
            ps.setString(2, order.dateTime);
            ps.setInt(3, order.hour24);
            ps.setString(4, order.cashier);
            ps.setDouble(5, order.total);
            ps.setString(6, order.status);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) order.id = keys.getInt(1);
            }

            String itemSql = "INSERT INTO order_line_items (order_id, name, quantity, unit_price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement itemPs = conn.prepareStatement(itemSql)) {
                for (LineItem li : order.lineItems) {
                    itemPs.setInt(1, order.id);
                    itemPs.setString(2, li.getName());
                    itemPs.setInt(3, li.getQuantity());
                    itemPs.setDouble(4, li.getUnitPrice());
                    itemPs.addBatch();
                }
                itemPs.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Could not save order to database: " + e.getMessage());
        }
    }

    public int getOrderNumber() { return orderNumber; }
    public String getDateTime() { return dateTime; }
    public int getHour24() { return hour24; }
    public String getCashier() { return cashier; }
    public List<LineItem> getLineItems() { return lineItems; }

    public String getItemsSummary() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lineItems.size(); i++) {
            LineItem li = lineItems.get(i);
            sb.append(li.getName()).append(" x").append(li.getQuantity());
            if (i < lineItems.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    public double getTotal() { return total; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
        if (dbAvailable && id != 0) {
            String sql = "UPDATE orders SET status = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, id);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Could not update order status in database: " + e.getMessage());
            }
        }
    }
}