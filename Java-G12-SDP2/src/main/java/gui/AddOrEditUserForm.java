package gui;

import domain.Address;
import domain.user.User;
import domain.user.UserBuilder;
import exceptions.InformationRequiredException;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import repository.UserRepository;
import util.RequiredElement;
import util.Role;
import util.Status;

public class AddOrEditUserForm extends GridPane
{
	private User user;
	private final UserManagementPane userManagementPane;
	private final UserRepository userRepo;
	private final Stage primaryStage;

	private TextField firstNameField, lastNameField, emailField, phoneField;
	private DatePicker birthdatePicker;
	private TextField streetField, houseNumberField, postalCodeField, cityField;
	private ComboBox<Role> roleBox;
	private ComboBox<Status> statusBox;
	private Label errorLabel;
	private Label firstNameError, lastNameError, emailError, phoneError, birthdateError;
	private Label streetError, houseNumberError, postalCodeError, cityError;
	private Label roleError, statusError;

	private boolean isNewUser;

	public AddOrEditUserForm(Stage primaryStage, UserRepository userRepo, UserManagementPane userManagementPane,
			User user)
	{
		this.primaryStage = primaryStage;
		this.userRepo = userRepo;
		this.userManagementPane = userManagementPane;
		this.user = user;
		this.isNewUser = user == null;

		buildGUI();

		if (!isNewUser)
		{
			fillUserData(user);
		}

	}

