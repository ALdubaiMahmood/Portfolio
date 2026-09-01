package com.coffee.view;

import com.coffee.MainLayout;
import com.coffee.model.StaffMember;
import com.coffee.util.SessionManager;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Staff Management content — no sidebar here, {@link MainLayout} already
 * owns one shared sidebar. This class only builds what goes in the content
 * area. Admin-only: {@link MainLayout} blocks navigation here for other roles.
 */
public class StaffView {

    private final MainLayout layout;

    private FlowPane staffGrid;

    public StaffView(MainLayout layout) {
        this.layout = layout;
    }

    public VBox getView() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #FFF8E7;");

        HBox topBar = buildTopBar();

        staffGrid = new FlowPane();
        staffGrid.setHgap(16);
        staffGrid.setVgap(16);
        staffGrid.setPadding(new Insets(20, 24, 24, 24));
        loadStaffCards();

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(staffGrid);
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
            staffGrid.setPrefWidth(newVal.getWidth())
        );

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

        Label title = new Label("Staff Management");
        title.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add Staff");
        addBtn.setStyle(
            "-fx-background-color: #C9952A;" +
            "-fx-text-fill: #1A0F00;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 8px 16px;" +
            "-fx-cursor: hand;"
        );
        addBtn.setOnAction(e -> showAddStaffDialog());

        Label user = new Label(SessionManager.getCurrentUserName() + "  \u25BE");
        user.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #7A5C2E;" +
            "-fx-padding: 0 0 0 16px;"
        );

        bar.getChildren().addAll(title, spacer, addBtn, user);
        return bar;
    }

    // ── STAFF CARDS ──
    private void loadStaffCards() {
        staffGrid.getChildren().clear();
        for (StaffMember staff : StaffMember.getAllStaff()) {
            staffGrid.getChildren().add(buildStaffCard(staff));
        }
    }

    private VBox buildStaffCard(StaffMember staff) {
        VBox card = new VBox(10);
        card.setPrefWidth(220);
        card.setMaxWidth(220);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-background-radius: 12px;"
        );

        StackPane avatar = new StackPane();
        avatar.setPrefSize(64, 64);
        avatar.setMinSize(64, 64);
        avatar.setMaxSize(64, 64);
        avatar.setStyle(
            "-fx-background-color: #2A1500;" +
            "-fx-background-radius: 32px;" +
            "-fx-border-color: #C9952A;" +
            "-fx-border-radius: 32px;" +
            "-fx-border-width: 2px;"
        );

        Label initials = new Label(staff.getInitials());
        initials.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #C9952A;"
        );
        avatar.getChildren().add(initials);

        Label nameLabel = new Label(staff.getName());
        nameLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #F5E6C8;"
        );

        Label roleLabel = new Label(staff.getRole());
        roleLabel.setPadding(new Insets(4, 12, 4, 12));
        roleLabel.setStyle(
            "-fx-background-color: " + roleColor(staff.getRole()) + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;"
        );

        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);
        statusRow.setMaxWidth(Double.MAX_VALUE);

        Label shiftLabel = new Label("Shift: " + staff.getShift());
        shiftLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #7A5C2E;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dutyLabel = new Label(staff.isOnDuty() ? "\u25CF On Duty" : "\u25CF Off Duty");
        dutyLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + (staff.isOnDuty() ? "#1E8449" : "#7A5C2E") + ";"
        );

        statusRow.getChildren().addAll(shiftLabel, spacer, dutyLabel);

        HBox actionRow = new HBox(8);
        actionRow.setAlignment(Pos.CENTER);

        Button editBtn = new Button("Edit");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle(smallButtonStyle("#2A1500", "#C9952A"));
        HBox.setHgrow(editBtn, Priority.ALWAYS);
        editBtn.setOnAction(e -> showEditStaffDialog(staff));

        Button removeBtn = new Button("Remove");
        removeBtn.setMaxWidth(Double.MAX_VALUE);
        removeBtn.setStyle(smallButtonStyle("#3B0000", "#E74C3C"));
        HBox.setHgrow(removeBtn, Priority.ALWAYS);
        removeBtn.setOnAction(e -> showRemoveConfirm(staff));

        actionRow.getChildren().addAll(editBtn, removeBtn);

        card.getChildren().addAll(avatar, nameLabel, roleLabel, statusRow, actionRow);
        return card;
    }

    private String roleColor(String role) {
        switch (role) {
            case "Admin":   return "#8E44AD";
            case "Cashier": return "#2E86C1";
            case "Barista": return "#28B463";
            default:        return "#7A5C2E";
        }
    }

    private String smallButtonStyle(String bg, String fg) {
        return "-fx-background-color: " + bg + ";" +
               "-fx-text-fill: " + fg + ";" +
               "-fx-font-size: 10px;" +
               "-fx-background-radius: 6px;" +
               "-fx-cursor: hand;" +
               "-fx-padding: 4 2 4 2;";
    }

    // ── ADD STAFF DIALOG ──
    private void showAddStaffDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Staff Member");
        dialog.setResizable(false);

        VBox layoutBox = new VBox(14);
        layoutBox.setPadding(new Insets(24));
        layoutBox.setStyle("-fx-background-color: #FFF8E7;");

        Label title = new Label("Add New Staff Member");
        title.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        TextField nameField = new TextField();
        nameField.setPromptText("Full name e.g. Amir Yusof");
        nameField.setStyle(fieldStyle());

        TextField usernameField = new TextField();
        usernameField.setPromptText("Login username e.g. cashier_amir");
        usernameField.setStyle(fieldStyle());

        TextField emailField = new TextField();
        emailField.setPromptText("Email e.g. amir@kaffe.com");
        emailField.setStyle(fieldStyle());

        TextField passwordField = new PasswordField();
        passwordField.setPromptText("Login password");
        passwordField.setStyle(fieldStyle());

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Cashier", "Barista");
        roleBox.setPromptText("Select role");
        roleBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> shiftBox = new ComboBox<>();
        shiftBox.getItems().addAll("Morning", "Evening");
        shiftBox.setPromptText("Select shift");
        shiftBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> dutyBox = new ComboBox<>();
        dutyBox.getItems().addAll("On Duty", "Off Duty");
        dutyBox.setValue("On Duty");
        dutyBox.setMaxWidth(Double.MAX_VALUE);

        Label error = new Label("");
        error.setStyle("-fx-text-fill: #C0392B; -fx-font-size: 11px;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setStyle(
            "-fx-background-color: #E0D0A0;" +
            "-fx-text-fill: #4A2C0A;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> dialog.close());

        Button saveBtn = new Button("Save");
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
                usernameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                roleBox.getValue() == null ||
                shiftBox.getValue() == null) {
                error.setText("Please fill all fields.");
                return;
            }

            if (StaffMember.isUsernameTaken(usernameField.getText().trim())) {
                error.setText("That username is already taken.");
                return;
            }

            StaffMember newStaff = new StaffMember(
                nameField.getText(),
                roleBox.getValue(),
                shiftBox.getValue(),
                dutyBox.getValue().equals("On Duty"),
                usernameField.getText().trim(),
                passwordField.getText(),
                emailField.getText().trim()
            );
                        StaffMember.addStaff(newStaff);
            loadStaffCards();
            dialog.close();
        });

        HBox btnRow = new HBox(10, cancelBtn, saveBtn);
        HBox.setHgrow(cancelBtn, Priority.ALWAYS);
        HBox.setHgrow(saveBtn,   Priority.ALWAYS);

        layoutBox.getChildren().addAll(
            title,
            new Label("Full Name:"),   nameField,
            new Label("Username:"),    usernameField,
            new Label("Email:"),       emailField,
            new Label("Password:"),    passwordField,
            new Label("Role:"),        roleBox,
            new Label("Shift:"),       shiftBox,
            new Label("Duty Status:"), dutyBox,
            error, btnRow
        );

        ScrollPane dialogScroll = new ScrollPane();
        dialogScroll.setContent(layoutBox);
        dialogScroll.setFitToWidth(true);
        dialogScroll.setStyle(
            "-fx-background: #FFF8E7;" +
            "-fx-background-color: #FFF8E7;" +
            "-fx-border-color: transparent;"
        );

        Scene dialogScene = new Scene(dialogScroll, 380, 560);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    // ── EDIT STAFF DIALOG ──
    private void showEditStaffDialog(StaffMember staff) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Staff Member");
        dialog.setResizable(false);

        VBox layoutBox = new VBox(14);
        layoutBox.setPadding(new Insets(24));
        layoutBox.setStyle("-fx-background-color: #FFF8E7;");

        Label title = new Label("Edit " + staff.getName());
        title.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );

        TextField nameField = new TextField();
        nameField.setText(staff.getName());
        nameField.setStyle(fieldStyle());

        TextField usernameField = new TextField();
        usernameField.setText(staff.getUsername());
        usernameField.setStyle(fieldStyle());

        TextField emailField = new TextField();
        emailField.setText(staff.getEmail());
        emailField.setStyle(fieldStyle());

        TextField passwordField = new PasswordField();
        passwordField.setText(staff.getPassword());
        passwordField.setStyle(fieldStyle());

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Cashier", "Barista");
        roleBox.setValue(staff.getRole());
        roleBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> shiftBox = new ComboBox<>();
        shiftBox.getItems().addAll("Morning", "Evening");
        shiftBox.setValue(staff.getShift());
        shiftBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> dutyBox = new ComboBox<>();
        dutyBox.getItems().addAll("On Duty", "Off Duty");
        dutyBox.setValue(staff.isOnDuty() ? "On Duty" : "Off Duty");
        dutyBox.setMaxWidth(Double.MAX_VALUE);

        Label error = new Label("");
        error.setStyle("-fx-text-fill: #C0392B; -fx-font-size: 11px;");

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
            if (nameField.getText().isEmpty() ||
                usernameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                roleBox.getValue() == null ||
                shiftBox.getValue() == null) {
                error.setText("Please fill all fields.");
                return;
            }

            String newUsername = usernameField.getText().trim();
            if (!newUsername.equals(staff.getUsername()) && StaffMember.isUsernameTaken(newUsername)) {
                error.setText("That username is already taken.");
                return;
            }

            staff.setName(nameField.getText());
            staff.setUsername(newUsername);
            staff.setEmail(emailField.getText().trim());
            staff.setPassword(passwordField.getText());
            staff.setRole(roleBox.getValue());
            staff.setShift(shiftBox.getValue());
            staff.setOnDuty(dutyBox.getValue().equals("On Duty"));

            loadStaffCards();
            dialog.close();
        });

        HBox btnRow = new HBox(10, cancelBtn, saveBtn);
        HBox.setHgrow(cancelBtn, Priority.ALWAYS);
        HBox.setHgrow(saveBtn,   Priority.ALWAYS);

        layoutBox.getChildren().addAll(
            title,
            new Label("Full Name:"),   nameField,
            new Label("Username:"),    usernameField,
            new Label("Email:"),       emailField,
            new Label("Password:"),    passwordField,
            new Label("Role:"),        roleBox,
            new Label("Shift:"),       shiftBox,
            new Label("Duty Status:"), dutyBox,
            error, btnRow
        );

        ScrollPane dialogScroll = new ScrollPane();
        dialogScroll.setContent(layoutBox);
        dialogScroll.setFitToWidth(true);
        dialogScroll.setStyle(
            "-fx-background: #FFF8E7;" +
            "-fx-background-color: #FFF8E7;" +
            "-fx-border-color: transparent;"
        );

        Scene dialogScene = new Scene(dialogScroll, 380, 560);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    // ── REMOVE CONFIRM ──
    private void showRemoveConfirm(StaffMember staff) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Staff Member");
        alert.setHeaderText("Remove " + staff.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                                StaffMember.removeStaff(staff);
                loadStaffCards();
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