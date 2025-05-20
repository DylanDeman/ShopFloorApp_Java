package gui;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class CustomButton extends Button {
    private final FontIcon icon;
    private final String text;
    private final Pos pos;
    
    public CustomButton(FontIcon icon, String text) {
        this.icon = icon;
        this.text = text;
        this.pos = Pos.CENTER_LEFT;
    }

    public CustomButton(FontIcon icon, String text, Pos pos) {
        this.icon = icon;
        this.text = text;
        this.pos = pos;
        buildGui();
    }
    
    public CustomButton(String text, Pos pos) {
        this(null, text, pos);
    }

    public CustomButton(String text) {
        this(null, text, Pos.CENTER_LEFT);
    }

    private void buildGui() {
        Text label = new Text(text);
        label.setFill(Color.WHITE);
        label.setStyle("-fx-font: 12 arial;");
        label.setTextAlignment(TextAlignment.CENTER);

        HBox hBox = new HBox(8);
        hBox.setAlignment(pos);

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
