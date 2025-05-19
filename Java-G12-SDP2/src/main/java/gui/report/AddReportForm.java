package gui.report;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.SiteController;
import domain.UserController;
import domain.maintenance.MaintenanceController;
import domain.report.ReportController;
import dto.MaintenanceDTO;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionReport;
import gui.MainLayout;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import util.RequiredElementReport;

public class AddReportForm extends GridPane
{
	private Label siteNameLabel, responsiblePersonLabel, maintenanceNumberLabel;
	private ComboBox<String> technicianComboBox;
	private DatePicker startDatePicker, endDatePicker;
	private ComboBox<LocalTime> startTimeField;
	private ComboBox<LocalTime> endTimeField;
	private TextField reasonField;
	private TextArea commentsArea;
	private Label technicianErrorLabel, startDateErrorLabel, startTimeErrorLabel, endDateErrorLabel, endTimeErrorLabel,
			reasonErrorLabel;
	private Label errorLabel;

	private MaintenanceDTO selectedMaintenanceDTO;

	private MaintenanceController mc;
	private ReportController rc;
	private SiteController sc;
	private UserController uc;

	private final MainLayout mainLayout;

	public AddReportForm(MainLayout mainLayout, MaintenanceDTO maintenanceDTO)
	{
		this.mc = mainLayout.getServices().getMaintenanceController();
		this.rc = mainLayout.getServices().getReportController();
		this.sc = mainLayout.getServices().getSiteController();
		this.uc = mainLayout.getServices().getUserController();

		this.mainLayout = mainLayout;
		this.selectedMaintenanceDTO = maintenanceDTO;

		initializeFields();
		buildGUI();

		if (maintenanceDTO == null)
		{
			mainLayout.showHomeScreen();
			throw new IllegalArgumentException("Het onderhoud is ongeldig");
		}
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

		VBox mainContainer = new VBox();
		mainContainer.setAlignment(Pos.CENTER);
		mainContainer.setPadding(new Insets(10));
		mainContainer.getChildren().addAll(createTitleSection(), errorLabel, formAndSaveButton);

		this.add(mainContainer, 0, 0);
	}

	private HBox createFormContent()
	{
		HBox formContent = new HBox(30);
		formContent.setAlignment(Pos.TOP_CENTER);
		formContent.getStyleClass().add("form-box");

		VBox informationBox = new VBox(15, createInformationBox());
		VBox technicianBox = new VBox(15, createTechnicianBox());
		VBox datesBox = new VBox(15, createDatesBox());
		VBox lowBox = new VBox(15, createLowBox());

		VBox leftBox = new VBox(20);
		leftBox.setAlignment(Pos.TOP_LEFT);
		leftBox.setMinWidth(400);
		leftBox.setMaxWidth(600);

		leftBox.getChildren().addAll(informationBox, technicianBox);

		VBox rightBox = new VBox(20);
		rightBox.setAlignment(Pos.TOP_LEFT);
		rightBox.setMinWidth(400);
		rightBox.setMaxWidth(600);

		rightBox.getChildren().addAll(datesBox, lowBox);

		formContent.getChildren().addAll(leftBox, rightBox);

		return formContent;
	}

