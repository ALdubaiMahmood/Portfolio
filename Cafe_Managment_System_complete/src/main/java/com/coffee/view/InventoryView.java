package com.coffee.view;

import com.coffee.MainLayout;
import com.coffee.model.InventoryItem;
import com.coffee.util.ImageStore;
import com.coffee.util.SessionManager;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Inventory management content — no sidebar here, {@link MainLayout} already
 * owns one shared sidebar. This class only builds what goes in the content
 * area. Tracks raw ingredients/materials (coffee beans, milk, cups, etc.),
 * not finished menu items — those live in {@link MenuView}.
 *
 * Items (including pictures) live in InventoryItem's shared, database-backed
 * list — this view never keeps its own copy, so navigating away and back
 * doesn't reset anything.
 */
public class InventoryView {

    private FlowPane inventoryGrid;
    private HBox categoryTabsBox;
    private HBox kpiRow;
    private String activeCategory = "All";

    public InventoryView(MainLayout layout) {
    }

    public VBox getView() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #FFF8E7;");

        HBox topBar = buildTopBar();

        kpiRow = buildKpiRow();
        kpiRow.setPadding(new Insets(20, 24, 0, 24));

        categoryTabsBox = new HBox(8);
        categoryTabsBox.setPadding(new Insets(16, 24, 12, 24));
        categoryTabsBox.setStyle("-fx-background-color: #FFF8E7;");
        refreshCategoryTabs();

