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
		this.getStyleClass().add("login-pane");
		buildGui();
	}

	private void buildGui() {
		HBox hbox = new HBox(40);
		hbox.setPadding(new Insets(20));
		hbox.setAlignment(Pos.CENTER);
		hbox.getStyleClass().add("login-container");

		// Left side: Login form
		VBox loginForm = new VBox(15);
		loginForm.getStyleClass().add("login-form");
		loginForm.setAlignment(Pos.CENTER_LEFT);

		Label welcomeLabel = new Label("Welkom!");
		welcomeLabel.getStyleClass().add("welcome-label");

		VBox loginInformationVbox = new VBox(5);
		Label loginInformationTitle = new Label("Nog geen acount?");
		loginInformationTitle.setStyle("-fx-font-weight: bold;");
		Label loginInformationDescription = new Label("Contacteer je verantwoordelijke, manager of een administrator!");

		loginInformationVbox.getChildren().addAll(loginInformationTitle, loginInformationDescription);

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

		Button loginButton = new Button("AANMELDEN");
		loginButton.getStyleClass().add("login-button");
		loginButton.setOnAction(e -> handleLogin());

		loginForm.getChildren().addAll(welcomeLabel, loginInformationVbox, emailLabel, emailField, passwordLabel,
				passwordField, loginButton);

		// Right side: Image
		ImageView delawareImage = new ImageView(new Image("/images/login_groepsfoto.jpg"));
		delawareImage.setFitWidth(800);
		delawareImage.setFitHeight(500);
		delawareImage.getStyleClass().add("login-image");

		hbox.getChildren().addAll(loginForm, delawareImage);
		this.getChildren().addAll(hbox);
	}

	private void loginForm() {

	}

	private void handleLogin() {
		String email = emailField.getText();
		String password = passwordField.getText();

		System.out.println("Logging in with: " + email + " / " + password);
	}
}
