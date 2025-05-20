package gui;

import java.time.format.DateTimeFormatter;
import java.util.List;

import domain.NotificationController;
import dto.NotificationDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class NotificationListComponent extends VBox {

    private ListView<NotificationDTO> notificationList;
    private NotificationController nc;
    private final MainLayout mainLayout;

    private ObservableList<NotificationDTO> unreadNotifications;
    private ObservableList<NotificationDTO> readNotifications;

    public NotificationListComponent(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
        this.nc = mainLayout.getServices().getNotificationController();
        this.notificationList = new ListView<>();

        initializeGui();
    }

    private void initializeGui() {
        // Set base component styling
        this.setSpacing(20);
        this.setPadding(new Insets(15));
        this.setStyle("-fx-background-color: #f8f9fa;");
        
        // Component Title
        Label titleLabel = new Label("Notificaties");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Unread Notifications
        Label unreadLabel = new Label("📩 Ongelezen notificaties");
        unreadLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ListView<NotificationDTO> unreadListView = new ListView<>();
        unreadNotifications = FXCollections.observableArrayList(nc.getAllUnread());
        unreadListView.setItems(unreadNotifications);
        unreadListView.setMaxHeight(300);
        
        // Ensure proper spacing between list items
        unreadListView.setCellFactory(lv -> {
            ListCell<NotificationDTO> cell = new ListCell<NotificationDTO>() {
                private final Button markAsReadButton = new Button("✔️ Markeer als gelezen");
                private final VBox layout = new VBox();
                private final Label messageLabel = new Label();
                private final Label timeLabel = new Label();
                
                {
                    // Style the button
                    markAsReadButton.setStyle(
                        "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" + 
                        "-fx-background-radius: 5px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5px 10px;"
                    );
                    
                    // Set button action
                    markAsReadButton.setOnAction(e -> {
                        NotificationDTO item = getItem();
                        if (item != null) {
                            nc.markAsRead(item.id());
                            unreadNotifications.remove(item);
                            readNotifications.add(item);
                        }
                    });
                    
                    // Style the time label
                    timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
                    
                    // Style message label
                    messageLabel.setWrapText(true);
                    messageLabel.setStyle("-fx-font-size: 14px;");
                    
                    // Style the cell container
                    layout.setSpacing(10);
                    layout.setPadding(new Insets(15));
                    layout.getChildren().addAll(timeLabel, messageLabel, markAsReadButton);
                    
                    // Add nice box styling with proper spacing
                    layout.setStyle(
                        "-fx-background-color: #f0f8ff;" +
                        "-fx-border-color: #b3d7ff;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                    );
                    
                    // Set margins to ensure spacing between cells
                    VBox.setMargin(layout, new Insets(5, 0, 5, 0));
                }
                
                @Override
                protected void updateItem(NotificationDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                        String formattedTime = item.time().format(formatter);
                        
                        timeLabel.setText("🕒 " + formattedTime);
                        messageLabel.setText(item.message());
                        
                        // Important: setPadding on the ListCell for spacing between items
                        setPadding(new Insets(5, 0, 5, 0));
                        setGraphic(layout);
                    }
                }
            };
            return cell;
        });
        
        // Apply styles to the list view
        unreadListView.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-insets: 0;" +
            "-fx-padding: 5;"
        );

        // Container for unread notifications section
        VBox unreadBox = new VBox(10, unreadLabel, unreadListView);
        unreadBox.setPadding(new Insets(15));
        unreadBox.setStyle(
            "-fx-background-color: #e8f4f8;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #d0e8f2;" +
            "-fx-border-radius: 8px;"
        );

        // Read Notifications
        Label readLabel = new Label("✅ Gelezen notificaties");
        readLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ListView<NotificationDTO> readListView = new ListView<>();
        readNotifications = FXCollections.observableArrayList(nc.getAllRead());
        readListView.setItems(readNotifications);
        readListView.setMaxHeight(300);
        
        // Ensure proper spacing between list items
        readListView.setCellFactory(lv -> {
            ListCell<NotificationDTO> cell = new ListCell<NotificationDTO>() {
                private final VBox layout = new VBox();
                private final Label messageLabel = new Label();
                private final Label timeLabel = new Label();
                
                {
                    // Style the time label
                    timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777;");
                    
                    // Style message label
                    messageLabel.setWrapText(true);
                    messageLabel.setStyle("-fx-font-size: 14px;");
                    
                    // Style the cell container
                    layout.setSpacing(8);
                    layout.setPadding(new Insets(12));
                    layout.getChildren().addAll(timeLabel, messageLabel);
                    
                    // Add nice box styling with proper spacing
                    layout.setStyle(
                        "-fx-background-color: #f5f5f5;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
                    );
                    
                    // Set margins to ensure spacing between cells
                    VBox.setMargin(layout, new Insets(4, 0, 4, 0));
                }
                
                @Override
                protected void updateItem(NotificationDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                        String formattedTime = item.time().format(formatter);
                        
                        timeLabel.setText("🕒 " + formattedTime);
                        messageLabel.setText(item.message());
                        
                        // Important: setPadding on the ListCell for spacing between items
                        setPadding(new Insets(4, 0, 4, 0));
                        setGraphic(layout);
                    }
                }
            };
            return cell;
        });
        
        // Apply styles to the list view
        readListView.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-insets: 0;" +
            "-fx-padding: 5;"
        );
        
        // No notifications placeholders
        Label noUnreadPlaceholder = new Label("Geen ongelezen notificaties");
        noUnreadPlaceholder.setStyle("-fx-text-fill: #777; -fx-font-style: italic;");
        unreadListView.setPlaceholder(noUnreadPlaceholder);
        
        Label noReadPlaceholder = new Label("Geen gelezen notificaties");
        noReadPlaceholder.setStyle("-fx-text-fill: #777; -fx-font-style: italic;");
        readListView.setPlaceholder(noReadPlaceholder);

        // Container for read notifications section
        VBox readBox = new VBox(10, readLabel, readListView);
        readBox.setPadding(new Insets(15));
        readBox.setStyle(
            "-fx-background-color: #f9f9f9;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #e6e6e6;" +
            "-fx-border-radius: 8px;"
        );

        // Add a separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #dddddd;");
        
        // Add everything to the main component with proper spacing
        this.getChildren().addAll(titleLabel, unreadBox, separator, readBox);
        VBox.setMargin(titleLabel, new Insets(0, 0, 10, 0));
        VBox.setMargin(unreadBox, new Insets(0, 0, 15, 0));
        VBox.setMargin(separator, new Insets(5, 0, 15, 0));
    }
}