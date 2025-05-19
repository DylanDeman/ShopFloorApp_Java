package gui.notification;

import dto.NotificationDTO;
import gui.MainLayout;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NotificationDetailComponent extends VBox {

    public NotificationDetailComponent(MainLayout layout, NotificationDTO notification) {
        this.setPadding(new Insets(20));
        this.setSpacing(10);

        Label title = new Label("Notificatiedetails");
        title.getStyleClass().add("title");

        Label message = new Label("Bericht: " + notification.message());
        Label time = new Label("Tijdstip: " + notification.time().toString());
        Label status = new Label("Status: " + (notification.isRead() ? "Gelezen" : "Ongelezen"));

        this.getChildren().addAll(title, message, time, status);

        // Optionally mark it as read here using layout.getServices() or directly call the controller.
    }
}
