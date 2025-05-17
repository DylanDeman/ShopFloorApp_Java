package gui;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.user.UserController;
import dto.AddressDTO;
import dto.UserDTO;
import exceptions.InformationRequiredException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import util.AuthenticationUtil;
import util.RequiredElement;
import util.Role;
import util.Status;

public class AddOrEditUserForm extends GridPane
{
	private UserDTO user;

	private final MainLayout mainLayout;
	private UserController uc;

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

	public AddOrEditUserForm(MainLayout mainLayout, int userId)
	{
		this.mainLayout = mainLayout;
		this.isNewUser = false;

		this.uc = AppServices.getInstance().getUserController();
		this.user = uc.getUserDTOById(userId);

		initializeFields();
		buildGUI();

		fillUserData();
	}

	public AddOrEditUserForm(MainLayout mainLayout)
	{
		this.isNewUser = true;
		this.mainLayout = mainLayout;

		this.uc = AppServices.getInstance().getUserController();

		initializeFields();
		buildGUI();
	}

	private void buildGUI()
	{
		this.getStylesheets().add(getClass().getResource("/css/form.css").toExternalForm());
		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(15);
		this.setPadding(new Insets(20));

		VBox formAndSaveButton = new VBox(10);
		formAndSaveButton.getChildren().addAll(createFormContent(), createSaveButton());

		VBox mainContainer = new VBox(10);
		mainContainer.setAlignment(Pos.TOP_CENTER);
		mainContainer.setPadding(new Insets(10));
		mainContainer.getChildren().addAll(createTitleSection(), errorLabel, formAndSaveButton);

		this.add(mainContainer, 0, 0);
	}

	private HBox createFormContent()
	{
		HBox formContent = new HBox(30);
		formContent.setAlignment(Pos.TOP_CENTER);
		formContent.getStyleClass().add("form-box");
		formContent.setMaxWidth(800);

		VBox siteNameBox = new VBox(15, createUserFieldsSection());
		VBox addressBox = new VBox(15, createAddressFieldsSection());
		VBox employeeBox = new VBox(15, createRoleStatusSection());

		VBox leftBox = new VBox(20);
		leftBox.setAlignment(Pos.TOP_LEFT);
		leftBox.setMinWidth(400);
		leftBox.setMaxWidth(400);

		leftBox.getChildren().addAll(siteNameBox);

		VBox rightBox = new VBox(20);
		rightBox.setAlignment(Pos.TOP_LEFT);
		rightBox.setMinWidth(400);
		rightBox.setMaxWidth(400);

		rightBox.getChildren().addAll(addressBox, employeeBox);

		formContent.getChildren().addAll(leftBox, rightBox);

		return formContent;
	}

	private HBox createSaveButton()
	{
		Button saveButton = new Button("Opslaan");
		saveButton.getStyleClass().add("save-button");
		saveButton.setOnAction(e -> saveUser());

		saveButton.setPrefSize(300, 40);
		saveButton.setMaxWidth(Double.MAX_VALUE);

		HBox buttonBox = new HBox(saveButton);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));

		buttonBox.setMinWidth(800);
		buttonBox.setMaxWidth(800);

		return buttonBox;
	}

	private VBox createTitleSection()
	{
		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.CENTER_LEFT);

		FontIcon icon = new FontIcon("fas-arrow-left");
		icon.setIconSize(20);
		Button backButton = new Button();
		backButton.setGraphic(icon);
		backButton.getStyleClass().add("back-button");
		backButton.setOnAction(e -> mainLayout.showUserManagementScreen());
		this.add(backButton, 0, 0, 2, 1);

		Label title = new Label(isNewUser ? "Gebruiker toevoegen" : "Gebruiker aanpassen");
		title.getStyleClass().add("title-label");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, title, spacer);

		return new VBox(10, hbox);
	}

	private void fillUserData()
	{
		firstNameField.setText(user.firstName());
		lastNameField.setText(user.lastName());
		emailField.setText(user.email());
		phoneField.setText(user.phoneNumber());
		birthdatePicker.setValue(user.birDate());

		AddressDTO address = user.address();
		if (address != null)
		{
			streetField.setText(address.street());
			houseNumberField.setText(String.valueOf(address.number()));
			postalCodeField.setText(String.valueOf(address.postalcode()));
			cityField.setText(address.city());
		}

		roleBox.setValue(user.role());
		statusBox.setValue(user.status());
	}

	private GridPane createUserFieldsSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel = new Label("Gebruikersgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, 0, 2, 1);

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
		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0, 2, 1);

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

		int row = 1;
		pane.add(new Label("Rol:"), 0, row);
		pane.add(roleBox, 1, row++);
		pane.add(roleError, 1, row++);

		if (!isNewUser)
		{
			pane.add(new Label("Status:"), 0, row);
			pane.add(statusBox, 1, row++);
			pane.add(statusError, 1, row++);
		}

		return pane;
	}

	private void saveUser()
	{
		resetErrorLabels();

		if (!AuthenticationUtil.hasRole(Role.ADMINISTRATOR))
		{
			mainLayout.showNotAllowedAlert();
			return;
		}

		try
		{
			if (isNewUser)
			{
				uc.createUser(firstNameField.getText(), lastNameField.getText(), emailField.getText(),
						phoneField.getText(), birthdatePicker.getValue(), streetField.getText(),
						houseNumberField.getText(), postalCodeField.getText(), cityField.getText(), roleBox.getValue());
			} else
			{
				uc.updateUser(user.id(), firstNameField.getText(), lastNameField.getText(), emailField.getText(),
						phoneField.getText(), birthdatePicker.getValue(), streetField.getText(),
						houseNumberField.getText(), postalCodeField.getText(), cityField.getText(), roleBox.getValue(),
						statusBox.getValue());
			}

			mainLayout.showUserManagementScreen();
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
		errorLabel.getStyleClass().add("error-label");
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

	private void initializeFields()
	{
		streetError = createErrorLabel();
		houseNumberError = createErrorLabel();
		postalCodeError = createErrorLabel();
		cityError = createErrorLabel();
		roleError = createErrorLabel();
		statusError = createErrorLabel();
		firstNameError = createErrorLabel();
		lastNameError = createErrorLabel();
		emailError = createErrorLabel();
		phoneError = createErrorLabel();
		birthdateError = createErrorLabel();
		errorLabel = createErrorLabel();

		statusBox = new ComboBox<>();
		statusBox.getItems().addAll(Status.values());
		statusBox.setPromptText("Wijzig de status");

		roleBox = new ComboBox<>();
		roleBox.getItems().addAll(Role.values());
		roleBox.setPromptText("Selecteer een rol");

		firstNameField = new TextField();
		lastNameField = new TextField();
		emailField = new TextField();
		phoneField = new TextField();
		birthdatePicker = new DatePicker();
		birthdatePicker.setEditable(false);
		streetField = new TextField();
		houseNumberField = new TextField();
		postalCodeField = new TextField();
		cityField = new TextField();
	}
}
