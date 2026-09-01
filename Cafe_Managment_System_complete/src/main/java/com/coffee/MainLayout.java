package com.coffee;

import com.coffee.controller.LoginController;
import com.coffee.util.SessionManager;
import com.coffee.view.DashboardView;
import com.coffee.view.InventoryView;
import com.coffee.view.MenuView;
import com.coffee.view.OrderHistoryView;
import com.coffee.view.OrderView;
import com.coffee.view.SalesReportView;
import com.coffee.view.StaffView;


import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The application's single Scene after login.
 *
 * Login -> MainLayout (ONE Scene)
 *          [ Sidebar | contentArea ]
 *
 * Navigating between sections swaps the contents of {@code contentArea}
 * only. The Sidebar and the Stage/Scene are never rebuilt, so there's no
 * flicker, no resizing bug, and no scroll-position reset.
 */
public class MainLayout {

    private final Stage stage;
    private final Sidebar sidebar;
    private final StackPane contentArea;
    private final HBox root;

    public MainLayout(Stage stage) {
        this.stage = stage;

        this.contentArea = new StackPane();
        this.contentArea.setStyle("-fx-background-color: #FFF8E7;");

        this.sidebar = new Sidebar(this::navigate);

        this.root = new HBox();
        this.root.setStyle("-fx-background-color: #FFF8E7;");
        HBox.setHgrow(contentArea, Priority.ALWAYS);
        this.root.getChildren().addAll(sidebar.getView(), contentArea);
    }

    /** Builds the one and only Scene for the whole application. */
    public Scene buildScene() {
        stage.setResizable(true);
        Scene scene = new Scene(root, 1100, 680);
        navigate("Dashboard");
        return scene;
    }

    public Stage getStage() {
        return stage;
    }

    /** Swaps the center panel and updates which sidebar item is highlighted. */
    public void navigate(String destination) {
        if (destination.equals("Logout")) {
            logout();
            return;
        }

        // Real access control — not just hiding the sidebar button. This also
        // catches Dashboard's "Staff List" / "Sales Report" quick-action
        // buttons, which would otherwise bypass the sidebar entirely.
        if ((destination.equals("Staff") || destination.equals("Sales Report"))
                && !SessionManager.isAdmin()) {
            sidebar.setActive("Dashboard");
            contentArea.getChildren().setAll(buildAccessDenied(destination));
            return;
        }

        sidebar.setActive(destination);

        switch (destination) {
            case "Dashboard":
                contentArea.getChildren().setAll(new DashboardView(this).getView());
                break;
            case "Menu":
                contentArea.getChildren().setAll(new MenuView(this).getView());
                break;
            case "Inventory":
                contentArea.getChildren().setAll(new InventoryView(this).getView());
                break;
                case "Orders":
                contentArea.getChildren().setAll(new OrderView(this).getView());
                break;
            case "Order History":
                contentArea.getChildren().setAll(new OrderHistoryView(this).getView());
                break;
            case "Staff":
                contentArea.getChildren().setAll(new StaffView(this).getView());
                break;
            case "Sales Report":
                contentArea.getChildren().setAll(new SalesReportView(this).getView());
                break;
            default:
                // Nothing left unbuilt — this is just a safety net.
                contentArea.getChildren().setAll(buildComingSoon(destination));
                break;
        }
    }

    private Node buildAccessDenied(String sectionName) {
        Label label = new Label("You don't have access to " + sectionName + " \u2014 Admin only.");
        label.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #C0392B;"
        );
        StackPane pane = new StackPane(label);
        pane.setAlignment(Pos.CENTER);
        pane.setStyle("-fx-background-color: #FFF8E7;");
        return pane;
    }

    private Node buildComingSoon(String sectionName) {
        Label label = new Label(sectionName + " — coming soon");
        label.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #7A5C2E;"
        );
        StackPane pane = new StackPane(label);
        pane.setAlignment(Pos.CENTER);
        pane.setStyle("-fx-background-color: #FFF8E7;");
        return pane;
    }
    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout Confirmation");
        confirm.setHeaderText("logout of KAFFE?");
        confirm.setContentText("You will need to Sign in again.");
        confirm.showAndWait().ifPresent(result ->{
            if (result == ButtonType.OK) {
                SessionManager.logout();
                LoginController  login = new LoginController();
                stage.setScene(login.buildScene(stage));
                stage.setResizable(false); 
                stage.centerOnScreen();
                
            }
            
        });

    }
}
