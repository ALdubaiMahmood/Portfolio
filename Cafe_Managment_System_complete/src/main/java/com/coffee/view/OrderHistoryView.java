package com.coffee.view;

import com.coffee.MainLayout;
import com.coffee.model.OrderRecord;
import com.coffee.util.SessionManager;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Order History content — no sidebar here, {@link MainLayout} already owns
 * one shared sidebar. Lists every {@link OrderRecord}: seeded historical
 * ones plus any placed live from {@link OrderView}.
 */
public class OrderHistoryView {

    private final MainLayout layout;

    private TableView<OrderRecord> table;
    private TextField dateFilter;
    private ComboBox<String> statusFilter;
    private Label summaryLabel;

    public OrderHistoryView(MainLayout layout) {
        this.layout = layout;
    }

    public VBox getView() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #FFF8E7;");

        HBox topBar = buildTopBar();
        HBox filterRow = buildFilterRow();

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setMargin(table, new Insets(0, 24, 12, 24));

        HBox summaryBar = buildSummaryBar();
        VBox.setMargin(summaryBar, new Insets(0, 24, 24, 24));

        refreshTable();

        content.getChildren().addAll(topBar, filterRow, table, summaryBar);
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

        Label title = new Label("Order History");
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

    // ── FILTER ROW ──
    private HBox buildFilterRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(16, 24, 12, 24));
        row.setStyle("-fx-background-color: #FFF8E7;");

        Label dateLabel = new Label("Date:");
        dateLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #4A2C0A;"
        );

        dateFilter = new TextField();
        dateFilter.setPromptText("e.g. 30 Jun");
        dateFilter.setPrefWidth(150);
        dateFilter.setStyle(fieldStyle());

        Label statusLabel = new Label("Status:");
        statusLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #4A2C0A;"
        );

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Completed", "In Progress", "Cancelled");
        statusFilter.setValue("All");
        statusFilter.setStyle(fieldStyle());
        // Plain -fx-text-fill on a ComboBox often doesn't reach the displayed
        // value's label — setting the button cell + cell factory directly is
        // the reliable way to control that text's color.
        statusFilter.setButtonCell(brightListCell());
        statusFilter.setCellFactory(lv -> brightListCell());

        Button searchBtn = new Button("Search");
        searchBtn.setStyle(
            "-fx-background-color: #C9952A;" +
            "-fx-text-fill: #1A0F00;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;" +
            "-fx-background-radius: 6px;" +
            "-fx-padding: 8px 20px;" +
            "-fx-cursor: hand;"
        );
        searchBtn.setOnAction(e -> refreshTable());

        row.getChildren().addAll(dateLabel, dateFilter, statusLabel, statusFilter, searchBtn);
        return row;
    }

    // ── TABLE ──
    private TableView<OrderRecord> buildTable() {
        TableView<OrderRecord> tv = new TableView<>();
        tv.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-control-inner-background: #1a0f00;" +
            "-fx-table-cell-border-color: transparent;"
        );
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("No orders match this filter."));

        TableColumn<OrderRecord, String> colNumber = new TableColumn<>("Order #");
        colNumber.setCellValueFactory(d ->
            new SimpleStringProperty("#" + d.getValue().getOrderNumber()));

        TableColumn<OrderRecord, String> colDateTime = new TableColumn<>("Date/Time");
        colDateTime.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getDateTime()));

        TableColumn<OrderRecord, String> colCashier = new TableColumn<>("Cashier");
        colCashier.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getCashier()));

        TableColumn<OrderRecord, String> colItems = new TableColumn<>("Items");
        colItems.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getItemsSummary()));

        TableColumn<OrderRecord, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(d ->
            new SimpleStringProperty(String.format("RM %.2f", d.getValue().getTotal())));

        TableColumn<OrderRecord, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<OrderRecord, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setStyle("-fx-text-fill: " + statusColor(status) + "; -fx-font-weight: bold;");
                }
            }
        });

        tv.getColumns().addAll(colNumber, colDateTime, colCashier, colItems, colTotal, colStatus);
        return tv;
    }

    private String statusColor(String status) {
        switch (status) {
            case "Completed":   return "#03d45a";
            case "In Progress": return "#e09405";
            case "Cancelled":   return "#db1600";
            default:            return "#F5E6C8";
        }
    }

    // ── FILTERING + SUMMARY ──
    private void refreshTable() {
        List<OrderRecord> all = OrderRecord.getAllOrders();
        String status = statusFilter.getValue();
        String dateQuery = dateFilter.getText().trim().toLowerCase();

        table.getItems().clear();
        for (OrderRecord order : all) {
            if (!status.equals("All") && !order.getStatus().equals(status)) continue;
            if (!dateQuery.isEmpty() && !order.getDateTime().toLowerCase().contains(dateQuery)) continue;
            table.getItems().add(order);
        }

        refreshSummaryBar(all);
    }

    private HBox buildSummaryBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(12));
        bar.setStyle(
            "-fx-background-color: #1F1200;" +
            "-fx-background-radius: 8px;"
        );

        summaryLabel = new Label();
        summaryLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #F5E6C8;"
        );

        bar.getChildren().add(summaryLabel);
        return bar;
    }

    /** Counts are over ALL orders (not just the filtered/visible rows) — matches a daily-summary bar. */
    private void refreshSummaryBar(List<OrderRecord> all) {
        int total = all.size();
        int completed = 0;
        int cancelled = 0;
        int inProgress = 0;
        double revenue = 0;

        for (OrderRecord o : all) {
            switch (o.getStatus()) {
                case "Completed":
                    completed++;
                    revenue += o.getTotal();
                    break;
                case "In Progress":
                    inProgress++;
                    revenue += o.getTotal();
                    break;
                case "Cancelled":
                    cancelled++;
                    break;
            }
        }

        summaryLabel.setText(String.format(
            "Total Orders: %d   |   Completed: %d   |   Cancelled: %d   |   In Progress: %d   |   Daily Revenue: RM %.2f",
            total, completed, cancelled, inProgress, revenue
        ));
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
               "-fx-padding: 6px 10px;";
    }
}