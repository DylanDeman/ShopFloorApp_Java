package gui;

import domain.User;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import util.Role;
import util.Status;

public class EditUserForm extends GridPane
{

	private TextField firstNameField, lastNameField, emailField, phoneField;
	private DatePicker birthdatePicker;
	private TextField streetField, houseNumberField, postalCodeField, cityField;
	private ComboBox<Role> roleBox;
	private ComboBox<Status> statusBox;

	public EditUserForm(Stage primaryStage, User user, UserManagementPane userManagementPane)
	{

		setPadding(new Insets(20));
		setHgap(10);
		setVgap(10);

		int row = 0;

		Button backButton = new Button("← Terug");
		backButton.setOnAction(e -> userManagementPane.returnToUserManagement(primaryStage));
		add(backButton, 0, row++, 2, 1);

		row = makeUserFields(this, row, user);
		row = makeAddressFields(this, row, user);
		row = makeRoleAndStatusFields(this, row, user);

		HBox buttonBox = new HBox(10);
		buttonBox.setPadding(new Insets(10));
		Button saveButton = new Button("Opslaan");
		saveButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
		saveButton.setOnAction(e -> {
			saveUser(user);
			userManagementPane.returnToUserManagement(primaryStage);
		});

		buttonBox.getChildren().add(saveButton);
		buttonBox.setStyle("-fx-alignment: center-right;");

		add(buttonBox, 1, row, 2, 1);
	}

	private int makeUserFields(GridPane pane, int row, User user)
	{
		Label sectionLabel = new Label("Gebruikersgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, row++, 2, 1);

		firstNameField = new TextField(user.getFirstName());
		lastNameField = new TextField(user.getLastName());
		emailField = new TextField(user.getEmail());
		phoneField = new TextField(user.getPhoneNumber());
		birthdatePicker = new DatePicker(user.getBirthdate());

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

	private int makeAddressFields(GridPane pane, int row, User user)
	{
		Label sectionLabel = new Label("Adresgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, row++, 2, 1);

		streetField = new TextField(user.getAddress().getStreet());
		houseNumberField = new TextField(String.valueOf(user.getAddress().getNumber()));
		postalCodeField = new TextField(String.valueOf(user.getAddress().getPostalcode()));
		cityField = new TextField(user.getAddress().getCity());

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

	private int makeRoleAndStatusFields(GridPane pane, int row, User user)
	{
		Label sectionLabel = new Label("Rol en Status");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, row++, 2, 1);

		roleBox = new ComboBox<>();
		roleBox.getItems().addAll(Role.values());
		roleBox.setValue(user.getRole());
		roleBox.setPromptText("Selecteer een rol");

		statusBox = new ComboBox<>();
		statusBox.getItems().addAll(Status.values());
		statusBox.setValue(user.getStatus());
		statusBox.setPromptText("Selecteer een status");

		pane.add(new Label("Rol:"), 0, row);
		pane.add(roleBox, 1, row++);

		pane.add(new Label("Status:"), 0, row);
		pane.add(statusBox, 1, row++);

		return row;
	}

	private void saveUser(User user)
	{
		user.setFirstName(firstNameField.getText());
		user.setLastName(lastNameField.getText());
		user.setEmail(emailField.getText());
		user.setPhoneNumber(phoneField.getText());
		user.setBirthdate(birthdatePicker.getValue());

		user.getAddress().setStreet(streetField.getText());
		user.getAddress().setNumber(Integer.parseInt(houseNumberField.getText()));
		user.getAddress().setPostalcode(Integer.parseInt(postalCodeField.getText()));
		user.getAddress().setCity(cityField.getText());

		user.setRole(roleBox.getValue());
		user.setStatus(statusBox.getValue());

		System.out.println("Gebruiker bijgewerkt!");
	}
}
