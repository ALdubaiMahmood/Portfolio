package com.coffee.controller;

import com.coffee.MainLayout;
import com.coffee.model.StaffMember;
import com.coffee.util.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginController {

    private Stage stage;
    private String selectedRole = "Admin";
    private Button btnAdmin, btnCashier, btnBarista;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;

    public Scene buildScene(Stage stage) {
        this.stage = stage;

        HBox mainLayout = new HBox();
        mainLayout.setMinSize(900, 580);
        mainLayout.setMaxSize(900, 580);

        VBox leftPanel  = buildLeftPanel();
        VBox rightPanel = buildRightPanel();
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        mainLayout.getChildren().addAll(leftPanel, rightPanel);

        return new Scene(mainLayout, 900, 580);
    }

    // ── LEFT PANEL ──
    private VBox buildLeftPanel() {
        VBox panel = new VBox(18);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(50, 30, 50, 30));
        panel.setPrefWidth(320);
        panel.setMaxWidth(320);
        panel.setStyle("-fx-background-color: #2A1500;");

        // Logo
        ImageView logo;
        try {
            java.io.InputStream stream = getClass()
                .getResourceAsStream("/com/coffee/images/logo.png");
            if (stream != null) {
                logo = new ImageView(new Image(stream));
                logo.setFitWidth(120);
                logo.setFitHeight(120);
                logo.setPreserveRatio(true);
            } else {
                logo = new ImageView();
                System.out.println("Logo not found!");
            }
        } catch (Exception e) {
            logo = new ImageView();
            System.out.println("Logo error: " + e.getMessage());
        }

        Label brand = new Label("KAFFE");
        brand.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #C9952A;" +
            "-fx-letter-spacing: 4px;"
        );

        Label subtitle = new Label("MANAGEMENT SYSTEM");
        subtitle.setStyle(
            "-fx-font-size: 9px;" +
            "-fx-text-fill: #7A5C2E;" +
            "-fx-letter-spacing: 2px;"
        );

        Region divider = new Region();
        divider.setPrefSize(50, 1);
        divider.setMaxWidth(50);
        divider.setStyle("-fx-background-color: #4A2E00;");

        Label tagline = new Label("Serving great coffee\nstarts with great management.");
        tagline.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #5A3E1A;" +
            "-fx-font-style: italic;"
        );
        tagline.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        tagline.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(logo, brand, subtitle, divider, tagline);
        return panel;
    }

    // ── RIGHT PANEL ──
    private VBox buildRightPanel() {
        VBox panel = new VBox(0);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPadding(new Insets(50, 60, 50, 60));
        panel.setStyle("-fx-background-color: #FFF8E7;");

        Label welcome = new Label("Welcome Back");
        welcome.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4A2C0A;"
        );
        VBox.setMargin(welcome, new Insets(0, 0, 4, 0));

        Label signInSub = new Label("Sign in to continue");
        signInSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A5C2E;");
        VBox.setMargin(signInSub, new Insets(0, 0, 28, 0));

        // USERNAME
        Label userLabel = new Label("USERNAME");
        userLabel.setStyle(fieldLabelStyle());
        VBox.setMargin(userLabel, new Insets(0, 0, 6, 0));

        usernameField = new TextField();
        usernameField.setPromptText("e.g. admin_mahmoud");
        usernameField.setMaxWidth(Double.MAX_VALUE);
        usernameField.setStyle(inputStyle());
        usernameField.setOnAction(e -> passwordField.requestFocus());
        VBox.setMargin(usernameField, new Insets(0, 0, 16, 0));

        // PASSWORD
        Label passLabel = new Label("PASSWORD");
        passLabel.setStyle(fieldLabelStyle());
        VBox.setMargin(passLabel, new Insets(0, 0, 6, 0));

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle(inputStyle());
        passwordField.setOnAction(e -> handleLogin());
        VBox.setMargin(passwordField, new Insets(0, 0, 16, 0));

        // ROLE
        Label roleLabel = new Label("ROLE");
        roleLabel.setStyle(fieldLabelStyle());
        VBox.setMargin(roleLabel, new Insets(0, 0, 6, 0));

        btnAdmin   = new Button("Admin");
        btnCashier = new Button("Cashier");
        btnBarista = new Button("Barista");

        btnAdmin.setMaxWidth(Double.MAX_VALUE);
        btnCashier.setMaxWidth(Double.MAX_VALUE);
        btnBarista.setMaxWidth(Double.MAX_VALUE);
        btnAdmin.setPrefHeight(40);
        btnCashier.setPrefHeight(40);
        btnBarista.setPrefHeight(40);

        HBox roleBox = new HBox(10, btnAdmin, btnCashier, btnBarista);
        HBox.setHgrow(btnAdmin,   Priority.ALWAYS);
        HBox.setHgrow(btnCashier, Priority.ALWAYS);
        HBox.setHgrow(btnBarista, Priority.ALWAYS);
        VBox.setMargin(roleBox, new Insets(0, 0, 20, 0));

        setActiveRole("Admin");
        btnAdmin.setOnAction(e   -> setActiveRole("Admin"));
        btnCashier.setOnAction(e -> setActiveRole("Cashier"));
        btnBarista.setOnAction(e -> setActiveRole("Barista"));

        // ERROR
        errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #C0392B; -fx-font-size: 11px;");
        VBox.setMargin(errorLabel, new Insets(0, 0, 6, 0));

        // SIGN IN BUTTON
        Button signInBtn = new Button("Sign In");
        signInBtn.setMaxWidth(Double.MAX_VALUE);
        signInBtn.setPrefHeight(46);
        signInBtn.setStyle(signInBtnStyle("#C9952A"));
        signInBtn.setOnAction(e -> handleLogin());
        signInBtn.setOnMouseEntered(e -> signInBtn.setStyle(signInBtnStyle("#E5A830")));
        signInBtn.setOnMouseExited(e  -> signInBtn.setStyle(signInBtnStyle("#C9952A")));
        VBox.setMargin(signInBtn, new Insets(0, 0, 14, 0));

        // FORGOT PASSWORD
        HBox forgotBox = new HBox(4);
        forgotBox.setAlignment(Pos.CENTER);
        Label forgotLabel = new Label("Forgot password?");
        forgotLabel.setStyle("-fx-text-fill: #7A5C2E; -fx-font-size: 11px;");
        Label forgotLink = new Label("Contact admin");
        forgotLink.setStyle(
            "-fx-text-fill: #C9952A;" +
            "-fx-font-size: 11px;" +
            "-fx-underline: true;" +
            "-fx-cursor: hand;"
        );
        forgotBox.getChildren().addAll(forgotLabel, forgotLink);

        panel.getChildren().addAll(
            welcome, signInSub,
            userLabel, usernameField,
            passLabel, passwordField,
            roleLabel, roleBox,
            errorLabel,
            signInBtn,
            forgotBox
        );

        return panel;
    }

    // ── LOGIN LOGIC ──
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty()) {
            showError("Please enter your username.");
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password.");
            return;
        }

        StaffMember matched = StaffMember.findByCredentials(username, password, selectedRole);

        if (matched != null) {
            showError("");
            SessionManager.login(matched);
            System.out.println("Login success! " + username + " | " + selectedRole);
            // Hand off to the single-Scene MainLayout (sidebar + dashboard).
            MainLayout mainLayout = new MainLayout(stage);
            stage.setScene(mainLayout.buildScene());
            stage.centerOnScreen();
        } else {
            showError("Invalid username, password, or role.");
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }

    // ── ROLE STYLES ──
    private void setActiveRole(String role) {
        selectedRole = role;
        btnAdmin.setStyle(roleBtnStyle(role.equals("Admin")));
        btnCashier.setStyle(roleBtnStyle(role.equals("Cashier")));
        btnBarista.setStyle(roleBtnStyle(role.equals("Barista")));
    }

    private String roleBtnStyle(boolean active) {
        if (active) {
            return "-fx-background-color: #C9952A;" +
                   "-fx-text-fill: #1A0F00;" +
                   "-fx-font-weight: bold;" +
                   "-fx-font-size: 12px;" +
                   "-fx-background-radius: 8px;" +
                   "-fx-cursor: hand;";
        } else {
            return "-fx-background-color: #F0E0B0;" +
                   "-fx-text-fill: #7A5C2E;" +
                   "-fx-font-size: 12px;" +
                   "-fx-background-radius: 8px;" +
                   "-fx-border-color: #C9952A;" +
                   "-fx-border-radius: 8px;" +
                   "-fx-cursor: hand;";
        }
    }

    private String signInBtnStyle(String color) {
        return "-fx-background-color: " + color + ";" +
               "-fx-text-fill: #1A0F00;" +
               "-fx-font-size: 14px;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;";
    }

    private String fieldLabelStyle() {
        return "-fx-font-size: 10px;" +
               "-fx-text-fill: #7A5C2E;" +
               "-fx-letter-spacing: 1px;";
    }

    private String inputStyle() {
        return "-fx-background-color: #1F1200;" +
               "-fx-border-color: #3A2200;" +
               "-fx-border-radius: 8px;" +
               "-fx-background-radius: 8px;" +
               "-fx-text-fill: #FFD700;" +
               "-fx-prompt-text-fill: #ffbe6a;" +
               "-fx-font-size: 13px;" +
               "-fx-padding: 10px 14px;";
    }
}