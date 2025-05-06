package gui.machine;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.machine.MachineBuilder;
import domain.machine.MachineController;
import domain.machine.MachineDTO;
import domain.site.Site;
import domain.site.SiteController;
import domain.user.User;
import domain.user.UserController;
import exceptions.InformationRequiredExceptionMachine;
import gui.MainLayout;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import util.MachineStatus;
import util.ProductionStatus;
import util.RequiredElementMachine;

public class AddOrEditMachineForm extends GridPane
{

	private MachineController machineController;
	private MachineDTO machineDTO;
	private SiteController siteController;
	private UserController userController;
	private final MainLayout mainLayout;

	private Label errorLabel;
	private Label codeError, locationError, productInfoError;
	private Label errorSite, errorTechnician, errorMachineStatus, errorProductionStatus;
	private Label errorFutureMaintenance;

	private TextField codeField, locationField, productInfoField;

	private ComboBox<Site> siteBox;
	private ComboBox<User> technicianBox;
	private ComboBox<MachineStatus> machineStatusBox;
	private ComboBox<ProductionStatus> productionStatusBox;

	private DatePicker futureMaintenance;

	private boolean isNewMachine;

	public AddOrEditMachineForm(MainLayout mainLayout, MachineDTO machineDTO)
	{
		this.mainLayout = mainLayout;
		this.machineController = mainLayout.getServices().getMachineController();
		this.machineDTO = machineDTO;
		this.siteController = mainLayout.getServices().getSiteController();
		this.userController = mainLayout.getServices().getUserController();
		this.isNewMachine = machineDTO == null;

		initializeFields();
		initializeGUI();

		if (!isNewMachine)
		{
			fillMachineData(machineDTO);
		}
	}

	private void initializeGUI()
	{
		this.getStylesheets().add(getClass().getResource("/css/form.css").toExternalForm());
		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(15);
		this.setPadding(new Insets(20));

		VBox mainContainer = new VBox(10);
		mainContainer.setAlignment(Pos.TOP_CENTER);
		mainContainer.setPadding(new Insets(10));

		// Create a scrollable container for the form content only
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(createFormContent());
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefViewportHeight(800); // or any height that works for your design
		scrollPane.getStyleClass().add("scroll-pane");
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		mainContainer.getChildren().addAll(createTitleSection(), errorLabel, scrollPane);

		this.add(mainContainer, 0, 0);

	}

	private VBox createFormContent()
	{
		VBox formContent = new VBox(30);
		formContent.setAlignment(Pos.TOP_CENTER);
		formContent.getStyleClass().add("form-box");

		VBox textFieldBox = new VBox(15, createTextFields());
		VBox datePickerBox = new VBox(15, createDatePicker());
		VBox infoBox = new VBox(15, createInfoBox());
		VBox statusBox = new VBox(15, createComboBoxSection());

		formContent.getChildren().addAll(textFieldBox, datePickerBox, infoBox, statusBox, createSaveButton());

		return formContent;
	}

