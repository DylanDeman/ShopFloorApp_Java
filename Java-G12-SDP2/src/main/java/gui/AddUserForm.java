package gui;

import java.time.LocalDate;

import domain.Address;
import domain.User;
import exceptions.InvalidAddressException;
import exceptions.InvalidUserException;
import jakarta.persistence.EntityManager;
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
import util.JPAUtil;
import util.Role;
import util.Status;

public class AddUserForm extends GridPane
{

	private TextField firstNameField, lastNameField, emailField, phoneField;
	private DatePicker birthdatePicker;
	private TextField streetField, houseNumberField, postalCodeField, cityField;
	private ComboBox<Role> roleBox;
	private ComboBox<Status> statusBox;
	private Label errorLabel;

	private EntityManager entityManager;
	private UserManagementPane userManagementPane;

	public AddUserForm(Stage primaryStage, UserManagementPane userManagementPane)
	{
		this.userManagementPane = userManagementPane;
		entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

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

		Label headerLabel = new Label("NIEUWE GEBRUIKER TOEVOEGEN");
		headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");
		HBox headerBox = new HBox(headerLabel);
		headerBox.setAlignment(Pos.CENTER);
		headerBox.setStyle("-fx-background-color: rgb(200, 50, 50); " + "-fx-padding: 15px; "
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

		Button addButton = new Button("Toevoegen");
		addButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
		addButton.setMaxWidth(Double.MAX_VALUE);
		addButton.setPadding(new Insets(10, 30, 10, 30));
		addButton.setOnAction(e -> addUser(primaryStage));

		HBox buttonBox = new HBox(addButton);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));
		HBox.setHgrow(addButton, Priority.ALWAYS);
		buttonBox.setMaxWidth(400);

		this.add(buttonBox, 0, 4, 2, 1);
		GridPane.setHalignment(buttonBox, HPos.CENTER);
	}

	private GridPane createUserFieldsSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(15);
		pane.setHgap(10);

		Label sectionLabel = new Label("Gebruikersgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, 0, 2, 1);

		firstNameField = new TextField();
		lastNameField = new TextField();
		emailField = new TextField();
		phoneField = new TextField();
		birthdatePicker = new DatePicker();

		firstNameField.setPrefWidth(200);
		lastNameField.setPrefWidth(200);
		emailField.setPrefWidth(200);
		phoneField.setPrefWidth(200);
		birthdatePicker.setPrefWidth(200);

		int row = 1;
		pane.add(new Label("Voornaam:"), 0, row);
		pane.add(firstNameField, 1, row++);

		pane.add(new Label("Achternaam:"), 0, row);
		pane.add(lastNameField, 1, row++);

		pane.add(new Label("Email:"), 0, row);
		pane.add(emailField, 1, row++);

		pane.add(new Label("Telefoonnummer:"), 0, row);
		pane.add(phoneField, 1, row++);

		pane.add(new Label("Geboortedatum:"), 0, row);
		pane.add(birthdatePicker, 1, row++);

		return pane;
	}

	private GridPane createAddressFieldsSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(15);
		pane.setHgap(10);

		Label sectionLabel = new Label("Adresgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, 0, 2, 1);

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

		pane.add(new Label("Huisnummer:"), 0, row);
		pane.add(houseNumberField, 1, row++);

		pane.add(new Label("Postcode:"), 0, row);
		pane.add(postalCodeField, 1, row++);

		pane.add(new Label("Stad:"), 0, row);
		pane.add(cityField, 1, row++);

		return pane;
	}

	private GridPane createRoleStatusSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(15);
		pane.setHgap(10);

		Label sectionLabel = new Label("Rol en Status");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, 0, 2, 1);

		roleBox = new ComboBox<>();
		roleBox.getItems().addAll(Role.values());
		roleBox.setPromptText("Selecteer een rol");
		roleBox.setPrefWidth(200);

		statusBox = new ComboBox<>();
		statusBox.getItems().addAll(Status.values());
		statusBox.setPromptText("Selecteer een status");
		statusBox.setPrefWidth(200);

		int row = 1;
		pane.add(new Label("Rol:"), 0, row);
		pane.add(roleBox, 1, row++);

		pane.add(new Label("Status:"), 0, row);
		pane.add(statusBox, 1, row++);

		return pane;
	}

	private void addUser(Stage primaryStage)
	{
		errorLabel.setText("");

		String firstName = firstNameField.getText();
		String lastName = lastNameField.getText();
		String email = emailField.getText();
		String phone = phoneField.getText();
		String street = streetField.getText();
		String houseNumberStr = houseNumberField.getText();
		String postalCodeStr = postalCodeField.getText();
		String city = cityField.getText();
		Role role = roleBox.getValue();
		Status status = statusBox.getValue();
		LocalDate birthdate = birthdatePicker.getValue();

		if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || street.isEmpty()
				|| houseNumberStr.isEmpty() || postalCodeStr.isEmpty() || city.isEmpty() || role == null
				|| status == null || birthdate == null)
		{
			showError("Alle velden zijn verplicht!");
			return;
		}

		int houseNumber = 0;
		int postalCode = 0;
		try
		{
			houseNumber = Integer.parseInt(houseNumberStr);
			postalCode = Integer.parseInt(postalCodeStr);
		} catch (NumberFormatException e)
		{
			showError("Huisnummer en postcode moeten numerieke waarden zijn!");
			return;
		}

		Address newAddress;

		try
		{
			newAddress = new Address(street, houseNumber, postalCode, city);
		} catch (InvalidAddressException e)
		{
			showError("Ongeldig adres: " + e.getMessage());
			return;
		}

		User newUser;
		try
		{
			newUser = new User(firstName, lastName, email, phone, generatePassword(), birthdate, newAddress, status,
					role);
		} catch (InvalidUserException e)
		{
			showError("Ongeldige gebruikersgegevens: " + e.getMessage());
			return;
		}

		entityManager.getTransaction().begin();

		try
		{
			entityManager.persist(newUser);

			entityManager.getTransaction().commit();

			userManagementPane.returnToUserManagement(primaryStage);

		} catch (Exception e)
		{
			if (entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().rollback();
			}
			System.err.println("Fout bij het toevoegen van de gebruiker:");
			e.printStackTrace();
		} finally
		{
			if (entityManager != null && entityManager.isOpen())
			{
				entityManager.close();
			}
		}

	}

	private void showError(String message)
	{
		errorLabel.setText(message);
	}

	private String generatePassword()
	{
		return "123456789";
	}

}
