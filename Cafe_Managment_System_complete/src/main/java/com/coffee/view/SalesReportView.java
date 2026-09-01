package com.coffee.view;

import com.coffee.MainLayout;
import com.coffee.model.OrderRecord;
import com.coffee.util.SessionManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Sales Report content — no sidebar here, {@link MainLayout} already owns
 * one shared sidebar. Every number here is computed live from
 * {@link OrderRecord#getAllOrders()} — nothing on this page is hardcoded.
 * Admin-only: {@link MainLayout} blocks navigation here for other roles.
 */
public class SalesReportView {

    private final MainLayout layout;

    private String activeTab = "Daily";
    private HBox tabsBox;

    public SalesReportView(MainLayout layout) {
        this.layout = layout;
    }

    public VBox getView() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #FFF8E7;");

        HBox topBar = buildTopBar();

        tabsBox = new HBox(8);
        tabsBox.setPadding(new Insets(16, 24, 4, 24));
        refreshTabs();

        Label note = new Label("Showing all recorded orders \u2014 daily/weekly/monthly filtering isn't wired up yet.");
        note.setPadding(new Insets(0, 24, 12, 24));
        note.setStyle(
            "-fx-font-size: 10px;" +
            "-fx-text-fill: #7A5C2E;"
        );

        List<OrderRecord> all = OrderRecord.getAllOrders();
        List<OrderRecord> nonCancelled = new ArrayList<>();
        for (OrderRecord o : all) {
            if (!o.getStatus().equals("Cancelled")) nonCancelled.add(o);
        }

        double revenue = 0;
        for (OrderRecord o : nonCancelled) {
            revenue += o.getTotal();
        }
        double avgOrder = nonCancelled.isEmpty() ? 0 : revenue / nonCancelled.size();

        String[][] breakdown = computeSalesBreakdown(nonCancelled);
        String topItem = breakdown.length > 0 ? breakdown[0][0] : "\u2014";
        String topItemCaption = breakdown.length > 0 ? breakdown[0][1] + " sold" : "No sales yet";

        HBox statRow = new HBox(16);
        statRow.setPadding(new Insets(4, 24, 16, 24));
        statRow.getChildren().addAll(
            buildStatCard(String.format("RM %.2f", revenue), "Total Revenue", null, "#d7981a"),
            buildStatCard(String.valueOf(all.size()), "Orders", null, "#28B463"),
            buildStatCard(String.format("RM %.2f", avgOrder), "Avg Order", "per transaction", "#2E86C1"),
            buildStatCard(topItem, "Top Item", topItemCaption, "#8E44AD")
        );

        HBox bottomRow = new HBox(20);
        bottomRow.setPadding(new Insets(0, 24, 24, 24));
        VBox.setVgrow(bottomRow, Priority.ALWAYS);

        VBox breakdownBox = buildBreakdownBox(breakdown);
        HBox.setHgrow(breakdownBox, Priority.ALWAYS);

        VBox hourlyBox = buildHourlyBox(nonCancelled);
        hourlyBox.setPrefWidth(360);
        hourlyBox.setMinWidth(360);

        bottomRow.getChildren().addAll(breakdownBox, hourlyBox);

        content.getChildren().addAll(topBar, tabsBox, note, statRow, bottomRow);
        return content;
    }

    // ── TOP BAR ──
    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 24, 14, 24));
        bar.setStyle(
            "-fx-background-color: #FFF8E7;" +
            "-fx-border-color: transparent transparent #E0D0A0 transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label title = new Label("Sales Report");
        title.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("Export PDF");
        exportBtn.setStyle(
            "-fx-background-color: #C9952A;" +
            "-fx-text-fill: #1A0F00;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 8px 16px;" +
            "-fx-cursor: hand;"
        );
        exportBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF");
            alert.setHeaderText("Not available yet");
            alert.setContentText("PDF export isn't built yet \u2014 this button is just a placeholder for now.");
            alert.showAndWait();
        });

        Label user = new Label(SessionManager.getCurrentUserName() + "  \u25BE");
        user.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #7A5C2E;" +
            "-fx-padding: 0 0 0 16px;"
        );

        bar.getChildren().addAll(title, spacer, exportBtn, user);
        return bar;
    }

    // ── DAILY/WEEKLY/MONTHLY TABS (visual only for now — see note above) ──
    private void refreshTabs() {
        tabsBox.getChildren().clear();
        String[] tabs = {"Daily", "Weekly", "Monthly"};

        for (String t : tabs) {
            Button tab = new Button(t);
            tab.setPrefWidth(100);
            tab.setPrefHeight(34);
            tab.setStyle(t.equals(activeTab) ? activeTabStyle() : inactiveTabStyle());

            tab.setOnAction(e -> {
                activeTab = t;
                refreshTabs();
            });

            tabsBox.getChildren().add(tab);
        }
    }

    private String activeTabStyle() {
        return "-fx-background-color: #C9952A;" +
               "-fx-text-fill: #1A0F00;" +
               "-fx-font-weight: bold;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;";
    }

    private String inactiveTabStyle() {
        return "-fx-background-color: #1F1200;" +
               "-fx-text-fill: #7A5C2E;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;";
    }

    // ── STAT CARDS ──
    private VBox buildStatCard(String value, String label, String caption, String color) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20, 16, 20, 16));
        card.setMinWidth(180);
        card.setPrefWidth(220);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-background-radius: 12px;"
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + color + ";"
        );

        Label textLabel = new Label(label);
        textLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #7A5C2E;"
        );

        card.getChildren().addAll(valueLabel, textLabel);

        if (caption != null && !caption.isEmpty()) {
            Label captionLabel = new Label(caption);
            captionLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: #5A3E1A;"
            );
            card.getChildren().add(captionLabel);
        }

        return card;
    }

    // ── SALES BREAKDOWN ──
    /** Aggregates every line item across all non-cancelled orders, sorted by quantity sold (descending). */
    private String[][] computeSalesBreakdown(List<OrderRecord> nonCancelled) {
        Map<String, Integer> qtyMap = new LinkedHashMap<>();
        Map<String, Double> revenueMap = new LinkedHashMap<>();

        for (OrderRecord o : nonCancelled) {
            for (OrderRecord.LineItem li : o.getLineItems()) {
                qtyMap.merge(li.getName(), li.getQuantity(), Integer::sum);
                revenueMap.merge(li.getName(), li.getLineTotal(), Double::sum);
            }
        }

        double totalRevenue = 0;
        for (double v : revenueMap.values()) {
            totalRevenue += v;
        }

        List<String> names = new ArrayList<>(qtyMap.keySet());
        names.sort((a, b) -> qtyMap.get(b) - qtyMap.get(a));

        String[][] rows = new String[names.size()][4];
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            int qty = qtyMap.get(name);
            double revenue = revenueMap.get(name);
            double pct = totalRevenue > 0 ? (revenue / totalRevenue) * 100 : 0;

            rows[i][0] = name;
            rows[i][1] = String.valueOf(qty);
            rows[i][2] = String.format("RM %.2f", revenue);
            rows[i][3] = String.format("%.1f%%", pct);
        }
        return rows;
    }

    private VBox buildBreakdownBox(String[][] rows) {
        VBox box = new VBox(10);

        Label title = new Label("Sales Breakdown");
        title.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        TableView<String[]> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-control-inner-background: #1F1200;" +
            "-fx-table-cell-border-color: transparent;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No sales recorded yet."));

        TableColumn<String[], String> colItem = new TableColumn<>("Item");
        colItem.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));

        TableColumn<String[], String> colQty = new TableColumn<>("Qty Sold");
        colQty.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));

        TableColumn<String[], String> colRevenue = new TableColumn<>("Revenue");
        colRevenue.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));

        TableColumn<String[], String> colPct = new TableColumn<>("% of Sales");
        colPct.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));

        table.getColumns().addAll(colItem, colQty, colRevenue, colPct);
        table.getItems().addAll(rows);
        VBox.setVgrow(table, Priority.ALWAYS);

        box.getChildren().addAll(title, table);
        return box;
    }

    // ── HOURLY CHART ──
    private VBox buildHourlyBox(List<OrderRecord> nonCancelled) {
        VBox box = new VBox(10);

        Label title = new Label("Hourly Orders");
        title.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        HBox chart = buildHourlyChart(nonCancelled);

        box.getChildren().addAll(title, chart);
        return box;
    }

    private HBox buildHourlyChart(List<OrderRecord> nonCancelled) {
        int[] hours = {8, 9, 10, 11, 12, 13, 14, 15};
        int[] counts = new int[hours.length];

        for (OrderRecord o : nonCancelled) {
            for (int i = 0; i < hours.length; i++) {
                if (o.getHour24() == hours[i]) counts[i]++;
            }
        }

        int max = 1;
        for (int c : counts) {
            max = Math.max(max, c);
        }

        HBox chart = new HBox(12);
        chart.setAlignment(Pos.BOTTOM_CENTER);
        chart.setPadding(new Insets(16));
        chart.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-background-radius: 12px;"
        );

        for (int i = 0; i < hours.length; i++) {
            VBox barCol = new VBox(6);
            barCol.setAlignment(Pos.BOTTOM_CENTER);

            double barHeight = 20 + (counts[i] / (double) max) * 100;

            Region bar = new Region();
            bar.setPrefWidth(28);
            bar.setPrefHeight(barHeight);
            bar.setStyle(
                "-fx-background-color: #C9952A;" +
                "-fx-background-radius: 4 4 0 0;"
            );

            Label countLabel = new Label(String.valueOf(counts[i]));
            countLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: #F5E6C8;"
            );

            Label hourLabel = new Label(formatHourLabel(hours[i]));
            hourLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: #7A5C2E;"
            );

            barCol.getChildren().addAll(countLabel, bar, hourLabel);
            chart.getChildren().add(barCol);
        }

        return chart;
    }

    private String formatHourLabel(int hour24) {
        if (hour24 == 0) return "12am";
        if (hour24 < 12) return hour24 + "am";
        if (hour24 == 12) return "12pm";
        return (hour24 - 12) + "pm";
    }
}
