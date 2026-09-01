package com.coffee.view;

import com.coffee.MainLayout;
import com.coffee.model.MenuItem;
import com.coffee.util.ImageStore;
import com.coffee.util.SessionManager;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Menu management content — no sidebar here, {@link MainLayout} already owns
 * one shared sidebar. This class only builds what goes in the content area.
 *
 * Menu items themselves (including pictures) live in {@link MenuItem}'s
 * shared static list, so pictures added here also show up on the New Order
 * page.
 */
public class MenuView {

    @SuppressWarnings("unused")
    private final MainLayout layout;
    private FlowPane menuGrid;
    private String activeCategory = "All";
    private HBox categoryTabsBox;

    public MenuView(MainLayout layout) {
        this.layout = layout;
    }

    public VBox getView() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #FFF8E7;");

        HBox topBar = buildTopBar();

        categoryTabsBox = new HBox(8);
        categoryTabsBox.setPadding(new Insets(16, 24, 12, 24));
        categoryTabsBox.setStyle("-fx-background-color: #FFF8E7;");
        refreshCategoryTabs();

        menuGrid = new FlowPane();
        menuGrid.setHgap(16);
        menuGrid.setVgap(16);
        menuGrid.setPadding(new Insets(8, 24, 24, 24));
        loadMenuCards("All");

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(menuGrid);
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
            menuGrid.setPrefWidth(newVal.getWidth())
        );

        content.getChildren().addAll(topBar, categoryTabsBox, scroll);
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

        Label title = new Label("Menu Management");
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

    // ── CATEGORY TABS ──
    private void refreshCategoryTabs() {
        categoryTabsBox.getChildren().clear();
        String[] categories = {"All", "Hot Drinks", "Cold Drinks", "Food", "Desserts"};

        for (String cat : categories) {
            Button tab = new Button(cat);

            tab.setPrefHeight(36);
            tab.setPrefWidth(115);
            tab.setMinWidth(115);
            tab.setMaxWidth(115);

            tab.setStyle(cat.equals(activeCategory) ? activeTabStyle() : inactiveTabStyle());

            tab.setOnAction(e -> {
                activeCategory = cat;
                loadMenuCards(cat);
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
               "-fx-min-width: 115px;" +
               "-fx-max-width: 115px;" +
               "-fx-pref-height: 36px;";
    }

    private String inactiveTabStyle() {
        return "-fx-background-color: #1F1200;" +
               "-fx-text-fill: #7A5C2E;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;" +
               "-fx-min-width: 115px;" +
               "-fx-max-width: 115px;" +
               "-fx-pref-height: 36px;";
    }

    // ── MENU CARDS ──
    private void loadMenuCards(String category) {
        menuGrid.getChildren().clear();

        for (MenuItem item : MenuItem.getAllItems()) {
            if (!category.equals("All") && !item.getCategory().equals(category)) continue;
            menuGrid.getChildren().add(buildMenuCard(item));
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
            view.setFitWidth(166); // card width minus padding
            Rectangle clip = new Rectangle(166, height);
            clip.setArcWidth(8);
            clip.setArcHeight(8);
            view.setClip(clip);
            imgBox.getChildren().add(view);
        } else {
            Label iconLabel = new Label(fallbackIcon);
            iconLabel.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #C9952A;"
            );
            imgBox.getChildren().add(iconLabel);
        }
        return imgBox;
    }

    private VBox buildMenuCard(MenuItem menuItem) {
        String name   = menuItem.getName();
        String price  = menuItem.getPrice();
        String status = menuItem.getStatus();

        VBox card = new VBox(8);
        card.setPrefWidth(190);
        card.setMaxWidth(190);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-background-radius: 12px;"
        );

        VBox imgBox = buildPictureBox(menuItem.getImagePath(), menuItem.getFallbackIcon(), 90);

        Label nameLabel = new Label(name);
        nameLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #F5E6C8;"
        );

        HBox priceRow = new HBox();
        priceRow.setAlignment(Pos.CENTER_LEFT);

        Label priceLabel = new Label(price);
        priceLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #C9952A;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean available = status.equals("Available");
        Label statusLabel = new Label(status);
        statusLabel.setPadding(new Insets(3, 8, 3, 8));
        statusLabel.setStyle(
            "-fx-background-color: " + (available ? "#1E8449" : "#C0392B") + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 10px;" +
            "-fx-background-radius: 4px;"
        );

        priceRow.getChildren().addAll(priceLabel, spacer, statusLabel);

        HBox actionRow = new HBox(8);
        actionRow.setAlignment(Pos.CENTER);

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> showEditDialog(menuItem));
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle(
            "-fx-background-color: #2A1500;" +
            "-fx-text-fill: #C9952A;" +
            "-fx-font-size: 11px;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );
        HBox.setHgrow(editBtn, Priority.ALWAYS);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setStyle(
            "-fx-background-color: #3B0000;" +
            "-fx-text-fill: #E74C3C;" +
            "-fx-font-size: 11px;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);

        deleteBtn.setOnAction(e -> showDeleteConfirm(menuItem));

        actionRow.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().addAll(imgBox, nameLabel, priceRow, actionRow);
        return card;
    }

    // ── ADD ITEM DIALOG ──
    private void showAddItemDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Item");
        dialog.setResizable(false);

        VBox layoutBox = new VBox(14);
        layoutBox.setPadding(new Insets(24));
        layoutBox.setStyle("-fx-background-color: #FFF8E7;");

        Label title = new Label("Add New Menu Item");
        title.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        TextField nameField = new TextField();
        nameField.setPromptText("Item name");
        nameField.setStyle(fieldStyle());

        TextField priceField = new TextField();
        priceField.setPromptText("Price e.g. RM 5.00");
        priceField.setStyle(fieldStyle());

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Hot Drinks", "Cold Drinks", "Food", "Desserts");
        categoryBox.setPromptText("Select category");
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        categoryBox.setStyle(fieldStyle());
        categoryBox.setButtonCell(brightListCell());
        categoryBox.setCellFactory(lv -> brightListCell());

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Available", "Sold Out");
        statusBox.setValue("Available");
        statusBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.setStyle(fieldStyle());
        statusBox.setButtonCell(brightListCell());
        statusBox.setCellFactory(lv -> brightListCell());

        // ── Picture picker ──
        String[] pickedImagePath = {null}; // holder so the lambda below can write to it
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

        Button pickImageBtn = new Button("Choose Picture...");
        pickImageBtn.setStyle(
            "-fx-background-color: #E0D0A0;" +
            "-fx-text-fill: #4A2C0A;" +
            "-fx-font-size: 11px;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );

        Label error = new Label("");
        error.setStyle("-fx-text-fill: #C0392B; -fx-font-size: 11px;");

        pickImageBtn.setOnAction(e -> {
            File file = ImageStore.chooseImageFile(dialog);
            if (file == null) return;
            Image preview = ImageStore.loadImage(file.getAbsolutePath());
            if (preview == null) {
                error.setText("Couldn't read that picture — try a different file.");
                return;
            }
            error.setText("");
            pickedImagePath[0] = file.getAbsolutePath(); // stored permanently on Save
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
                priceField.getText().isEmpty() ||
                categoryBox.getValue() == null) {
                error.setText("Please fill all fields.");
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

            MenuItem newItem = new MenuItem(
                nameField.getText(),
                priceField.getText(),
                categoryBox.getValue(),
                statusBox.getValue(),
                storedImagePath
            );
            MenuItem.addItem(newItem);
            loadMenuCards(activeCategory);
            dialog.close();
        });

        HBox btnRow = new HBox(10, cancelBtn, saveBtn);
        HBox.setHgrow(cancelBtn, Priority.ALWAYS);
        HBox.setHgrow(saveBtn,   Priority.ALWAYS);

        layoutBox.getChildren().addAll(
            title,
            new Label("Item Name:"),  nameField,
            new Label("Price:"),      priceField,
            new Label("Category:"),   categoryBox,
            new Label("Status:"),     statusBox,
            new Label("Picture:"),    pictureRow,
            error, btnRow
        );

               javafx.scene.control.ScrollPane dialogScroll = new javafx.scene.control.ScrollPane(layoutBox);
        dialogScroll.setFitToWidth(true);
        dialogScroll.setStyle("-fx-background: #FFF8E7; -fx-background-color: #FFF8E7; -fx-border-color: transparent;");

        Scene dialogScene = new Scene(dialogScroll, 360, 560);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    // ── EDIT ITEM DIALOG ──
    private void showEditDialog(MenuItem menuItem) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Item");
        dialog.setResizable(false);

        VBox layoutBox = new VBox(14);
        layoutBox.setPadding(new Insets(24));
        layoutBox.setStyle("-fx-background-color: #FFF8E7;");

        Label title = new Label("Edit " + menuItem.getName());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4A2C0A;");

        TextField priceField = new TextField(menuItem.getPrice());
        priceField.setStyle(fieldStyle());

        String[] pickedImagePath = {menuItem.getImagePath()};
        ImageView previewView = new ImageView();
        previewView.setFitWidth(80);
        previewView.setFitHeight(80);
        previewView.setPreserveRatio(false);

        VBox previewBox = new VBox();
        previewBox.setAlignment(Pos.CENTER);
        previewBox.setPrefSize(80, 80);
        previewBox.setMaxSize(80, 80);
        previewBox.setStyle("-fx-background-color: #1F1200; -fx-background-radius: 8px;");

        Image existing = ImageStore.loadImage(menuItem.getImagePath());
        if (existing != null) {
            previewView.setImage(existing);
            previewBox.getChildren().add(previewView);
        } else {
            Label previewPlaceholder = new Label("No\npicture");
            previewPlaceholder.setStyle("-fx-text-fill: #7A5C2E; -fx-font-size: 10px; -fx-text-alignment: center;");
            previewBox.getChildren().add(previewPlaceholder);
        }

        Label error = new Label("");
        error.setStyle("-fx-text-fill: #C0392B; -fx-font-size: 11px;");

        Button pickImageBtn = new Button("Change Picture...");
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

        Button saveBtn = new Button("Save Changes");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle(
            "-fx-background-color: #C9952A;" +
            "-fx-text-fill: #1A0F00;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );
        saveBtn.setOnAction(e -> {
            menuItem.setPrice(priceField.getText());

            // Only re-copy the picture if it actually changed (a fresh pick,
            // not the item's existing already-stored path).
            if (pickedImagePath[0] != null && !pickedImagePath[0].equals(menuItem.getImagePath())) {
                try {
                    menuItem.setImagePath(ImageStore.storeImage(new File(pickedImagePath[0])));
                } catch (java.io.IOException ex) {
                    error.setText("Couldn't save the picture: " + ex.getMessage());
                    return;
                }
            }

            loadMenuCards(activeCategory);
            dialog.close();
        });

        HBox btnRow = new HBox(10, cancelBtn, saveBtn);
        HBox.setHgrow(cancelBtn, Priority.ALWAYS);
        HBox.setHgrow(saveBtn,   Priority.ALWAYS);

        layoutBox.getChildren().addAll(
            title,
            new Label("Price:"),   priceField,
            new Label("Picture:"), pictureRow,
            error, btnRow
        );

              javafx.scene.control.ScrollPane dialogScroll = new javafx.scene.control.ScrollPane(layoutBox);
        dialogScroll.setFitToWidth(true);
        dialogScroll.setStyle("-fx-background: #FFF8E7; -fx-background-color: #FFF8E7; -fx-border-color: transparent;");

        Scene dialogScene = new Scene(dialogScroll, 340, 400);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    // ── DELETE CONFIRM ──
    private void showDeleteConfirm(MenuItem menuItem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Remove " + menuItem.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                MenuItem.removeItem(menuItem);
                loadMenuCards(activeCategory);
            }
        });
    }

    private ListCell<String> brightListCell() {
        return new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle(
                        "-fx-text-fill: #FFD700;" +
                        "-fx-background-color: #1F1200;"
                    );
                }
            }
        };
    }

    private String fieldStyle() {
        return "-fx-background-color: #1F1200;" +
               "-fx-border-color: #3A2200;" +
               "-fx-border-radius: 6px;" +
               "-fx-background-radius: 6px;" +
               "-fx-text-fill: #FFD700;" +
               "-fx-prompt-text-fill: #C9A66B;" +
               "-fx-font-size: 12px;" +
               "-fx-padding: 8px 12px;";
    }
}