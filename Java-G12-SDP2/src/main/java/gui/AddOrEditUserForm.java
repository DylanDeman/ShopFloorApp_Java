package gui;

import dto.AddressDTO;
import dto.UserDTO;
import exceptions.InformationRequired;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import util.AuthenticationUtil;
import util.Role;
import util.Status;

public class AddOrEditUserForm extends AddOrEditAbstract
{
    private UserDTO userDTO;
    private TextField firstNameField, lastNameField, emailField, phoneField;
    private DatePicker birthdatePicker;
    private TextField streetField, houseNumberField, postalCodeField, cityField;
    private ComboBox<Role> roleBox;
    private ComboBox<Status> statusBox;
    private Label firstNameError, lastNameError, emailError, phoneError, birthdateError;
    private Label streetError, houseNumberError, postalCodeError, cityError;
    private Label roleError, statusError;
    
    public AddOrEditUserForm(MainLayout mainLayout, int userId)
    {
        super(mainLayout, false);
        
        this.userDTO = userController.getUserById(userId);
    }

    public AddOrEditUserForm(MainLayout mainLayout)
    {
        super(mainLayout, true);
    }

    @Override
    protected void initializeFields()
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

    @Override
    protected VBox createLeftBox()
    {
        VBox leftBox = new VBox(20);
        leftBox.setAlignment(Pos.TOP_LEFT);
        leftBox.setMinWidth(400);
        leftBox.setMaxWidth(400);

        leftBox.getChildren().addAll(createUserFieldsSection());

        return leftBox;
    }

    @Override
    protected VBox createRightBox()
    {
        VBox rightBox = new VBox(20);
        rightBox.setAlignment(Pos.TOP_LEFT);
        rightBox.setMinWidth(400);
        rightBox.setMaxWidth(400);

        rightBox.getChildren().addAll(createAddressFieldsSection(), createRoleStatusSection());

        return rightBox;
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

        String labelString = isNew ? "Rol" : "Rol en status";

        Label sectionLabel = new Label(labelString);

        sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        pane.add(sectionLabel, 0, 0, 2, 1);

        int row = 1;
        pane.add(new Label("Rol:"), 0, row);
        pane.add(roleBox, 1, row++);
        pane.add(roleError, 1, row++);

        if (!isNew)
        {
            pane.add(new Label("Status:"), 0, row);
            pane.add(statusBox, 1, row++);
            pane.add(statusError, 1, row++);
        }

        return pane;
    }

    @Override
    protected void fillData()
    {
    	Platform.runLater(() -> {
    		firstNameField.setText(userDTO.firstName());
	        lastNameField.setText(userDTO.lastName());
	        emailField.setText(userDTO.email());
	        phoneField.setText(userDTO.phoneNumber());
	        birthdatePicker.setValue(userDTO.birDate());

	        AddressDTO address = userDTO.address();
	        if (address != null)
	        {
	            streetField.setText(address.street());
	            houseNumberField.setText(String.valueOf(address.number()));
	            postalCodeField.setText(String.valueOf(address.postalcode()));
	            cityField.setText(address.city());
	        }
	        
	        roleBox.setValue(userDTO.role());
	        statusBox.setValue(userDTO.status());
    	});
    }

    @Override
    protected void save()
    {
        resetErrorLabels();

        if (!AuthenticationUtil.hasRole(Role.ADMINISTRATOR))
        {
            mainLayout.showNotAllowedAlert();
            return;
        }

        try
        {
            if (isNew)
            {
                userController.createUser(firstNameField.getText(), lastNameField.getText(), emailField.getText(),
                        phoneField.getText(), birthdatePicker.getValue(), streetField.getText(),
                        houseNumberField.getText(), postalCodeField.getText(), cityField.getText(), roleBox.getValue());
            } else
            {
            	userController.updateUser(userDTO.id(), firstNameField.getText(), lastNameField.getText(), emailField.getText(),
                        phoneField.getText(), birthdatePicker.getValue(), streetField.getText(),
                        houseNumberField.getText(), postalCodeField.getText(), cityField.getText(), roleBox.getValue(),
                        statusBox.getValue());
            }

            navigateBack();
        } catch (NumberFormatException e)
        {
            showError("Huisnummer en postcode moeten numeriek zijn");
        } catch (IllegalArgumentException e)
        {
            handleInformationRequiredException(e);
        }  catch (Exception e)
        {
        	// hier ipv dit teruggaan naar beginscherm
            showError("Er is een fout opgetreden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void navigateBack()
    {
        mainLayout.showUserManagementScreen();
    }

    @Override
    protected String getTitleText()
    {
        return isNew ? "Gebruiker toevoegen" : "Gebruiker aanpassen";
    }

    @Override
    protected void handleInformationRequiredException(Exception e)
    {
        if (e instanceof InformationRequired) {
            InformationRequired exception = (InformationRequired) e;
            exception.getRequiredElements().forEach((field, requiredElement) -> {
                String errorMessage = requiredElement.getMessage();
                showFieldError(field, errorMessage);
            });
        }
        if (e instanceof IllegalArgumentException) {
        	String errorMessage = e.getMessage();
        	showFieldError("email", errorMessage);
        }
    }

    @Override
    protected void showFieldError(String fieldName, String message)
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

    @Override
    protected void resetErrorLabels()
    {
        super.resetErrorLabels();
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