package gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.Role;
import utils.Status;

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

		Button closeButton = new Button("Sluiten");
		closeButton.setOnAction(e -> formStage.close());
		formPane.add(closeButton, 1, row);

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
}