	private Node createDatePicker()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel = new Label("Onderhoud");

		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0);

		codeField.setPrefWidth(200);

		int row = 1;

		pane.add(new Label("Volgend onderhoud:"), 0, row);
		pane.add(futureMaintenance, 1, row++);
		pane.add(errorFutureMaintenance, 1, row++);

		return pane;
	}

	private Node createTextFields()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel = new Label("Site info");

		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0);

		codeField.setPrefWidth(200);
		locationField.setPrefWidth(200);
		productInfoField.setPrefWidth(200);

		int row = 1;

		pane.add(new Label("Code:"), 0, row);
		pane.add(codeField, 1, row++);
		pane.add(codeError, 1, row++);

		pane.add(new Label("Locatie:"), 0, row);
		pane.add(locationField, 1, row++);
		pane.add(locationError, 1, row++);

		pane.add(new Label("Product info:"), 0, row);
		pane.add(productInfoField, 1, row++);
		pane.add(productInfoError, 1, row++);

		return pane;
	}

	private Node createInfoBox()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel1 = new Label("Site en technieker");
		sectionLabel1.getStyleClass().add("section-label");
		pane.add(sectionLabel1, 0, 0, 2, 1);

		siteBox = new ComboBox<>();
		siteBox.getItems().addAll(siteController.getSiteObjects());
		siteBox.setPromptText("Selecteer een site");
		siteBox.setPrefWidth(200);

		siteBox = new ComboBox<>();
		siteBox.getItems().addAll(siteController.getSiteObjects());
		siteBox.setPromptText("Selecteer een site");
		siteBox.setPrefWidth(200);

		// Customizing the ComboBox's display
		siteBox.setCellFactory(param -> new ListCell<Site>()
		{
			@Override
			protected void updateItem(Site item, boolean empty)
			{
				super.updateItem(item, empty);
				if (empty || item == null)
				{
					setText(null);
				} else
				{
					setText(item.getSiteName()); // Customize display format
				}
			}
		});

		siteBox.setButtonCell(new ListCell<Site>()
		{
			@Override
			protected void updateItem(Site item, boolean empty)
			{
				super.updateItem(item, empty);
				if (empty || item == null)
				{
					setText(null);
				} else
				{
					setText(item.getSiteName()); // Customize button cell format
				}
			}
		});

		technicianBox = new ComboBox<>();
		technicianBox.getItems().addAll(userController.getAllTechniekers());
		technicianBox.setPromptText("Selecteer een technieker");
		technicianBox.setPrefWidth(200);

		technicianBox.setCellFactory(param -> new ListCell<User>()
		{
			@Override
			protected void updateItem(User technician, boolean empty)
			{
				super.updateItem(technician, empty);
				if (empty || technician == null)
				{
					setText(null);
				} else
				{
					setText(technician.getFullName());
				}
			}
		});

		technicianBox.setButtonCell(new ListCell<User>()
		{
			@Override
			protected void updateItem(User technician, boolean empty)
			{
				super.updateItem(technician, empty);
				if (empty || technician == null)
				{
					setText(null);
				} else
				{
					setText(technician.getFullName());
				}
			}
		});

		int row = 1;
		pane.add(new Label("Site:"), 0, row);
		pane.add(siteBox, 1, row++);
		pane.add(errorSite, 1, row++);

		pane.add(new Label("Technieker:"), 0, row);
		pane.add(technicianBox, 1, row++);
		pane.add(errorTechnician, 1, row++);

		return pane;
	}

	private Node createComboBoxSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel = new Label("Statussen");
		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0, 2, 1);

		machineStatusBox = new ComboBox<>();
		machineStatusBox.getItems().addAll(MachineStatus.values());
		machineStatusBox.setPromptText("Selecteer een machinestatus");
		machineStatusBox.setPrefWidth(200);

		productionStatusBox = new ComboBox<>();
		productionStatusBox.getItems().addAll(ProductionStatus.values());
		productionStatusBox.setPromptText("Selecteer een productiestatus");
		productionStatusBox.setPrefWidth(200);

		int row = 1;

		pane.add(new Label("Machinestatus:"), 0, row);
		pane.add(machineStatusBox, 1, row++);
		pane.add(errorMachineStatus, 1, row++);

		pane.add(new Label("Productiestatus:"), 0, row);
		pane.add(productionStatusBox, 1, row++);
		pane.add(errorProductionStatus, 1, row++);

		return pane;
	}

	private HBox createSaveButton()
	{
		Button saveButton = new Button("Opslaan");
		saveButton.getStyleClass().add("save-button");
		saveButton.setOnAction(e -> saveMachine());

		HBox buttonBox = new HBox(saveButton);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));
		buttonBox.setMaxWidth(400);

		return buttonBox;
	}

	private void saveMachine()
	{
		resetErrorLabels();

		try
		{
			MachineBuilder machineBuilder = new MachineBuilder();
			machineBuilder.createMachine();

			// If editing an existing machine, set the ID
			if (!isNewMachine)
			{
				machineBuilder.buildId(machineDTO.id());
			}

			machineBuilder.buildSite(siteBox.getValue());
			machineBuilder.buildTechnician(technicianBox.getValue());
			machineBuilder.buildCode(codeField.getText());
			machineBuilder.buildStatusses(machineStatusBox.getValue(), productionStatusBox.getValue());
			machineBuilder.buildLocation(locationField.getText());
			machineBuilder.buildProductInfo(productInfoField.getText());
			machineBuilder.buildMaintenance(futureMaintenance.getValue());

			if (isNewMachine)
			{
				machineController.addNewMachine(machineBuilder.getMachine());
			} else
			{
				machineController.updateMachine(machineBuilder.getMachine());
			}

			mainLayout.showMachineScreen();
		} catch (InformationRequiredExceptionMachine e)
		{
			handleInformationRequiredException(e);
		} catch (Exception e)
		{
			showError("Er is een fout opgetreden: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void resetErrorLabels()
	{
		errorLabel.setText("");
		codeError.setText("");
		locationError.setText("");
		productInfoError.setText("");
		errorLabel.setText("");
		errorSite.setText("");
		errorTechnician.setText("");
		errorMachineStatus.setText("");
		errorProductionStatus.setText("");
		errorFutureMaintenance.setText("");
	}

	private void handleInformationRequiredException(InformationRequiredExceptionMachine e)
	{
		e.getInformationRequired().forEach((field, requiredElement) -> {
			String errorMessage = getErrorMessageForRequiredElement(requiredElement);
			showFieldError(field, errorMessage);
		});

	}

	private String getErrorMessageForRequiredElement(RequiredElementMachine element)
	{
		switch (element)
		{
		case CODE_REQUIRED:
			return "Code is verplicht";
		case MACHINESTATUS_REQUIRED:
			return "Machinestatus is verplicht";
		case PRODUCTIONSTATUS_REQUIRED:
			return "Productiestatus is verplicht";
		case LOCATION_REQUIRED:
			return "Locatie is verplicht";
		case PRODUCTINFO_REQUIRED:
			return "Product info is verplicht";
		case SITE_REQUIRED:
			return "Site is verplicht";
		case TECHNICIAN_REQUIRED:
			return "Technieker is verplicht";
		case FUTURE_MAINTENANCE_REQUIRED:
			return "Volgend onderhoud is verplicht";
		default:
			return "Verplicht veld";
		}
	}

	private void showFieldError(String fieldName, String message)
	{
		switch (fieldName)
		{
		case "code":
			codeError.setText(message);
			break;
		case "machineStatus":
			errorMachineStatus.setText(message);
			break;
		case "productionStatus":
			errorProductionStatus.setText(message);
			break;
		case "location":
			locationError.setText(message);
			break;
		case "productInfo":
			productInfoError.setText(message);
			break;
		case "site":
			errorSite.setText(message);
			break;
		case "technician":
			errorTechnician.setText(message);
			break;
		case "futureMaintenance":
			errorFutureMaintenance.setText(message);
			break;
		default:
			errorLabel.setText(message);
		}
	}

	private void showError(String message)
	{
		errorLabel.setText(message);
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
		backButton.setOnAction(e -> mainLayout.showMachineScreen());
		this.add(backButton, 0, 0, 2, 1);

		Label title = new Label(isNewMachine ? "Machine toevoegen" : "Machine aanpassen");
		title.getStyleClass().add("title-label");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, title, spacer);

		return new VBox(10, hbox);
	}

	private void fillMachineData(MachineDTO machineDTO)
	{
		codeField.setText(machineDTO.code());
		locationField.setText(machineDTO.location());
		productInfoField.setText(machineDTO.productInfo());
		siteBox.setValue(siteController.getSiteObject(machineDTO.site()));
		technicianBox.setValue(machineDTO.technician());
		machineStatusBox.setValue(machineDTO.machineStatus());
		productionStatusBox.setValue(machineDTO.productionStatus());
		futureMaintenance.setValue(machineDTO.futureMaintenance());
	}

	private void initializeFields()
	{
		codeField = new TextField();
		locationField = new TextField();
		productInfoField = new TextField();
		siteBox = new ComboBox<Site>();
		technicianBox = new ComboBox<User>();
		machineStatusBox = new ComboBox<MachineStatus>();
		productionStatusBox = new ComboBox<ProductionStatus>();
		futureMaintenance = new DatePicker();
		futureMaintenance.setEditable(false);

		errorLabel = createErrorLabel();
		codeError = createErrorLabel();
		locationError = createErrorLabel();
		productInfoError = createErrorLabel();
		errorSite = createErrorLabel();
		errorTechnician = createErrorLabel();
		errorMachineStatus = createErrorLabel();
		errorProductionStatus = createErrorLabel();
		errorFutureMaintenance = createErrorLabel();
	}

	private Label createErrorLabel()
	{
		Label errorLabel = new Label();
		errorLabel.getStyleClass().add("error-label");
		return errorLabel;
	}

}