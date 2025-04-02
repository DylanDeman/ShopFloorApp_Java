package gui.login;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginPane extends VBox {
    private TextField emailField;
    private PasswordField passwordField;

    public LoginPane() {
        this.setAlignment(Pos.CENTER);
        this.setMinWidth(800);
        this.setPrefHeight(600); 
        this.getStyleClass().add("login-pane"); 
        buildGui();
    }

    private void buildGui() {
        HBox hbox = new HBox(20);
        hbox.setPadding(new Insets(20));
        hbox.setAlignment(Pos.CENTER);
        hbox.getStyleClass().add("login-container"); 

        VBox loginForm = new VBox(10);
        loginForm.setAlignment(Pos.CENTER_LEFT);
        loginForm.getStyleClass().add("login-form"); 
        
        Label emailLabel = new Label("E-mail:");
        emailLabel.getStyleClass().add("login-label");
        emailField = new TextField();
        emailField.setPromptText("Example@delaware.com");
        emailField.getStyleClass().add("login-field");
        
        Label passwordLabel = new Label("Wachtwoord:");
        passwordLabel.getStyleClass().add("login-label");
        passwordField = new PasswordField();
        passwordField.setPromptText("********");
        passwordField.getStyleClass().add("login-field");
        
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setOnAction(e -> handleLogin());
        
        loginForm.getChildren().addAll(emailLabel, emailField, passwordLabel, passwordField, loginButton);
        
        ImageView delawareImage = new ImageView(new Image("/images/login_groepsfoto.jpg"));
        delawareImage.setFitWidth(200);
        delawareImage.setPreserveRatio(true);
        delawareImage.getStyleClass().add("login-image");

        hbox.getChildren().addAll(loginForm, delawareImage);
        
        this.getChildren().add(hbox);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        System.out.println("Logging in with: " + email + " / " + password);
    }
}
