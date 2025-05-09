package gui;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.Address;
import domain.site.Site;
import domain.site.SiteBuilder;
import domain.user.User;
import domain.user.UserController;
import exceptions.InformationRequiredExceptionSite;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import repository.SiteRepository;
import util.RequiredElementSite;
import util.Status;

public class AddOrEditSiteForm extends GridPane
{
	private Site site;
	private final SiteRepository siteRepo;
	private final MainLayout mainLayout;
	private UserController uc;

	private TextField siteNameField;
	private TextField streetField, houseNumberField, postalCodeField, cityField;
	private ComboBox<String> employeeBox;
	private ComboBox<Status> statusBox;

	private Label errorLabel, siteNameError, employeeError;
	private Label streetError, houseNumberError, postalCodeError, cityError;
	private Label statusError;

	private boolean isNewSite;

	public AddOrEditSiteForm(MainLayout mainLayout, SiteRepository siteRepo, Site site)
	{
		this.siteRepo = siteRepo;
		this.mainLayout = mainLayout;
		this.site = site;
		this.isNewSite = site == null;
		uc = new UserController();

		initializeFields();
		buildGUI();

		if (!isNewSite)
		{
			fillSiteData(site);
		}

	}

	private void buildGUI()
	{
		this.getStylesheets().add(getClass().getResource("/css/form.css").toExternalForm());
		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(15);
		this.setPadding(new Insets(20));

		VBox mainContainer = new VBox(10);
		mainContainer.setAlignment(Pos.TOP_CENTER);
		mainContainer.setPadding(new Insets(10));
		mainContainer.setMaxWidth(850);

		VBox formAndSaveButton = new VBox(10);
		formAndSaveButton.getChildren().addAll(createFormContent(), createSaveButton());

		mainContainer.getChildren().addAll(createTitleSection(), errorLabel, formAndSaveButton);

		this.add(mainContainer, 0, 0);
	}

	private HBox createFormContent()
	{
		HBox formContent = new HBox(30);
		formContent.setAlignment(Pos.TOP_CENTER);
		formContent.getStyleClass().add("form-box");
		formContent.setMaxWidth(800);

		VBox siteNameBox = new VBox(15, createSiteNameField());
		VBox addressBox = new VBox(15, createAddressFieldsSection());
		VBox comboBoxBox = new VBox(15, createComboBoxSection());

		VBox leftBox = new VBox(20);
		leftBox.setAlignment(Pos.TOP_LEFT);
		leftBox.setMinWidth(400);
		leftBox.setMaxWidth(400);

		leftBox.getChildren().addAll(siteNameBox, comboBoxBox);

		VBox rightBox = new VBox(20);
		rightBox.setAlignment(Pos.TOP_LEFT);
		rightBox.setMinWidth(400);
		rightBox.setMaxWidth(400);

		rightBox.getChildren().addAll(addressBox);

		formContent.getChildren().addAll(leftBox, rightBox);

		return formContent;
	}

	private HBox createSaveButton()
	{
		Button saveButton = new Button("Opslaan");
		saveButton.getStyleClass().add("save-button");
		saveButton.setOnAction(e -> saveSite());

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
		backButton.setOnAction(e -> mainLayout.showSiteList());
		this.add(backButton, 0, 0, 2, 1);

		Label title = new Label(isNewSite ? "Site toevoegen" : "Site aanpassen");
		title.getStyleClass().add("title-label");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, title, spacer);

