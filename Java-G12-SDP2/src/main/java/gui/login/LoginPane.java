package gui.login;

import domain.user.UserController;
import exceptions.InvalidInputException;
import gui.ChoicePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

// TODO 
public class LoginPane extends VBox {

	private static final String CSS_PATH = "/css/login.css";
	private static final String LOGO_PATH = "/images/delaware_logo.png";

	private final UserController userController = new UserController();
	private final Stage stage;

	private final Label errorLabel = new Label();
	private final TextField emailField = new TextField();
	private final PasswordField passwordField = new PasswordField();

	private final Label emailValidationLabel = new Label();
	private final Label passwordValidationLabel = new Label();

	public LoginPane(Stage stage) {
		this.stage = stage;
		this.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
		this.getStyleClass().add("login-pane");
		setupLayout();
	}

	private void setupLayout() {
		this.stage.setMaximized(true);
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(40));

		HBox logoContainer = createLogoContainer();
		HBox mainLayout = createMainLayout();

		this.getChildren().addAll(logoContainer, mainLayout);
	}

	private HBox createLogoContainer() {
		Image logoImage = new Image(getClass().getResourceAsStream(LOGO_PATH));
		ImageView logoView = new ImageView(logoImage);
		logoView.setFitHeight(120);
		logoView.setPreserveRatio(true);

		HBox logoContainer = new HBox();
		logoContainer.setAlignment(Pos.CENTER);
		logoContainer.setPadding(new Insets(0, 0, 20, 0));
		logoContainer.getChildren().add(logoView);

		return logoContainer;
	}

	private HBox createMainLayout() {
		HBox mainLayout = new HBox(40);
		mainLayout.setAlignment(Pos.CENTER);
		mainLayout.setPadding(new Insets(20));

		VBox loginForm = createLoginForm();
		StackPane loginImage = createLoginImage(loginForm);

		mainLayout.getChildren().addAll(loginForm, loginImage);
		return mainLayout;
	}

	private VBox createLoginForm() {
		VBox form = new VBox(15);
		form.setAlignment(Pos.CENTER_LEFT);
		form.getStyleClass().add("login-form");
		form.setFillWidth(true);

		// Welcome header
		Label welcomeLabel = new Label("Welkom!");
		welcomeLabel.getStyleClass().add("welcome-label");
		welcomeLabel.setMaxWidth(Double.MAX_VALUE);
		welcomeLabel.setAlignment(Pos.CENTER);

		// infoBox
		VBox infoBox = createInfoBox();

		// Error label
		errorLabel.getStyleClass().add("error-label");

		// Email input
		Label emailLabel = new Label("E-mail:");
		emailLabel.getStyleClass().add("login-label");
		emailField.setPromptText("Example@delaware.com");
		emailField.getStyleClass().add("login-field");

		// Setup validation labels
		emailValidationLabel.getStyleClass().add("validation-label");
		emailValidationLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
		emailValidationLabel.setVisible(false);
		emailValidationLabel.setText("verplicht");

		// Password input
		Label passwordLabel = new Label("Wachtwoord:");
		passwordLabel.getStyleClass().add("login-label");
		passwordField.setPromptText("●●●●●●●●●●●");
		passwordField.getStyleClass().add("login-field");

		// Password validation label
		passwordValidationLabel.getStyleClass().add("validation-label");
		passwordValidationLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
		passwordValidationLabel.setVisible(false);
		passwordValidationLabel.setText("verplicht");

		// Login button
		Button loginButton = new Button("AANMELDEN");
		loginButton.getStyleClass().add("login-button");
		loginButton.setOnAction(e -> handleLogin());
		loginButton.setMaxWidth(Double.MAX_VALUE);

		VBox.setVgrow(loginButton, Priority.ALWAYS);
		VBox.setMargin(welcomeLabel, new Insets(0));

		VBox emailContainer = new VBox(2);
		emailContainer.getChildren().addAll(emailField, emailValidationLabel);

		VBox passwordContainer = new VBox(2);
		passwordContainer.getChildren().addAll(passwordField, passwordValidationLabel);

		form.getChildren().addAll(welcomeLabel, infoBox, errorLabel, emailLabel, emailContainer, passwordLabel,
				passwordContainer, loginButton);

		return form;
	}

	private VBox createInfoBox() {
		VBox infoBox = new VBox(5);

		Label infoTitle = new Label("Nog geen account?");
		infoTitle.setStyle("-fx-font-weight: bold;");

		Label infoDescription = new Label("Contacteer je verantwoordelijke, manager of een administrator!");
		infoDescription.getStyleClass().add("info-description");

		infoBox.getChildren().addAll(infoTitle, infoDescription);
		return infoBox;
	}

	private StackPane createLoginImage(VBox loginForm) {
		StackPane imageContainer = new StackPane();
		imageContainer.setPrefWidth(800);
		imageContainer.prefHeightProperty().bind(loginForm.heightProperty());
		imageContainer.getStyleClass().add("login-image");

		Rectangle clip = new Rectangle();
		clip.setArcWidth(24);
		clip.setArcHeight(24);
		clip.widthProperty().bind(imageContainer.widthProperty());
		clip.heightProperty().bind(imageContainer.heightProperty());

		imageContainer.setClip(clip);

		return imageContainer;
	}

	private void handleLogin() {
		String email = emailField.getText().trim();
		String password = passwordField.getText();
		
		errorLabel.setVisible(false);
		emailValidationLabel.setVisible(false);
		passwordValidationLabel.setVisible(false);

		boolean isValid = true;

		if (email.isEmpty()) {
			emailValidationLabel.setVisible(true);
			isValid = false;
		}

		if (password.isEmpty()) {
			passwordValidationLabel.setVisible(true);
			isValid = false;
		}

		// Als alle velden zijn ingevuld, authenticatie doen:
		if (isValid) {
			try {
				userController.authenticate(email, password);
				errorLabel.setText("");
				navigateToChoicePane();
			} catch (InvalidInputException e) {
				errorLabel.setVisible(true);
				errorLabel.setText(e.getMessage());
			}
		}
	}

	private void navigateToChoicePane() {
		ChoicePane choicePane = new ChoicePane(stage);
		Scene scene = new Scene(choicePane);
		stage.setScene(scene);
	}
}