	private void buildGUI()
	{
		this.setPadding(new Insets(20));
		this.setHgap(20);
		this.setVgap(20);

		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		setBackground(new Background(backgroundImage));

		Button backButton = new Button("← Terug");
		backButton.setOnAction(e -> userManagementPane.returnToUserManagement(primaryStage));
		this.add(backButton, 0, 0, 2, 1);

		errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		errorLabel.setWrapText(true);
		this.add(errorLabel, 0, 1, 2, 1);

		Label headerLabel = new Label(isNewUser ? "GEBRUIKER TOEVOEGEN" : "GEBRUIKER AANPASSEN");
		headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");
		HBox headerBox = new HBox(headerLabel);
		headerBox.setAlignment(Pos.CENTER);
		headerBox.setStyle("-fx-background-color: rgb(240, 69, 60); " + "-fx-padding: 15px; "
				+ "-fx-border-radius: 5 5 5 5; " + "-fx-background-radius: 5 5 5 5;");
		headerBox.setMaxWidth(Double.MAX_VALUE);
		headerBox.setMaxHeight(40);
		this.add(headerBox, 0, 2, 2, 1);

		GridPane.setMargin(headerBox, new Insets(0, 0, 0, 0));

		HBox mainContent = new HBox(30);
		mainContent.setAlignment(Pos.TOP_CENTER);
		mainContent.setStyle(
				"-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");

		mainContent.setMaxWidth(Double.MAX_VALUE);

		VBox userFieldsBox = new VBox(15);
		userFieldsBox.setPadding(new Insets(20));
		userFieldsBox.getChildren().add(createUserFieldsSection());

		Line divider = new Line(0, 0, 0, 400);
		divider.setStroke(Color.LIGHTGRAY);
		divider.setStrokeWidth(1);

		VBox rightFieldsBox = new VBox(15);
		rightFieldsBox.setPadding(new Insets(20));

		VBox addressBox = new VBox(15, createAddressFieldsSection());
		VBox roleStatusBox = new VBox(15, createRoleStatusSection());
		rightFieldsBox.getChildren().addAll(addressBox, roleStatusBox);

		mainContent.getChildren().addAll(userFieldsBox, divider, rightFieldsBox);
		this.add(mainContent, 0, 3, 2, 1);

		Button saveButton = new Button("Opslaan");
		saveButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
		saveButton.setMaxWidth(Double.MAX_VALUE);
		saveButton.setPadding(new Insets(10, 30, 10, 30));
		saveButton.setOnAction(e -> saveUser());

		HBox buttonBox = new HBox(saveButton);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));
		HBox.setHgrow(saveButton, Priority.ALWAYS);
		buttonBox.setMaxWidth(400);

		this.add(buttonBox, 0, 4, 2, 1);
		GridPane.setHalignment(buttonBox, HPos.CENTER);
	}

	private void fillUserData(User user)
	{
		firstNameField.setText(user.getFirstName());
		lastNameField.setText(user.getLastName());
		emailField.setText(user.getEmail());
		phoneField.setText(user.getPhoneNumber());
		birthdatePicker.setValue(user.getBirthdate());

		Address address = user.getAddress();
		if (address != null)
		{
			streetField.setText(address.getStreet());
			houseNumberField.setText(String.valueOf(address.getNumber()));
			postalCodeField.setText(String.valueOf(address.getPostalcode()));
			cityField.setText(address.getCity());
		}

		roleBox.setValue(user.getRole());
		statusBox.setValue(user.getStatus());
	}

	private GridPane createUserFieldsSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel = new Label("Gebruikersgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, 0, 2, 1);

		firstNameError = createErrorLabel();
		lastNameError = createErrorLabel();
		emailError = createErrorLabel();
		phoneError = createErrorLabel();
		birthdateError = createErrorLabel();

		firstNameField = new TextField();
		lastNameField = new TextField();
		emailField = new TextField();
		phoneField = new TextField();
		birthdatePicker = new DatePicker();
		birthdatePicker.setEditable(false);

		firstNameField.setPrefWidth(200);
		lastNameField.setPrefWidth(200);
		emailField.setPrefWidth(200);
		phoneField.setPrefWidth(200);
		birthdatePicker.setPrefWidth(200);

		int row = 1;
		pane.add(new Label("Voornaam:"), 0, row);
		pane.add(firstNameField, 1, row++);
		pane.add(firstNameError, 1, row++);

		pane.add(new Label("Achternaam:"), 0, row);
		pane.add(lastNameField, 1, row++);
		pane.add(lastNameError, 1, row++);

		pane.add(new Label("Email:"), 0, row);
		pane.add(emailField, 1, row++);
		pane.add(emailError, 1, row++);

		pane.add(new Label("Telefoonnummer:"), 0, row);
		pane.add(phoneField, 1, row++);
		pane.add(phoneError, 1, row++);

		pane.add(new Label("Geboortedatum:"), 0, row);
		pane.add(birthdatePicker, 1, row++);
		pane.add(birthdateError, 1, row++);

		return pane;
	}

	private GridPane createAddressFieldsSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel = new Label("Adresgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, 0, 2, 1);

		streetError = createErrorLabel();
		houseNumberError = createErrorLabel();
		postalCodeError = createErrorLabel();
		cityError = createErrorLabel();

		streetField = new TextField();
		houseNumberField = new TextField();
		postalCodeField = new TextField();
		cityField = new TextField();

		streetField.setPrefWidth(200);
		houseNumberField.setPrefWidth(200);
		postalCodeField.setPrefWidth(200);
		cityField.setPrefWidth(200);

		int row = 1;
		pane.add(new Label("Straat:"), 0, row);
		pane.add(streetField, 1, row++);
		pane.add(streetError, 1, row++);

		pane.add(new Label("Huisnummer:"), 0, row);
		pane.add(houseNumberField, 1, row++);
		pane.add(houseNumberError, 1, row++);

		pane.add(new Label("Postcode:"), 0, row);
		pane.add(postalCodeField, 1, row++);
		pane.add(postalCodeError, 1, row++);

		pane.add(new Label("Stad:"), 0, row);
		pane.add(cityField, 1, row++);
		pane.add(cityError, 1, row++);

		return pane;
	}

	private GridPane createRoleStatusSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		String labelString = isNewUser ? "Rol" : "Rol en status";

		Label sectionLabel = new Label(labelString);

		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, 0, 2, 1);

		roleError = createErrorLabel();
		statusError = createErrorLabel();

		roleBox = new ComboBox<>();
		roleBox.getItems().addAll(Role.values());
		roleBox.setPromptText("Selecteer een rol");
		roleBox.setPrefWidth(200);

		int row = 1;
		pane.add(new Label("Rol:"), 0, row);
		pane.add(roleBox, 1, row++);
		pane.add(roleError, 1, row++);

		if (!isNewUser)
		{
			statusBox = new ComboBox<>();
			statusBox.getItems().addAll(Status.values());
			statusBox.setPromptText("Wijzig de status");
			statusBox.setPrefWidth(200);

			pane.add(new Label("Status:"), 0, row);
			pane.add(statusBox, 1, row++);
			pane.add(statusError, 1, row++);
		}

		return pane;
	}

	private void saveUser()
	{
		resetErrorLabels();

		try
		{
			UserBuilder userBuilder = new UserBuilder();
			userBuilder.createUser();
			userBuilder.buildName(firstNameField.getText(), lastNameField.getText());
			userBuilder.buildContactInfo(emailField.getText(), phoneField.getText());
			userBuilder.buildBirthdate(birthdatePicker.getValue());
			userBuilder.createAddress();
			userBuilder.buildStreet(streetField.getText());
			userBuilder.buildNumber(Integer.parseInt(houseNumberField.getText()));
			userBuilder.buildPostalcode(Integer.parseInt(postalCodeField.getText()));
			userBuilder.buildCity(cityField.getText());

			if (isNewUser)
			{
				userBuilder.buildRoleAndStatus(roleBox.getValue(), Status.ACTIEF);
				User newUser = userBuilder.getUser();
				userRepo.addUser(newUser);
			} else
			{
				userBuilder.buildRoleAndStatus(roleBox.getValue(), statusBox.getValue());
				User updatedUser = userBuilder.getUser();
				updatedUser.setId(user.getId());
				updatedUser.getAddress().setId(user.getAddress().getId());
				userRepo.updateUser(updatedUser);
			}

			userManagementPane.returnToUserManagement(primaryStage);
		} catch (InformationRequiredException e)
		{
			handleInformationRequiredException(e);
		} catch (NumberFormatException e)
		{
			showError("Huisnummer en postcode moeten numeriek zijn");
		} catch (Exception e)
		{
			showError("Er is een fout opgetreden: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private Label createErrorLabel()
	{
		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		errorLabel.setStyle("-fx-font-size: 10px;");
		errorLabel.setMaxWidth(150);
		errorLabel.setWrapText(true);
		return errorLabel;
	}

	private void showError(String message)
	{
		errorLabel.setText(message);
	}

	private void handleInformationRequiredException(InformationRequiredException e)
	{
		e.getInformationRequired().forEach((field, requiredElement) -> {
			String errorMessage = getErrorMessageForRequiredElement(requiredElement);
			showFieldError(field, errorMessage);
		});

	}

	private String getErrorMessageForRequiredElement(RequiredElement element)
	{
		switch (element)
		{
		case FIRST_NAME_REQUIRED:
			return "Voornaam is verplicht";
		case LAST_NAME_REQUIRED:
			return "Achternaam is verplicht";
		case EMAIL_REQUIRED:
			return "Email is verplicht";
		case BIRTH_DATE_REQUIRED:
			return "Geboortedatum is verplicht";
		case STREET_REQUIRED:
			return "Straat is verplicht";
		case NUMBER_REQUIRED:
			return "Huisnummer is verplicht";
		case POSTAL_CODE_REQUIRED:
			return "Postcode is verplicht";
		case CITY_REQUIRED:
			return "Stad is verplicht";
		case ROLE_REQUIRED:
			return "Rol is verplicht";
		case STATUS_REQUIRED:
			return "Status is verplicht";
		default:
			return "Verplicht veld";
		}
	}

	private void showFieldError(String fieldName, String message)
	{
		switch (fieldName)
		{
		case "firstName":
			firstNameError.setText(message);
			break;
		case "lastName":
			lastNameError.setText(message);
			break;
		case "email":
			emailError.setText(message);
			break;
		case "phone":
			phoneError.setText(message);
			break;
		case "birthDate":
			birthdateError.setText(message);
			break;
		case "street":
			streetError.setText(message);
			break;
		case "number":
			houseNumberError.setText(message);
			break;
		case "postalCode":
			postalCodeError.setText(message);
			break;
		case "city":
			cityError.setText(message);
			break;
		case "role":
			roleError.setText(message);
			break;
		case "status":
			statusError.setText(message);
			break;
		default:
			errorLabel.setText(message);
		}
	}

	private void resetErrorLabels()
	{
		errorLabel.setText("");
		firstNameError.setText("");
		lastNameError.setText("");
		emailError.setText("");
		phoneError.setText("");
		birthdateError.setText("");
		streetError.setText("");
		houseNumberError.setText("");
		postalCodeError.setText("");
		cityError.setText("");
		roleError.setText("");
		statusError.setText("");
	}
}
