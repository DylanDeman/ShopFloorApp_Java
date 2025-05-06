package gui.notification;

import java.util.List;

import domain.notifications.NotificationController;
import domain.notifications.NotificationDTO;
import gui.MainLayout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

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
        Label unreadLabel = new Label("📩 Ongelezen notificaties");
        unreadLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<NotificationDTO> unreadListView = new ListView<>();
        unreadNotifications = FXCollections.observableArrayList(nc.getAllUnread());

        unreadListView.setItems(unreadNotifications);
        unreadListView.setCellFactory(lv -> new ListCell<>() {
            private final Button markAsReadButton = new Button("✔️ Mark as read");
            private final VBox cellBox = new VBox();
            private final Label messageLabel = new Label();

            {
                markAsReadButton.setOnAction(e -> {
                    NotificationDTO item = getItem();
                    if (item != null) {
                        nc.markAsRead(item.id()); // backend update
                        unreadNotifications.remove(item); // update observable list
                        readNotifications.add(item); // move to read list
                    }
                });
                cellBox.getChildren().addAll(messageLabel, markAsReadButton);
                cellBox.setSpacing(5);
                cellBox.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(NotificationDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    messageLabel.setText(item.time() + " - " + item.message());
                    setGraphic(cellBox);
                }
            }
        });

        VBox unreadBox = new VBox(5, unreadLabel, unreadListView);
        unreadBox.setPadding(new Insets(10));

        // Now using the class-level readNotifications variable
        Label readLabel = new Label("Gelezen notificaties");
        readLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        ListView<NotificationDTO> readListView = new ListView<>();

        readNotifications = FXCollections.observableArrayList(nc.getAllRead());
        readListView.setItems(readNotifications);

        readListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NotificationDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.time() + " - " + item.message());
                }
            }
        });

        VBox readBox = new VBox(5, readLabel, readListView);
        readBox.setPadding(new Insets(10));

        this.getChildren().addAll(unreadBox, readBox);
    }
}
