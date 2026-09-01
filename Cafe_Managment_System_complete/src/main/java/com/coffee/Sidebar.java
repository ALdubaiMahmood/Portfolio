package com.coffee;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.coffee.util.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * One reusable sidebar, shared by every view inside {@link MainLayout}.
 * It is built exactly once and never rebuilt when the user navigates —
 * only the active nav button's style changes.
 */
public class Sidebar {

    private static final String[] ALL_NAV_ITEMS = {
        "Dashboard", "Menu", "Orders", "Order History", "Inventory", "Staff", "Sales Report"
    };

    /** Cashier/Barista don't see Staff Management or Sales Report — Admin-only pages. */
    private static String[] visibleNavItems() {
        if (SessionManager.isAdmin()) {
            return ALL_NAV_ITEMS;
        }
        java.util.List<String> filtered = new ArrayList<>();
        for (String item : ALL_NAV_ITEMS) {
            if (item.equals("Staff") || item.equals("Sales Report")) continue;
            filtered.add(item);
        }
        return filtered.toArray(new String[0]);
    }

    private final VBox view;
    private final Map<String, Button> navButtons = new LinkedHashMap<>();
    private final Consumer<String> onNavigate;
    private String active = "Dashboard";

    public Sidebar(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        this.view = build();
    }

    /** The root node to place in the layout. Build it once, reuse it forever. */
    public VBox getView() {
        return view;
    }

    /** Highlights the given destination as active. Does not trigger navigation. */
    public void setActive(String destination) {
        this.active = destination;
        navButtons.forEach((name, btn) -> styleButton(btn, name.equals(active)));
    }

    private VBox build() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(200);
        sidebar.setMaxWidth(200);
        sidebar.setStyle("-fx-background-color: #2A1500;");

        sidebar.getChildren().addAll(buildLogoBox(), buildNavBox(), buildBottomBox());
        return sidebar;
    }

    // ── LOGO ──
    private VBox buildLogoBox() {
        VBox logoBox = new VBox(6);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(24, 16, 20, 16));
        logoBox.setStyle(
            "-fx-border-color: transparent transparent #3A2200 transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        javafx.scene.Node logoNode;
        try {
            java.io.InputStream stream = getClass()
                .getResourceAsStream("/com/coffee/images/logo.png");
            if (stream != null) {
                ImageView logoImg = new ImageView(new Image(stream));
                logoImg.setFitWidth(55);
                logoImg.setFitHeight(55);
                logoImg.setPreserveRatio(true);
                logoNode = logoImg;
            } else {
                logoNode = fallbackLogoLabel();
            }
        } catch (Exception e) {
            logoNode = fallbackLogoLabel();
        }

        Label brandName = new Label("KAFFE");
        brandName.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #C9952A;" +
            "-fx-letter-spacing: 3px;"
        );

        logoBox.getChildren().addAll(logoNode, brandName);
        return logoBox;
    }

    private Label fallbackLogoLabel() {
        Label l = new Label("KAFFE");
        l.setStyle("-fx-font-size: 14px; -fx-text-fill: #C9952A;");
        return l;
    }

    // ── NAV ──
    private VBox buildNavBox() {
        VBox navBox = new VBox(4);
        navBox.setPadding(new Insets(16, 10, 16, 10));
        VBox.setVgrow(navBox, Priority.ALWAYS);

        for (String item : visibleNavItems()) {
            Button btn = navItem(item);
            navButtons.put(item, btn);
            navBox.getChildren().add(btn);
        }

        setActive(active);
        return navBox;
    }

    private Button navItem(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 0, 0, 14));
        btn.setOnAction(e -> onNavigate.accept(text));
        return btn;
    }

    private void styleButton(Button btn, boolean isActive) {
        if (isActive) {
            btn.setStyle(activeStyle());
            btn.setOnMouseEntered(e -> {});
            btn.setOnMouseExited(e -> {});
        } else {
            btn.setStyle(inactiveStyle());
            btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle()));
            btn.setOnMouseExited(e -> btn.setStyle(inactiveStyle()));
        }
    }

    private String activeStyle() {
        return "-fx-background-color: #C9952A22;" +
               "-fx-text-fill: #e4a21e;" +
               "-fx-font-weight: bold;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 8px;" +
               "-fx-border-color: #e0a32b;" +
               "-fx-border-radius: 8px;" +
               "-fx-border-width: 1px;" +
               "-fx-cursor: hand;";
    }

    private String inactiveStyle() {
        return "-fx-background-color: transparent;" +
               "-fx-text-fill: #7A5C2E;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;";
    }

    private String hoverStyle() {
        return "-fx-background-color: #3A2200;" +
               "-fx-text-fill: #e4a72d;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;";
    }

    // ── BOTTOM (admin badge) ──
    private VBox buildBottomBox() {
        VBox bottomBox = new VBox();
        bottomBox.setPadding(new Insets(12, 10, 16, 10));
        bottomBox.setStyle(
            "-fx-border-color: #3A2200 transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;"
        );

        Label adminBadge = new Label("\u25CF " + SessionManager.getCurrentUserRole());
        adminBadge.setMaxWidth(Double.MAX_VALUE);
        adminBadge.setAlignment(Pos.CENTER);
        adminBadge.setPadding(new Insets(8));
        adminBadge.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-text-fill: #e4a21e;" +
            "-fx-font-size: 11px;" +
            "-fx-background-radius: 6px;"
        );
         Button logoutButton = new Button("Logout");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setAlignment(Pos.CENTER);
        logoutButton.setPrefHeight(36);
        logoutButton.setPadding(new Insets(8));
        logoutButton.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-text-fill: #e4a21e;" +
            "-fx-font-size: 11px;" +
            "-fx-background-radius: 6px;"
        );
        logoutButton.setOnAction(e -> onNavigate.accept("Logout"));

          
        bottomBox.getChildren().add(adminBadge);
        bottomBox.getChildren().add(logoutButton);
        return bottomBox;
    }
}
