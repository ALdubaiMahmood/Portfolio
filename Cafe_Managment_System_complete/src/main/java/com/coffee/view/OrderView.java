package com.coffee.view;

import com.coffee.MainLayout;
import com.coffee.model.MenuItem;
import com.coffee.model.OrderRecord;
import com.coffee.util.ImageStore;
import com.coffee.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * "New Order" content — no sidebar here, {@link MainLayout} already owns one
 * shared sidebar. Cashiers pick menu items on the left; the running order
 * (table number, line items, subtotal/tax/total) lives in the right panel.
 *
 * Items (including pictures) are read live from {@link MenuItem}'s shared
 * list, the same one Menu Management edits — so any picture added there
 * shows up here too, for existing items and any added in the future.
 */
public class OrderView {

    private static final double TAX_RATE = 0.06;

    private final MainLayout layout;

    private String activeCategory = "All";
    private FlowPane itemsGrid;
    private HBox categoryTabsBox;

    private final List<CartLine> cartLines = new ArrayList<>();
    private VBox summaryLinesBox;
    private TextField tableField;
    private Label subtotalValueLabel;
    private Label taxValueLabel;
    private Label totalValueLabel;

    private static class CartLine {
        final String name;
        final double price;
        int quantity;

        CartLine(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }

    public OrderView(MainLayout layout) {
        this.layout = layout;
    }

    public VBox getView() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #FFF8E7;");

        HBox topBar = buildTopBar();

        HBox body = new HBox(0);
        VBox.setVgrow(body, Priority.ALWAYS);

        VBox leftColumn = buildLeftColumn();
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox rightColumn = buildSummaryPanel();
        rightColumn.setPrefWidth(340);
        rightColumn.setMinWidth(340);
        rightColumn.setMaxWidth(340);

        body.getChildren().addAll(leftColumn, rightColumn);

        content.getChildren().addAll(topBar, body);
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

        Label title = new Label("New Order");
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
            "-fx-padding: 0 0 0 16px;"
        );

