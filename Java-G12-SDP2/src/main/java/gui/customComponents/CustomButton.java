package gui.customComponents;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CustomButton extends Button {
    private final FontIcon icon;
    private final String text;

    public CustomButton(FontIcon icon, String text) {
        this.icon = icon;
        this.text = text;
        buildGui();
    }

    public CustomButton(String text) {
        this(null, text);
    }

    private void buildGui() {
        Text label = new Text(text);
        label.setFill(Color.WHITE);
        label.setStyle("-fx-font: 12 arial;");

        HBox hBox = new HBox(8);
        hBox.setAlignment(Pos.CENTER_LEFT);

        if (icon != null) {
            icon.setIconSize(16);
            icon.setIconColor(Color.WHITE);
            hBox.getChildren().addAll(icon, label);
        } else {
            hBox.getChildren().add(label);
        }

        this.setGraphic(hBox); // -> Display the hBox inside this button!


        // TODO later in stylesheet zetten om overzicht van klasse te bewaren!
        this.setStyle(
            "-fx-background-color: #e53935;" +
            "-fx-background-radius: 3;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 6 12;"
        );

        // For having hover:cursor-pointer
        this.setCursor(javafx.scene.Cursor.HAND);
    }
}