		return new VBox(10, hbox);
	}

	private Node createComboBoxSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		String labelString = isNewSite ? "Verantwoordelijke" : "Verantwoordelijke en status";

		Label sectionLabel = new Label(labelString);

		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0, 2, 1);

		employeeBox = new ComboBox<>();
		employeeBox.setPromptText("Kies een verantwoordelijke");
		employeeBox.setPrefWidth(200);
		Set<String> uniqueEmployees = siteRepo.getAllEmployees().stream().map(User::getFullName)
				.collect(Collectors.toCollection(TreeSet::new));

		employeeBox.getItems().addAll(uniqueEmployees);

		int row = 1;
		pane.add(new Label("Verantwoordelijke:"), 0, row);
		pane.add(employeeBox, 1, row++);
		pane.add(employeeError, 1, row++);

		if (!isNewSite)
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

	private void saveSite()
	{
		resetErrorLabels();

		try
		{
			SiteBuilder siteBuilder = new SiteBuilder();
			siteBuilder.createSite();
			siteBuilder.buildName(siteNameField.getText());
			siteBuilder.createAddress();
			siteBuilder.buildStreet(streetField.getText());
			siteBuilder.buildNumber(Integer.parseInt(houseNumberField.getText()));
			siteBuilder.buildPostalcode(Integer.parseInt(postalCodeField.getText()));
			siteBuilder.buildCity(cityField.getText());
			siteBuilder.buildEmployee(uc.getAllUsers().stream()
					.filter(user -> user.getFullName().equals(employeeBox.getValue())).findFirst().orElse(null));

			if (isNewSite)
			{
				siteBuilder.buildStatus(Status.ACTIEF);
				Site newSite = siteBuilder.getSite();
				siteRepo.addSite(newSite);
			} else
			{
				siteBuilder.buildStatus(statusBox.getValue());
				Site updatedSite = siteBuilder.getSite();
				updatedSite.setId(site.getId());
				updatedSite.getAddress().setId(site.getAddress().getId());
				siteRepo.updateSite(updatedSite);
			}

			mainLayout.showSiteList();
		} catch (InformationRequiredExceptionSite e)
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

	private void showError(String message)
	{
		errorLabel.setText(message);
	}

	private void resetErrorLabels()
	{
		errorLabel.setText("");
		siteNameError.setText("");
		employeeError.setText("");
		streetError.setText("");
		houseNumberError.setText("");
		postalCodeError.setText("");
		cityError.setText("");
		statusError.setText("");
	}

	private void handleInformationRequiredException(InformationRequiredExceptionSite e)
	{
		e.getInformationRequired().forEach((field, requiredElement) -> {
			String errorMessage = getErrorMessageForRequiredElement(requiredElement);
			showFieldError(field, errorMessage);
		});

	}

	private String getErrorMessageForRequiredElement(RequiredElementSite element)
	{
		switch (element)
		{
		case SITE_NAME_REQUIRED:
			return "Site naam is verplicht";
		case EMPLOYEE_REQUIRED:
			return "Verantwoordelijke is verplicht";
		case STREET_REQUIRED:
			return "Straat is verplicht";
		case NUMBER_REQUIRED:
			return "Huisnummer is verplicht";
		case POSTAL_CODE_REQUIRED:
			return "Postcode is verplicht";
		case CITY_REQUIRED:
			return "Stad is verplicht";
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
		case "siteName":
			siteNameError.setText(message);
			break;
		case "employee":
			employeeError.setText(message);
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
		case "status":
			statusError.setText(message);
			break;
		default:
			errorLabel.setText(message);
		}
	}

	private GridPane createSiteNameField()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel = new Label("Site naam");

		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0);

		siteNameField.setPrefWidth(200);

		pane.add(new Label("Site:"), 0, 1);
		pane.add(siteNameField, 1, 1);
		pane.add(siteNameError, 1, 2);

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

	private void initializeFields()
	{
		siteNameField = new TextField();
		streetField = new TextField();
		houseNumberField = new TextField();
		postalCodeField = new TextField();
		cityField = new TextField();
		employeeBox = new ComboBox<>();
		statusBox = new ComboBox<>();

		errorLabel = new Label();
		siteNameError = createErrorLabel();
		employeeError = createErrorLabel();
		streetError = createErrorLabel();
		houseNumberError = createErrorLabel();
		postalCodeError = createErrorLabel();
		cityError = createErrorLabel();
		statusError = createErrorLabel();
	}

	private Label createErrorLabel()
	{
		Label errorLabel = new Label();
		errorLabel.getStyleClass().add("error-label");
		return errorLabel;
	}

	private void fillSiteData(Site site)
	{
		siteNameField.setText(site.getSiteName());

		Address address = site.getAddress();
		if (address != null)
		{
			streetField.setText(address.getStreet());
			houseNumberField.setText(String.valueOf(address.getNumber()));
			postalCodeField.setText(String.valueOf(address.getPostalcode()));
			cityField.setText(address.getCity());
		}

		employeeBox.setValue(site.getVerantwoordelijke().getFullName());
		statusBox.setValue(site.getStatus());
	}

}