        bar.getChildren().addAll(title, spacer, user);
        return bar;
    }

    // ── LEFT COLUMN: ITEM SELECTION ──
    private VBox buildLeftColumn() {
        VBox col = new VBox(0);

        Label sectionTitle = new Label("Select Items");
        sectionTitle.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );
        sectionTitle.setPadding(new Insets(16, 24, 0, 24));

        categoryTabsBox = new HBox(8);
        categoryTabsBox.setPadding(new Insets(12, 24, 12, 24));
        categoryTabsBox.setStyle("-fx-background-color: #FFF8E7;");
        refreshCategoryTabs();

        itemsGrid = new FlowPane();
        itemsGrid.setHgap(16);
        itemsGrid.setVgap(16);
        itemsGrid.setPadding(new Insets(8, 24, 24, 24));
        loadItemCards("All");

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(itemsGrid);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setPannable(false);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(
            "-fx-background: #FFF8E7;" +
            "-fx-background-color: #FFF8E7;" +
            "-fx-border-color: transparent;"
        );
        VBox.setVgrow(scroll, Priority.ALWAYS);

        scroll.viewportBoundsProperty().addListener((obs, oldVal, newVal) ->
            itemsGrid.setPrefWidth(newVal.getWidth())
        );

        col.getChildren().addAll(sectionTitle, categoryTabsBox, scroll);
        return col;
    }

    private void refreshCategoryTabs() {
        categoryTabsBox.getChildren().clear();
        String[] categories = {"All", "Hot Drinks", "Cold Drinks", "Food", "Desserts"};

        for (String cat : categories) {
            Button tab = new Button(cat);
            tab.setPrefHeight(34);
            tab.setPrefWidth(110);
            tab.setMinWidth(110);
            tab.setMaxWidth(110);
            tab.setStyle(cat.equals(activeCategory) ? activeTabStyle() : inactiveTabStyle());

            tab.setOnAction(e -> {
                activeCategory = cat;
                loadItemCards(cat);
                refreshCategoryTabs();
            });

            categoryTabsBox.getChildren().add(tab);
        }
    }

    private String activeTabStyle() {
        return "-fx-background-color: #C9952A;" +
               "-fx-text-fill: #1A0F00;" +
               "-fx-font-weight: bold;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;" +
               "-fx-min-width: 110px;" +
               "-fx-max-width: 110px;" +
               "-fx-pref-height: 34px;";
    }

    private String inactiveTabStyle() {
        return "-fx-background-color: #1F1200;" +
               "-fx-text-fill: #7A5C2E;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;" +
               "-fx-min-width: 110px;" +
               "-fx-max-width: 110px;" +
               "-fx-pref-height: 34px;";
    }

    private void loadItemCards(String category) {
        itemsGrid.getChildren().clear();

        for (MenuItem item : MenuItem.getAllItems()) {
            if (!category.equals("All") && !item.getCategory().equals(category)) continue;
            itemsGrid.getChildren().add(buildItemCard(item));
        }
    }

    /** Small thumbnail shown on each item card; falls back to initials when there's no picture yet. */
    private VBox buildThumbnail(String imagePath, String fallbackIcon) {
        VBox imgBox = new VBox();
        imgBox.setAlignment(Pos.CENTER);
        imgBox.setPrefSize(40, 40);
        imgBox.setMinSize(40, 40);
        imgBox.setMaxSize(40, 40);
        imgBox.setStyle(
            "-fx-background-color: #2A1500;" +
            "-fx-background-radius: 6px;"
        );

        Image image = ImageStore.loadImage(imagePath);
        if (image != null) {
            ImageView view = new ImageView(image);
            view.setPreserveRatio(false);
            view.setFitWidth(40);
            view.setFitHeight(40);
            Rectangle clip = new Rectangle(40, 40);
            clip.setArcWidth(6);
            clip.setArcHeight(6);
            view.setClip(clip);
            imgBox.getChildren().add(view);
        } else {
            Label iconLabel = new Label(fallbackIcon);
            iconLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #C9952A;"
            );
            imgBox.getChildren().add(iconLabel);
        }
        return imgBox;
    }

    private VBox buildItemCard(MenuItem menuItem) {
        String name = menuItem.getName();
        String priceText = menuItem.getPrice();
        String status = menuItem.getStatus();

        VBox card = new VBox(6);
        card.setPrefWidth(150);
        card.setMaxWidth(150);
        card.setPadding(new Insets(10));
        card.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-background-radius: 10px;"
        );

        boolean available = status.equals("Available");
        double price = parsePrice(priceText);

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox thumbnail = buildThumbnail(menuItem.getImagePath(), menuItem.getFallbackIcon());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+");
        if (available) {
            addBtn.setStyle(
                "-fx-background-color: #C9952A;" +
                "-fx-text-fill: #1A0F00;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 14px;" +
                "-fx-min-width: 26px;" +
                "-fx-min-height: 26px;" +
                "-fx-cursor: hand;"
            );
            addBtn.setOnAction(e -> addToCart(name, price));
        } else {
            addBtn.setStyle(
                "-fx-background-color: #3A2200;" +
                "-fx-text-fill: #7A5C2E;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 14px;" +
                "-fx-min-width: 26px;" +
                "-fx-min-height: 26px;"
            );
        }

        headerRow.getChildren().addAll(thumbnail, spacer, addBtn);

        Label nameLabel = new Label(name);
        nameLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #F5E6C8;"
        );

        Label priceLabel = new Label(available ? priceText : "Sold Out");
        priceLabel.setStyle(
            available
                ? "-fx-font-size: 11px; -fx-text-fill: #C9952A;"
                : "-fx-font-size: 11px; -fx-text-fill: #C0392B;"
        );

        card.getChildren().addAll(headerRow, nameLabel, priceLabel);
        return card;
    }

    private double parsePrice(String priceText) {
        return Double.parseDouble(priceText.replace("RM", "").trim());
    }

    // ── RIGHT COLUMN: ORDER SUMMARY ──
    private VBox buildSummaryPanel() {
        VBox panel = new VBox(14);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #1F1200;");

        Label title = new Label("Order Summary");
        title.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #F5E6C8;"
        );
        HBox titleRow = new HBox();
        titleRow.getChildren().add(title);
        titleRow.setAlignment(Pos.CENTER);

        HBox tableRow = new HBox(8);
        tableRow.setAlignment(Pos.CENTER_LEFT);
        Label tableLabel = new Label("Table No:");
        tableLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #C9952A;"
        );
        tableField = new TextField();
        tableField.setText("01");
        tableField.setPrefWidth(60);
        tableField.setStyle(fieldStyle());
        tableRow.getChildren().addAll(tableLabel, tableField);

        summaryLinesBox = new VBox(10);

        ScrollPane linesScroll = new ScrollPane();
        linesScroll.setContent(summaryLinesBox);
        linesScroll.setFitToWidth(true);
        linesScroll.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );
        VBox.setVgrow(linesScroll, Priority.ALWAYS);

        Region divider1 = new Region();
        divider1.setPrefHeight(1);
        divider1.setStyle("-fx-background-color: #3A2200;");

        Region divider2 = new Region();
        divider2.setPrefHeight(1);
        divider2.setStyle("-fx-background-color: #3A2200;");

        subtotalValueLabel = new Label("RM 0.00");
        taxValueLabel = new Label("RM 0.00");
        totalValueLabel = new Label("RM 0.00");

        VBox totalsBox = new VBox(6);
        totalsBox.getChildren().addAll(
            totalsRow("Subtotal:",  subtotalValueLabel, false),
            totalsRow("Tax (6%):",  taxValueLabel,      false),
            totalsRow("TOTAL:",     totalValueLabel,     true)
        );

        Button placeOrderBtn = new Button("Place Order");
        placeOrderBtn.setMaxWidth(Double.MAX_VALUE);
        placeOrderBtn.setStyle(
            "-fx-background-color: #C9952A;" +
            "-fx-text-fill: #1A0F00;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 10px;" +
            "-fx-cursor: hand;"
        );
        placeOrderBtn.setOnAction(e -> handlePlaceOrder());

        Button clearOrderBtn = new Button("Clear Order");
        clearOrderBtn.setMaxWidth(Double.MAX_VALUE);
        clearOrderBtn.setStyle(
            "-fx-background-color: #2A1500;" +
            "-fx-text-fill: #C9952A;" +
            "-fx-font-size: 12px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 8px;" +
            "-fx-cursor: hand;"
        );
        clearOrderBtn.setOnAction(e -> handleClearOrder());

        refreshSummaryLines();
        refreshTotals();

        panel.getChildren().addAll(
            titleRow, tableRow, divider1, linesScroll, divider2,
            totalsBox, placeOrderBtn, clearOrderBtn
        );
        return panel;
    }

    private HBox totalsRow(String labelText, Label valueLabel, boolean bold) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.setStyle(
            bold
                ? "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F5E6C8;"
                : "-fx-font-size: 12px; -fx-text-fill: #C9952A;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        valueLabel.setStyle(
            bold
                ? "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F5E6C8;"
                : "-fx-font-size: 12px; -fx-text-fill: #C9952A;"
        );

        row.getChildren().addAll(label, spacer, valueLabel);
        return row;
    }

    // ── CART LOGIC ──
    private void addToCart(String name, double price) {
        for (CartLine line : cartLines) {
            if (line.name.equals(name)) {
                line.quantity++;
                refreshSummaryLines();
                refreshTotals();
                return;
            }
        }
        cartLines.add(new CartLine(name, price, 1));
        refreshSummaryLines();
        refreshTotals();
    }

    private void removeLine(CartLine line) {
        cartLines.remove(line);
        refreshSummaryLines();
        refreshTotals();
    }

    private void refreshSummaryLines() {
        summaryLinesBox.getChildren().clear();

        if (cartLines.isEmpty()) {
            Label empty = new Label("No items added yet.");
            empty.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #7A5C2E;"
            );
            summaryLinesBox.getChildren().add(empty);
            return;
        }

        for (CartLine line : cartLines) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(line.quantity + "x " + line.name);
            nameLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #F5E6C8;"
            );
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Label priceLabel = new Label(String.format("RM %.2f", line.price * line.quantity));
            priceLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #C9952A;"
            );

            Button removeBtn = new Button("\u2715");
            removeBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #C0392B;" +
                "-fx-font-size: 11px;" +
                "-fx-cursor: hand;"
            );
            removeBtn.setOnAction(e -> removeLine(line));

            row.getChildren().addAll(nameLabel, priceLabel, removeBtn);
            summaryLinesBox.getChildren().add(row);
        }
    }

    private void refreshTotals() {
        double subtotal = 0;
        for (CartLine line : cartLines) {
            subtotal += line.price * line.quantity;
        }
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        subtotalValueLabel.setText(String.format("RM %.2f", subtotal));
        taxValueLabel.setText(String.format("RM %.2f", tax));
        totalValueLabel.setText(String.format("RM %.2f", total));
    }

    private void handlePlaceOrder() {
        if (cartLines.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Order");
            alert.setHeaderText("No items selected");
            alert.setContentText("Add at least one item before placing the order.");
            alert.showAndWait();
            return;
        }

        double subtotal = 0;
        for (CartLine line : cartLines) {
            subtotal += line.price * line.quantity;
        }
        double total = subtotal * (1 + TAX_RATE);

        List<OrderRecord.LineItem> lineItems = new ArrayList<>();
        for (CartLine line : cartLines) {
            lineItems.add(new OrderRecord.LineItem(line.name, line.quantity, line.price));
        }
        OrderRecord.recordNewOrder(SessionManager.getCurrentUserName(), lineItems, total);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Placed");
        alert.setHeaderText("Order placed for Table " + tableField.getText());
        alert.setContentText(String.format("Total: RM %.2f", total));
        alert.showAndWait();

        cartLines.clear();
        refreshSummaryLines();
        refreshTotals();
    }

    private void handleClearOrder() {
        cartLines.clear();
        refreshSummaryLines();
        refreshTotals();
    }

    private String fieldStyle() {
        return "-fx-background-color: #2A1500;" +
               "-fx-border-color: #3A2200;" +
               "-fx-border-radius: 6px;" +
               "-fx-background-radius: 6px;" +
               "-fx-text-fill: #FFD700;" +
               "-fx-font-size: 12px;" +
               "-fx-padding: 6px 10px;";
    }
}