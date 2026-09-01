package com.coffee.view;

import com.coffee.MainLayout;
import com.coffee.util.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Dashboard content — no sidebar here, {@link MainLayout} already owns one
 * shared sidebar. This class only builds what goes in the content area.
 */
public class DashboardView {

    private final MainLayout layout;

    public DashboardView(MainLayout layout) {
        this.layout = layout;
    }

    public VBox getView() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #FFF8E7;");

        HBox topBar = buildTopBar();

        VBox body = new VBox(24);
        body.setPadding(new Insets(28, 28, 28, 28));

        HBox kpiRow = buildKpiRow();

        HBox bottomRow = new HBox(20);
        VBox recentOrders = buildRecentOrders();
        VBox quickActions = buildQuickActions();
        recentOrders.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(recentOrders, Priority.ALWAYS);
        HBox.setHgrow(quickActions, Priority.NEVER);
        bottomRow.getChildren().addAll(recentOrders, quickActions);

        body.getChildren().addAll(kpiRow, bottomRow);

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) ->
            body.setMinHeight(newBounds.getHeight())
        );
        scroll.setStyle(
            "-fx-background: #FFF8E7;" +
            "-fx-background-color: #FFF8E7;"
        );
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topBar, scroll);
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

        Label title = new Label("Dashboard");
        title.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label user = new Label(SessionManager.getCurrentUserName() + "  \u25BE");
        user.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #7A5C2E;" +
            "-fx-cursor: hand;"
        );

        bar.getChildren().addAll(title, spacer, user);
        return bar;
    }

    // ── KPI CARDS ──
    private HBox buildKpiRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setFillHeight(true);

        String[][] cards = {
            {"48",    "Today's Orders", "#d7981a"},
            {"1,240", "Revenue (RM)",   "#08bf54"},
            {"24",    "Menu Items",     "#1189d4"},
            {"5",     "Staff On Duty",  "#a00edf"},
        };

        for (String[] card : cards) {
            VBox kpi = new VBox(6);
            kpi.setAlignment(Pos.CENTER);
            kpi.setPadding(new Insets(20, 16, 20, 16));
            kpi.setMinWidth(180);
            kpi.setPrefWidth(220);
            kpi.setMaxWidth(Double.MAX_VALUE);
            kpi.setStyle(
                "-fx-background-color: #1F1200;" +
                "-fx-background-radius: 12px;"
            );
            HBox.setHgrow(kpi, Priority.ALWAYS);

            Label value = new Label(card[0]);
            value.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + card[2] + ";"
            );

            Label label = new Label(card[1]);
            label.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #7A5C2E;"
            );

            kpi.getChildren().addAll(value, label);
            row.getChildren().add(kpi);
        }

        return row;
    }

    // ── RECENT ORDERS TABLE ──
    private VBox buildRecentOrders() {
        VBox box = new VBox(12);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);

        Label title = new Label("Recent Orders");
        title.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        TableView<String[]> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-background-radius: 10px;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(table, Priority.ALWAYS);
        table.setPrefHeight(260);
        table.setMaxHeight(260);
        table.setPlaceholder(new Label("No orders yet."));

        TableColumn<String[], String> colOrder = new TableColumn<>("Order #");
        colOrder.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));

        TableColumn<String[], String> colItems = new TableColumn<>("Items");
        colItems.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));

        TableColumn<String[], String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));

        TableColumn<String[], String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));

        table.getColumns().addAll(colOrder, colItems, colTotal, colStatus);

        table.getItems().addAll(
            new String[]{"#1042", "Latte x2, Muffin",  "RM 18.00", "Done"},
            new String[]{"#1041", "Americano x1",       "RM 6.00",  "Done"},
            new String[]{"#1040", "Cappuccino x3",      "RM 21.00", "In Progress"},
            new String[]{"#1039", "Espresso x1, Cake",  "RM 15.00", "Done"}
        );

        Label footer = new Label("Last updated: Today, 2:45 PM");
        footer.setStyle("-fx-font-size: 10px; -fx-text-fill: #7A5C2E;");

        box.getChildren().addAll(title, table, footer);
        return box;
    }

    // ── QUICK ACTIONS ──
    private VBox buildQuickActions() {
        VBox box = new VBox(12);
        box.setPrefWidth(200);
        box.setMaxWidth(200);
        box.setMinWidth(200);

        Label title = new Label("Quick Actions");
        title.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );
        box.getChildren().add(title);

        String[] actions = {"+ New Order", "View Menu", "Staff List", "Sales Report", "Inventory"};

        for (String action : actions) {
            Button btn = new Button(action);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPrefHeight(44);
            btn.setStyle(
                "-fx-background-color: #1F1200;" +
                "-fx-text-fill: #C9952A;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
            );
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #2A1500;" +
                "-fx-text-fill: #E5A830;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-color: #C9952A;" +
                "-fx-border-radius: 8px;" +
                "-fx-cursor: hand;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #1F1200;" +
                "-fx-text-fill: #C9952A;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
            ));

            btn.setOnAction(e -> {
                switch (action) {
                    case "View Menu":
                        layout.navigate("Menu");
                        break;
                    case "Staff List":
                        layout.navigate("Staff");
                        break;
                    case "Sales Report":
                        layout.navigate("Sales Report");
                        break;
                    case "+ New Order":
                        layout.navigate("Orders");
                        break;
                    case "Inventory":
                        layout.navigate("Inventory");
                        break;
                }
            });

            box.getChildren().add(btn);
        }

        return box;
    }
}