        inventoryGrid = new FlowPane();
        inventoryGrid.setHgap(16);
        inventoryGrid.setVgap(16);
        inventoryGrid.setPadding(new Insets(8, 24, 24, 24));
        loadInventoryCards("All");

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(inventoryGrid);
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
            inventoryGrid.setPrefWidth(newVal.getWidth())
        );

        content.getChildren().addAll(topBar, kpiRow, categoryTabsBox, scroll);
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

        Label title = new Label("Inventory Management");
        title.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add Item");
        addBtn.setStyle(
            "-fx-background-color: #C9952A;" +
            "-fx-text-fill: #1A0F00;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 8px 16px;" +
            "-fx-cursor: hand;"
        );
        addBtn.setOnAction(e -> showAddItemDialog());

        Label user = new Label(SessionManager.getCurrentUserName() + "  \u25BE");
        user.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #7A5C2E;" +
            "-fx-padding: 0 0 0 16px;"
        );

        bar.getChildren().addAll(title, spacer, addBtn, user);
        return bar;
    }

    // ── KPI CARDS ──
    private HBox buildKpiRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setFillHeight(true);
        refreshKpiCards(row);
        return row;
    }

    private void refreshKpiCards(HBox row) {
        row.getChildren().clear();

        int total = InventoryItem.getAllItems().size();
        int low = 0;
        int out = 0;
        for (InventoryItem item : InventoryItem.getAllItems()) {
            if (item.isOutOfStock()) out++;
            else if (item.isLowStock()) low++;
        }

        String[][] cards = {
            {String.valueOf(total), "Total Items",  "#d7981a"},
            {String.valueOf(low),   "Low Stock",    "#e67e22"},
            {String.valueOf(out),   "Out of Stock", "#e74c3c"},
        };

        for (String[] card : cards) {
            VBox kpi = new VBox(6);
            kpi.setAlignment(Pos.CENTER);
            kpi.setPadding(new Insets(20, 16, 20, 16));
            kpi.setMinWidth(160);
            kpi.setPrefWidth(200);
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
    }

    // ── CATEGORY TABS ──
    private void refreshCategoryTabs() {
        categoryTabsBox.getChildren().clear();
        String[] categories = {"All", "Coffee & Beans", "Dairy", "Packaging", "Other"};

        for (String cat : categories) {
            Button tab = new Button(cat);

            tab.setPrefHeight(36);
            tab.setPrefWidth(130);
            tab.setMinWidth(130);
            tab.setMaxWidth(130);

            tab.setStyle(cat.equals(activeCategory) ? activeTabStyle() : inactiveTabStyle());

            tab.setOnAction(e -> {
                activeCategory = cat;
                loadInventoryCards(cat);
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
               "-fx-min-width: 130px;" +
               "-fx-max-width: 130px;" +
               "-fx-pref-height: 36px;";
    }

    private String inactiveTabStyle() {
        return "-fx-background-color: #1F1200;" +
               "-fx-text-fill: #7A5C2E;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;" +
               "-fx-min-width: 130px;" +
               "-fx-max-width: 130px;" +
               "-fx-pref-height: 36px;";
    }

    // ── INVENTORY CARDS ──
    private void loadInventoryCards(String category) {
        inventoryGrid.getChildren().clear();

        for (InventoryItem item : InventoryItem.getAllItems()) {
            if (!category.equals("All") && !item.getCategory().equals(category)) continue;
            inventoryGrid.getChildren().add(buildInventoryCard(item));
        }
    }

    /** Builds the small picture/icon box shown at the top of an item card. */
    private VBox buildPictureBox(String imagePath, String fallbackIcon, double height) {
        VBox imgBox = new VBox();
        imgBox.setAlignment(Pos.CENTER);
        imgBox.setPrefHeight(height);
        imgBox.setStyle(
            "-fx-background-color: #2A1500;" +
            "-fx-background-radius: 8px;"
        );

        Image image = ImageStore.loadImage(imagePath);
        if (image != null) {
            ImageView view = new ImageView(image);
            view.setPreserveRatio(false);
            view.setFitHeight(height);
            view.setFitWidth(176);
            Rectangle clip = new Rectangle(176, height);
            clip.setArcWidth(8);
            clip.setArcHeight(8);
            view.setClip(clip);
            imgBox.getChildren().add(view);
        } else {
            Label iconLabel = new Label(fallbackIcon);
            iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #C9952A;"
            );
            imgBox.getChildren().add(iconLabel);
        }
        return imgBox;
    }

    private VBox buildInventoryCard(InventoryItem item) {
        VBox card = new VBox(8);
        card.setPrefWidth(200);
        card.setMaxWidth(200);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-background-radius: 12px;"
        );

        VBox imgBox = buildPictureBox(item.getImagePath(), categoryIcon(item.getCategory()), 70);

        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #F5E6C8;"
        );

        Label qtyLabel = new Label(formatQuantity(item) + "  \u2022  " + item.getCategory());
        qtyLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #C9952A;"
        );

        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label(item.getStatus());
        statusLabel.setPadding(new Insets(3, 8, 3, 8));
        statusLabel.setStyle(
            "-fx-background-color: " + statusColor(item) + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 10px;" +
            "-fx-background-radius: 4px;"
        );

        statusRow.getChildren().add(statusLabel);

        Label restockedLabel = new Label("Restocked: " + item.getLastRestocked());
        restockedLabel.setStyle(
            "-fx-font-size: 9px;" +
            "-fx-text-fill: #7A5C2E;"
        );

        HBox actionRow = new HBox(6);
        actionRow.setAlignment(Pos.CENTER);

        Button editBtn = new Button("Edit");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle(smallButtonStyle("#2A1500", "#C9952A"));
        HBox.setHgrow(editBtn, Priority.ALWAYS);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setStyle(smallButtonStyle("#3B0000", "#E74C3C"));
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);
        deleteBtn.setOnAction(e -> showDeleteConfirm(item));
        editBtn.setOnAction(e -> showEditItemDialog(item));

        actionRow.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(imgBox, nameLabel, qtyLabel, statusRow, restockedLabel, actionRow);
        return card;
    }

    // ── EDIT ITEM DIALOG ──
    private boolean showEditItemDialog(InventoryItem item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");

        TextField nameField = new TextField(item.getName());

        TextField quantityField = new TextField(String.valueOf(item.getQuantity()));

        String[] pickedImagePath = {item.getImagePath()};
        ImageView previewView = new ImageView();
        previewView.setFitWidth(70);
        previewView.setFitHeight(70);
        previewView.setPreserveRatio(false);

        VBox previewBox = new VBox();
        previewBox.setAlignment(Pos.CENTER);
        previewBox.setPrefSize(70, 70);
        previewBox.setMaxSize(70, 70);
        previewBox.setStyle("-fx-background-color: #1F1200; -fx-background-radius: 8px;");

        Image existing = ImageStore.loadImage(item.getImagePath());
        if (existing != null) {
            previewView.setImage(existing);
            previewBox.getChildren().add(previewView);
        } else {
            Label previewPlaceholder = new Label("No\npicture");
            previewPlaceholder.setStyle("-fx-text-fill: #7A5C2E; -fx-font-size: 10px; -fx-text-alignment: center;");
            previewBox.getChildren().add(previewPlaceholder);
        }

        Button pickImageBtn = new Button("Change Picture...");
        pickImageBtn.setOnAction(e -> {
            File file = ImageStore.chooseImageFile(dialog.getDialogPane().getScene().getWindow());
            if (file == null) return;
            Image preview = ImageStore.loadImage(file.getAbsolutePath());
            if (preview == null) return;
            pickedImagePath[0] = file.getAbsolutePath();
            previewView.setImage(preview);
            previewBox.getChildren().setAll(previewView);
        });

        HBox pictureRow = new HBox(10, previewBox, pickImageBtn);
        pictureRow.setAlignment(Pos.CENTER_LEFT);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Quantity:"), 0, 1);
        grid.add(quantityField, 1, 1);

        grid.add(new Label("Picture:"), 0, 2);
        grid.add(pictureRow, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            item.setName(nameField.getText());
            item.setQuantity(Double.parseDouble(quantityField.getText()));
            item.setLastRestocked(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

            if (pickedImagePath[0] != null && !pickedImagePath[0].equals(item.getImagePath())) {
                try {
                    item.setImagePath(ImageStore.storeImage(new File(pickedImagePath[0])));
                } catch (java.io.IOException ex) {
                    // Keep the old picture if the copy fails; don't block the rest of the edit.
                }
            }

            loadInventoryCards(activeCategory);
            refreshKpiCards(kpiRow);

            return true;
        }
        return false;
    }

    private String categoryIcon(String category) {
        switch (category) {
            case "Coffee & Beans": return "[COF]";
            case "Dairy":          return "[DAI]";
            case "Packaging":      return "[PKG]";
            default:               return "[OTH]";
        }
    }

    private String formatQuantity(InventoryItem item) {
        double q = item.getQuantity();
        if (q == Math.floor(q)) {
            return String.format("%.0f %s", q, item.getUnit());
        }
        return String.format("%.1f %s", q, item.getUnit());
    }

    private String statusColor(InventoryItem item) {
        if (item.isOutOfStock()) return "#C0392B";
        if (item.isLowStock())   return "#D98E04";
        return "#1E8449";
    }

    private String smallButtonStyle(String bg, String fg) {
        return "-fx-background-color: " + bg + ";" +
               "-fx-text-fill: " + fg + ";" +
               "-fx-font-size: 10px;" +
               "-fx-background-radius: 6px;" +
               "-fx-cursor: hand;" +
               "-fx-padding: 4 2 4 2;";
    }

    // ── ADD ITEM DIALOG ──
    private void showAddItemDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Inventory Item");
        dialog.setResizable(false);

        VBox layoutBox = new VBox(14);
        layoutBox.setPadding(new Insets(24));
        layoutBox.setStyle("-fx-background-color: #FFF8E7;");

        Label title = new Label("Add New Inventory Item");
        title.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        TextField nameField = new TextField();
        nameField.setPromptText("Item name e.g. Coffee Beans - Arabica");
        nameField.setStyle(fieldStyle());

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Coffee & Beans", "Dairy", "Packaging", "Other");
        categoryBox.setPromptText("Select category");
        categoryBox.setMaxWidth(Double.MAX_VALUE);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity e.g. 10");
        quantityField.setStyle(fieldStyle());

        TextField unitField = new TextField();
        unitField.setPromptText("Unit e.g. kg, L, pcs");
        unitField.setStyle(fieldStyle());

        TextField reorderField = new TextField();
        reorderField.setPromptText("Reorder level e.g. 5");
        reorderField.setStyle(fieldStyle());

        String[] pickedImagePath = {null};
        ImageView previewView = new ImageView();
        previewView.setFitWidth(80);
        previewView.setFitHeight(80);
        previewView.setPreserveRatio(false);

        VBox previewBox = new VBox();
        previewBox.setAlignment(Pos.CENTER);
        previewBox.setPrefSize(80, 80);
        previewBox.setMaxSize(80, 80);
        previewBox.setStyle("-fx-background-color: #1F1200; -fx-background-radius: 8px;");
        Label previewPlaceholder = new Label("No\npicture");
        previewPlaceholder.setStyle("-fx-text-fill: #7A5C2E; -fx-font-size: 10px; -fx-text-alignment: center;");
        previewBox.getChildren().add(previewPlaceholder);

        Label error = new Label("");
        error.setStyle("-fx-text-fill: #C0392B; -fx-font-size: 11px;");

        Button pickImageBtn = new Button("Choose Picture...");
        pickImageBtn.setStyle(
            "-fx-background-color: #E0D0A0;" +
            "-fx-text-fill: #4A2C0A;" +
            "-fx-font-size: 11px;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );
        pickImageBtn.setOnAction(e -> {
            File file = ImageStore.chooseImageFile(dialog);
            if (file == null) return;
            Image preview = ImageStore.loadImage(file.getAbsolutePath());
            if (preview == null) {
                error.setText("Couldn't read that picture — try a different file.");
                return;
            }
            error.setText("");
            pickedImagePath[0] = file.getAbsolutePath();
            previewView.setImage(preview);
            previewBox.getChildren().setAll(previewView);
        });

        HBox pictureRow = new HBox(12, previewBox, pickImageBtn);
        pictureRow.setAlignment(Pos.CENTER_LEFT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setStyle(
            "-fx-background-color: #E0D0A0;" +
            "-fx-text-fill: #4A2C0A;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> dialog.close());

        Button saveBtn = new Button("Save Item");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle(
            "-fx-background-color: #C9952A;" +
            "-fx-text-fill: #1A0F00;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );
        saveBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty() ||
                categoryBox.getValue() == null ||
                quantityField.getText().isEmpty() ||
                unitField.getText().isEmpty() ||
                reorderField.getText().isEmpty()) {
                error.setText("Please fill all fields.");
                return;
            }

            double quantity;
            double reorderLevel;
            try {
                quantity = Double.parseDouble(quantityField.getText());
                reorderLevel = Double.parseDouble(reorderField.getText());
            } catch (NumberFormatException ex) {
                error.setText("Quantity and reorder level must be numbers.");
                return;
            }

            String storedImagePath = null;
            if (pickedImagePath[0] != null) {
                try {
                    storedImagePath = ImageStore.storeImage(new File(pickedImagePath[0]));
                } catch (java.io.IOException ex) {
                    error.setText("Couldn't save the picture: " + ex.getMessage());
                    return;
                }
            }

            InventoryItem newItem = new InventoryItem(
                nameField.getText(),
                categoryBox.getValue(),
                quantity,
                unitField.getText(),
                reorderLevel,
                "Not yet restocked",
                storedImagePath
            );
            InventoryItem.addItem(newItem);
            loadInventoryCards(activeCategory);
            refreshKpiCards(kpiRow);
            dialog.close();
        });

        HBox btnRow = new HBox(10, cancelBtn, saveBtn);
        HBox.setHgrow(cancelBtn, Priority.ALWAYS);
        HBox.setHgrow(saveBtn,   Priority.ALWAYS);

        layoutBox.getChildren().addAll(
            title,
            new Label("Item Name:"),     nameField,
            new Label("Category:"),      categoryBox,
            new Label("Quantity:"),      quantityField,
            new Label("Unit:"),          unitField,
            new Label("Reorder Level:"), reorderField,
            new Label("Picture:"),       pictureRow,
            error, btnRow
        );

                javafx.scene.control.ScrollPane dialogScroll = new javafx.scene.control.ScrollPane(layoutBox);
        dialogScroll.setFitToWidth(true);
        dialogScroll.setStyle("-fx-background: #FFF8E7; -fx-background-color: #FFF8E7; -fx-border-color: transparent;");

        Scene dialogScene = new Scene(dialogScroll, 360, 600);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    // ── DELETE CONFIRM ──
    private void showDeleteConfirm(InventoryItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Remove " + item.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                InventoryItem.removeItem(item);
                loadInventoryCards(activeCategory);
                refreshKpiCards(kpiRow);
            }
        });
    }

    private String fieldStyle() {
        return "-fx-background-color: #1F1200;" +
               "-fx-border-color: #3A2200;" +
               "-fx-border-radius: 6px;" +
               "-fx-background-radius: 6px;" +
               "-fx-text-fill: #FFD700;" +
               "-fx-prompt-text-fill: #5A3E1A;" +
               "-fx-font-size: 12px;" +
               "-fx-padding: 8px 12px;";
    }
}