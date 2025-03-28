package gui;

import java.time.LocalDate;

import domain.Address;
import domain.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Role;
import util.Status;

public class AddUserForm
{

	private TextField firstNameField, lastNameField, emailField, phoneField;
	private DatePicker birthdatePicker;
	private TextField streetField, houseNumberField, postalCodeField, cityField;
	private ComboBox<Role> roleBox;
	private ComboBox<Status> statusBox;

	public AddUserForm(Stage formStage)
	{
		formStage.initModality(Modality.APPLICATION_MODAL);
		formStage.setTitle("Nieuwe Gebruiker Toevoegen");

		GridPane formPane = new GridPane();
		formPane.setPadding(new Insets(20));
		formPane.setHgap(10);
		formPane.setVgap(10);

		int row = 0;

		row = makeUserFields(formPane, row);
		row = makeAddressFields(formPane, row);
		row = makeRoleAndStatusFields(formPane, row);

		HBox buttonBox = new HBox(10);
		buttonBox.setPadding(new Insets(10));
		Button addButton = new Button("Toevoegen");
		addButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
		addButton.setOnAction(e -> addUser(formStage));

		Button closeButton = new Button("Sluiten");
		closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
		closeButton.setOnAction(e -> formStage.close());

		buttonBox.getChildren().addAll(closeButton, addButton);
		buttonBox.setStyle("-fx-alignment: center-right;");

		formPane.add(buttonBox, 1, row, 2, 1);

		Scene scene = new Scene(formPane, 400, 550);
		formStage.setScene(scene);
		formStage.showAndWait();
	}

	private int makeUserFields(GridPane pane, int row)
	{
		Label sectionLabel = new Label("Gebruikersgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, row++, 2, 1);

		firstNameField = new TextField();
		lastNameField = new TextField();
		emailField = new TextField();
		phoneField = new TextField();
		birthdatePicker = new DatePicker();

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

		return row;
	}

	private int makeAddressFields(GridPane pane, int row)
	{
		Label sectionLabel = new Label("Adresgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, row++, 2, 1);

		streetField = new TextField();
		houseNumberField = new TextField();
		postalCodeField = new TextField();
		cityField = new TextField();

		pane.add(new Label("Straat:"), 0, row);
		pane.add(streetField, 1, row++);

		pane.add(new Label("Huisnummer:"), 0, row);
		pane.add(houseNumberField, 1, row++);

		pane.add(new Label("Postcode:"), 0, row);
		pane.add(postalCodeField, 1, row++);

		pane.add(new Label("Stad:"), 0, row);
		pane.add(cityField, 1, row++);

		return row;
	}

	private int makeRoleAndStatusFields(GridPane pane, int row)
	{
		Label sectionLabel = new Label("Rol en Status");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, row++, 2, 1);

		roleBox = new ComboBox<>();
		roleBox.getItems().addAll(Role.values());
		roleBox.setPromptText("Selecteer een rol");

		statusBox = new ComboBox<>();
		statusBox.getItems().addAll(Status.values());
		statusBox.setPromptText("Selecteer een status");

		pane.add(new Label("Rol:"), 0, row);
		pane.add(roleBox, 1, row++);

		pane.add(new Label("Status:"), 0, row);
		pane.add(statusBox, 1, row++);

		return row;
	}

	private void addUser(Stage formStage)
	{
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
			System.out.println("Een van de velden bij het toevoegen van deze gebruiker was leeg!");
		}

		int houseNumber = 0;
		int postalCode = 0;
		try
		{
			houseNumber = Integer.parseInt(houseNumberStr);
			postalCode = Integer.parseInt(postalCodeStr);
		} catch (NumberFormatException e)
		{
			e.printStackTrace();
		}

		Address newAddress = new Address(street, houseNumber, postalCode, city);

		User newUser = new User(firstName, lastName, email, phone, generatePassword(), birthdate, newAddress, status,
				role);

		printUser(newUser);

		formStage.close();
	}

	private String generatePassword()
	{
		return "123456789";
	}

	private void printUser(User user)
	{
		System.out.printf("Nieuwe gebruiker toegevoegd! --> %n");
		System.out.printf("Voornaam: %s%n", user.getFirstName());
		System.out.printf("Achternaam: %s%n", user.getLastName());
		System.out.printf("Email: %s%n", user.getEmail());
		System.out.printf("Telefoonnummer: %s%n", user.getPhoneNumber());
		System.out.printf("Geboortedatum: %s%n", user.getBirthdate());
		System.out.printf("Adres: %s %d, %s, %s%n", user.getAddress().getStreet(), user.getAddress().getNumber(),
				user.getAddress().getPostalcode(), user.getAddress().getCity());
		System.out.printf("Rol: %s%n", user.getRole());
		System.out.printf("Status: %s%n", user.getStatus());

	}
}
