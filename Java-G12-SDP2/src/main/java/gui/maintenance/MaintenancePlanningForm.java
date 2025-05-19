package gui.maintenance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.MachineController;
import domain.MaintenanceController;
import domain.UserController;
import dto.MachineDTO;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionMaintenance;
import gui.MainLayout;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import util.AuthenticationUtil;
import util.MaintenanceStatus;
import util.Role;

public class MaintenancePlanningForm extends GridPane
{

	private final MainLayout mainLayout;
	private final MachineDTO machineDTO;
	private final MachineController mc;
	private final MaintenanceController mntcc;
	private final UserController uc;

	private Label errorLabel;
	private Label startDateErrorLabel, endDateErrorLabel, machineErrorLabel, statusErrorLabel, reasonErrorLabel,
			technicianErrorLabel, executionDateErrorLabel;

	private TextField reasonField;

	private TextArea commentsField;

	private ComboBox<UserDTO> technicianComboBox;
	private ComboBox<LocalTime> startTimeField, endTimeField;
	private ComboBox<String> statusComboBox;
	private ComboBox<MachineDTO> machineComboBox;

	private DatePicker executionDatePicker;

	public MaintenancePlanningForm(MainLayout mainLayout, MachineDTO machineDTO)
	{
		this.mainLayout = mainLayout;
		this.machineDTO = machineDTO;
		this.mc = mainLayout.getServices().getMachineController();
		this.mntcc = mainLayout.getServices().getMaintenanceController();
		this.uc = mainLayout.getServices().getUserController();

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

	private VBox createTitleSection()
	{
		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.CENTER_LEFT);

		FontIcon icon = new FontIcon("fas-arrow-left");
		icon.setIconSize(20);
		Button backButton = new Button();
		backButton.setGraphic(icon);
		backButton.getStyleClass().add("back-button");
		backButton.setOnAction(e -> mainLayout.showMaintenanceList(machineDTO));
		this.add(backButton, 0, 0, 2, 1);

		Label title = new Label("Onderhoud inplannen");
		title.getStyleClass().add("title-label");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, title, spacer);