	private Node createInformationBox()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		String labelString = "Onderhoudsgegevens";
		Label sectionLabel = new Label(labelString);
		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0, 2, 1);

		int row = 1;

		pane.add(new Label("Site:"), 0, row);
		pane.add(siteNameLabel, 1, row++);

		pane.add(new Label("Verantwoordelijke:"), 0, row);
		pane.add(responsiblePersonLabel, 1, row++);

		pane.add(new Label("Onderhoudsnummer:"), 0, row);
		pane.add(maintenanceNumberLabel, 1, row++);

		return pane;
	}

	private Node createTechnicianBox()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		String labelString = "Rapport informatie";
		Label sectionLabel = new Label(labelString);
		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0, 2, 1);

		technicianComboBox = new ComboBox<>();
		technicianComboBox.getItems()
				.addAll(uc.getAllTechniekers().stream().map(user -> user.firstName()).collect(Collectors.toList()));

		technicianComboBox.setPromptText("Selecteer een technieker");
		technicianComboBox.setPrefWidth(200);

		int row = 1;
		pane.add(new Label("Technieker:"), 0, row);
		pane.add(technicianComboBox, 1, row++);
		pane.add(technicianErrorLabel, 1, row++);

		return pane;
	}

	private Node createDatesBox()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		String labelString = "Data";
		Label sectionLabel = new Label(labelString);
		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0, 2, 1);

		int row = 1;
		pane.add(new Label("Startdatum:"), 0, row);
		pane.add(startDatePicker, 1, row++);
		pane.add(startDateErrorLabel, 1, row++);

		pane.add(new Label("Starttijd:"), 0, row);
		pane.add(startTimeField, 1, row++);
		pane.add(startTimeErrorLabel, 1, row++);

		pane.add(new Label("Einddatum:"), 0, row);
		pane.add(endDatePicker, 1, row++);
		pane.add(endDateErrorLabel, 1, row++);

		pane.add(new Label("Eindtijd:"), 0, row);
		pane.add(endTimeField, 1, row++);
		pane.add(endTimeErrorLabel, 1, row++);

		return pane;
	}

	private Node createLowBox()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		int row = 1;

		pane.add(new Label("Reden:"), 0, row);
		pane.add(reasonField, 1, row++);
		pane.add(reasonErrorLabel, 1, row++);

		pane.add(new Label("Opmerkingen:"), 0, row);
		pane.add(commentsArea, 1, row++);

		return pane;
	}

	private HBox createSaveButton()
	{
		Button saveButton = new Button("Opslaan");
		saveButton.getStyleClass().add("save-button");
		saveButton.setOnAction(e -> createReport());

		saveButton.setPrefSize(300, 40);
		saveButton.setMaxWidth(Double.MAX_VALUE);

		HBox buttonBox = new HBox(saveButton);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));

		buttonBox.setMinWidth(800);
		buttonBox.setMaxWidth(800);

		return buttonBox;
	}

	private void initializeFields()
	{
		siteNameLabel = new Label(selectedMaintenanceDTO.machine().site().siteName());
		siteNameLabel.getStyleClass().add("info-value");

		responsiblePersonLabel = new Label(selectedMaintenanceDTO.technician().firstName());
		responsiblePersonLabel.getStyleClass().add("info-value");

		maintenanceNumberLabel = new Label("" + selectedMaintenanceDTO.id());
		maintenanceNumberLabel.getStyleClass().add("info-value");

		technicianComboBox = new ComboBox<>();
		technicianComboBox.setPromptText("Selecteer een technieker");

		startDatePicker = new DatePicker();
		startDatePicker.setPromptText("Kies startdatum");

		startTimeField = new ComboBox<>();
		startTimeField.setPromptText("Kies starttijd");
		populateTimePicker(startTimeField);

		endDatePicker = new DatePicker();
		endDatePicker.setPromptText("Kies einddatum");

		endTimeField = new ComboBox<>();
		endTimeField.setPromptText("Kies eindtijd");
		populateTimePicker(endTimeField);

		reasonField = new TextField();
		reasonField.setPromptText("Voer reden in");

		commentsArea = new TextArea();
		commentsArea.setPrefRowCount(5);
		commentsArea.setWrapText(true);
		commentsArea.setPromptText("Voer eventuele opmerkingen in");

		errorLabel = createErrorLabel();
		technicianErrorLabel = createErrorLabel();
		startDateErrorLabel = createErrorLabel();
		startTimeErrorLabel = createErrorLabel();
		endDateErrorLabel = createErrorLabel();
		endTimeErrorLabel = createErrorLabel();
		reasonErrorLabel = createErrorLabel();
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
		backButton.setOnAction(e -> mainLayout.showMaintenanceList());
		this.add(backButton, 0, 0, 2, 1);

		Label title = new Label("Rapport aanmaken");
		title.getStyleClass().add("title-label");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, title, spacer);

		return new VBox(10, hbox);
	}

	private void populateTimePicker(ComboBox<LocalTime> timePicker)
	{
		LocalTime time = LocalTime.of(0, 0);
		while (time.isBefore(LocalTime.of(23, 45)))
		{
			timePicker.getItems().add(time);
			time = time.plusMinutes(15);
		}

		timePicker.setConverter(new javafx.util.StringConverter<>()
		{
			private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

			@Override
			public String toString(LocalTime time)
			{
				if (time != null)
				{
					return formatter.format(time);
				}
				return "";
			}

			@Override
			public LocalTime fromString(String string)
			{
				if (string != null && !string.isEmpty())
				{
					return LocalTime.parse(string, formatter);
				}
				return null;
			}
		});
	}

	private void createReport()
	{
		resetErrorLabels();
		try
		{
			UserDTO selectedTechnician = null;
			if (technicianComboBox.getValue() != null)
			{
				selectedTechnician = uc.getAllTechniekers().stream()
						.filter(user -> user.firstName().equals(technicianComboBox.getValue())).findFirst()
						.orElse(null);
			}

			rc.createReport(mc.getMaintenance(selectedMaintenanceDTO.id()), selectedTechnician,
					startDatePicker.getValue(), startTimeField.getValue(), endDatePicker.getValue(),
					endTimeField.getValue(), reasonField.getText().trim(), commentsArea.getText().trim(),
					selectedMaintenanceDTO.machine().site());

			mainLayout.showMaintenanceDetails(selectedMaintenanceDTO);
		} catch (InformationRequiredExceptionReport e)
		{
			handleInformationRequiredException(e);
		} catch (Exception e)
		{
			e.printStackTrace();
			showError("Er is een fout opgetreden: " + e.getMessage());
		}
	}

	private void handleInformationRequiredException(InformationRequiredExceptionReport e)
	{
		e.getMissingElements().forEach((field, requiredElement) ->
		{
			String errorMessage = getErrorMessageForRequiredElement(requiredElement);
			showFieldError(field, errorMessage);
		});
	}

	private String getErrorMessageForRequiredElement(RequiredElementReport element)
	{
		switch (element)
		{
		case MAINTENANCE_REQUIRED:
			return "Onderhoud is verplicht";
		case TECHNICIAN_REQUIRED:
			return "Technieker is verplicht";
		case STARTDATE_REQUIRED:
			return "Startdatum is verplicht";
		case STARTTIME_REQUIRED:
			return "Starttijd is verplicht";
		case ENDDATE_REQUIRED:
			return "Einddatum is verplicht";
		case ENDTIME_REQUIRED:
			return "Eindtijd is verplicht";
		case REASON_REQUIRED:
			return "Reden is verplicht";
		case SITE_REQUIRED:
			return "Site is verplicht";
		case END_DATE_BEFORE_START:
			return "Einddatum mag niet voor startdatum liggen";
		case END_TIME_BEFORE_START:
			return "Eindtijd mag niet voor starttijd liggen";
		default:
			return "Verplicht veld";
		}
	}

	private void showFieldError(String fieldName, String message)
	{
		switch (fieldName)
		{
		case "technician":
			technicianErrorLabel.setText(message);
			break;
		case "startDate":
			startDateErrorLabel.setText(message);
			break;
		case "startTime":
			startTimeErrorLabel.setText(message);
			break;
		case "endDate":
			endDateErrorLabel.setText(message);
			break;
		case "endTime":
			endTimeErrorLabel.setText(message);
			break;
		case "reason":
			reasonErrorLabel.setText(message);
			break;
		default:
			errorLabel.setText(message);
		}
	}

	private void showError(String message)
	{
		errorLabel.setText(message);
	}

	private void resetErrorLabels()
	{
		errorLabel.setText("");
		technicianErrorLabel.setText("");
		startDateErrorLabel.setText("");
		startTimeErrorLabel.setText("");
		endDateErrorLabel.setText("");
		endTimeErrorLabel.setText("");
		reasonErrorLabel.setText("");
	}

	private Label createErrorLabel()
	{
		Label errorLabel = new Label();
		errorLabel.getStyleClass().add("error-label");
		return errorLabel;
	}
}