		return new VBox(10, hbox);
	}

	private HBox createSaveButton()
	{
		Button saveButton = new Button("Opslaan");
		saveButton.getStyleClass().add("save-button");
		saveButton.setOnAction(e -> {

			if (AuthenticationUtil.hasRole(Role.ADMINISTRATOR) || AuthenticationUtil.hasRole(Role.VERANTWOORDELIJKE))
			{
				savePlanning();
			} else
			{
				mainLayout.showNotAllowedAlert();
			}
		});

		saveButton.setPrefSize(300, 40);
		saveButton.setMaxWidth(Double.MAX_VALUE);

		HBox buttonBox = new HBox(saveButton);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));

		buttonBox.setMinWidth(800);
		buttonBox.setMaxWidth(800);

		return buttonBox;
	}

	private void savePlanning()
	{
		if (AuthenticationUtil.hasRole(Role.ADMINISTRATOR) || AuthenticationUtil.hasRole(Role.VERANTWOORDELIJKE))
		{
			try
			{
				resetErrorLabels();

				LocalDate execDate = executionDatePicker.getValue();
				LocalTime startTime = startTimeField.getValue();
				LocalTime endTime = endTimeField.getValue();

				// Convert to LocalDateTime
				LocalDateTime startDateTime = (execDate != null && startTime != null)
						? LocalDateTime.of(execDate, startTime)
						: null;
				LocalDateTime endDateTime = (execDate != null && endTime != null) ? LocalDateTime.of(execDate, endTime)
						: null;

				// Get IDs for technician and machine
				int technicianId = technicianComboBox.getValue() != null ? technicianComboBox.getValue().id() : 0;
				int machineId = machineComboBox.getValue() != null ? machineComboBox.getValue().id() : 0;

				// Get status
				MaintenanceStatus status = statusComboBox.getValue() != null
						? MaintenanceStatus.valueOf(statusComboBox.getValue())
						: null;

				// Use the controller to create the maintenance
				mntcc.createMaintenance(execDate, // execution date
						startDateTime, // start date and time
						endDateTime, // end date and time
						technicianId, // technician ID
						reasonField.getText(), // reason
						commentsField.getText(), // comments
						status, // status
						machineId // machine ID
				);

				mainLayout.showMaintenanceList(machineDTO);

			} catch (InformationRequiredExceptionMaintenance ex)
			{
				handleInformationRequiredException(ex);
			} catch (Exception ex)
			{
				errorLabel.setText("Er is een fout opgetreden: " + ex.getMessage());
				ex.printStackTrace();
			}
		} else
		{
			mainLayout.showNotAllowedAlert();
		}
	}

	private HBox createFormContent()
	{
		HBox formContent = new HBox(30);
		formContent.setAlignment(Pos.TOP_CENTER);
		formContent.getStyleClass().add("form-box");
		HBox.setHgrow(formContent, Priority.ALWAYS);

		VBox dataBox = new VBox(15, createDateSection());
		VBox technicianReasonBox = new VBox(15, createTechnicianReasonSection());
		VBox otherBox = new VBox(15, createOtherSection());

		VBox leftBox = new VBox(20);
		leftBox.setAlignment(Pos.TOP_LEFT);
		leftBox.setMinWidth(600);
		leftBox.setMaxWidth(800);

		leftBox.getChildren().addAll(dataBox, technicianReasonBox);

		VBox rightBox = new VBox(20);
		rightBox.setAlignment(Pos.TOP_LEFT);
		rightBox.setMinWidth(600);
		rightBox.setMaxWidth(800);

		rightBox.getChildren().addAll(otherBox);

		formContent.getChildren().addAll(leftBox, rightBox);

		return formContent;
	}

	private void initializeFields()
	{
		startDateErrorLabel = createErrorLabel();
		endDateErrorLabel = createErrorLabel();
		machineErrorLabel = createErrorLabel();
		statusErrorLabel = createErrorLabel();
		technicianErrorLabel = createErrorLabel();
		reasonErrorLabel = createErrorLabel();
		executionDateErrorLabel = createErrorLabel();
		errorLabel = createErrorLabel();

		reasonField = new TextField();
		reasonField.setPrefWidth(200);

		commentsField = new TextArea();

		technicianComboBox = new ComboBox<>();
		technicianComboBox.getItems().addAll(uc.getAllTechniekers());
		technicianComboBox.setPromptText("Selecteer technieker");
		technicianComboBox.setPrefWidth(200);

		startTimeField = new ComboBox<>();
		startTimeField.setPrefWidth(200);
		endTimeField = new ComboBox<>();
		endTimeField.setPrefWidth(200);

		startTimeField.setPromptText("Starttijd");
		endTimeField.setPromptText("Eindtijd");

		populateTimePicker(endTimeField);
		populateTimePicker(startTimeField);

		executionDatePicker = new DatePicker();
		executionDatePicker.setEditable(false);

		statusComboBox = new ComboBox<>();
		statusComboBox.setPrefWidth(200);
		machineComboBox = new ComboBox<>();
		machineComboBox.setPrefWidth(200);

		statusComboBox.getItems().addAll(Arrays.stream(MaintenanceStatus.values()).map(Enum::toString).toList());
		statusComboBox.setPromptText("Selecteer status");

		if (machineDTO != null)
		{
			machineComboBox.getItems().add(machineDTO);
			machineComboBox.setValue(machineDTO);
			machineComboBox.setDisable(true);
		} else
		{
			machineComboBox.setPromptText("Selecteer machine");
			List<MachineDTO> machines = mc.getMachineList();
			machineComboBox.getItems().addAll(machines);
		}
	}

	private GridPane createOtherSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		int row = 1;

		pane.add(new Label("Opmerkingen:"), 0, row);
		pane.add(commentsField, 1, row++);

		pane.add(new Label("Status:"), 0, row);
		pane.add(statusComboBox, 1, row++);
		pane.add(statusErrorLabel, 1, row++);

		pane.add(new Label("Machine:"), 0, row);
		pane.add(machineComboBox, 1, row++);
		pane.add(machineErrorLabel, 1, row++);

		return pane;
	}

	private GridPane createTechnicianReasonSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		int row = 1;

		pane.add(new Label("Technieker:"), 0, row);
		pane.add(technicianComboBox, 1, row++);
		pane.add(technicianErrorLabel, 1, row++);

		pane.add(new Label("Reden:"), 0, row);
		pane.add(reasonField, 1, row++);
		pane.add(reasonErrorLabel, 1, row++);

		return pane;
	}

	private GridPane createDateSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel = new Label("Datum en tijd");
		sectionLabel.getStyleClass().add("section-label");
		pane.add(sectionLabel, 0, 0, 2, 1);

		int row = 1;
		pane.add(new Label("Datum uitgevoerd:"), 0, row);
		pane.add(executionDatePicker, 1, row++);
		pane.add(executionDateErrorLabel, 1, row++);

		pane.add(new Label("Starttijdstip:"), 0, row);
		pane.add(startTimeField, 1, row++);
		pane.add(startDateErrorLabel, 1, row++);

		pane.add(new Label("Eindtijdstip:"), 0, row);
		pane.add(endTimeField, 1, row++);
		pane.add(endDateErrorLabel, 1, row++);

		return pane;
	}

	private void handleInformationRequiredException(InformationRequiredExceptionMaintenance e)
	{
		Map<String, Label> fieldToLabelMap = Map.of("executionDate", executionDateErrorLabel, "startDate",
				startDateErrorLabel, "endDate", endDateErrorLabel, "machine", machineErrorLabel, "status",
				statusErrorLabel, "reason", reasonErrorLabel, "technician", technicianErrorLabel);

		e.getInformationRequired().forEach((field, requiredElement) -> {
			String message = switch (requiredElement)
			{
			case EXECUTION_DATE_REQUIRED -> "Uitvoeringsdatum is verplicht";
			case START_DATE_REQUIRED -> "Starttijdstip is verplicht";
			case END_DATE_REQUIRED -> "Eindtijdstip is verplicht";
			case MACHINE_REQUIRED -> "Machine is verplicht";
			case MAINTENANCESTATUS_REQUIRED -> "Status is verplicht";
			case REASON_REQUIRED -> "Reden is verplicht";
			case TECHNICIAN_REQUIRED -> "Technieker is verplicht";
			case END_DATE_BEFORE_START -> "Eindtijd mag niet voor starttijd liggen";
			default -> "Verplicht veld";
			};

			Label label = fieldToLabelMap.get(field);
			if (label != null)
			{
				label.setText(message);
			} else
			{
				errorLabel.setText("Er is een fout opgetreden: " + message);
			}
		});
	}

	private void resetErrorLabels()
	{
		errorLabel.setText("");
		startDateErrorLabel.setText("");
		endDateErrorLabel.setText("");
		technicianErrorLabel.setText("");
		reasonErrorLabel.setText("");
		machineErrorLabel.setText("");
		statusErrorLabel.setText("");
		executionDateErrorLabel.setText("");
	}

	private Label createErrorLabel()
	{
		Label error = new Label();
		error.getStyleClass().add("error-label");
		return error;
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
				return time != null ? formatter.format(time) : "";
			}

			@Override
			public LocalTime fromString(String string)
			{
				return (string != null && !string.isEmpty()) ? LocalTime.parse(string, formatter) : null;
			}
		});
	}
}
