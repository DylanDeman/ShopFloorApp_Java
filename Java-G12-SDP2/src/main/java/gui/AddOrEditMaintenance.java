package gui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dto.MachineDTO;
import dto.MaintenanceDTO;
import dto.UserDTO;
import exceptions.InformationRequired;
import exceptions.InformationRequiredExceptionMaintenance;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import util.AuthenticationUtil;
import util.MaintenanceStatus;
import util.Role;

public class AddOrEditMaintenance extends AddOrEditAbstract
{
	private final MachineDTO machineDTO;
	private final MaintenanceDTO maintenanceDTO;

	private Label startDateErrorLabel, endDateErrorLabel, machineErrorLabel, statusErrorLabel, reasonErrorLabel,
			technicianErrorLabel, executionDateErrorLabel;

	private TextField reasonField;
	private TextArea commentsField;

	private ComboBox<String> technicianComboBox;
	private ComboBox<LocalTime> startTimeField, endTimeField;
	private ComboBox<String> statusComboBox;
	private ComboBox<String> machineComboBox;
	private Map<String, UserDTO> technicianMap;
	private Map<String, MachineDTO> machineMap;

	private DatePicker executionDatePicker;

	public AddOrEditMaintenance(MainLayout mainLayout, MaintenanceDTO maintenanceDTO, MachineDTO machineDTO)
	{
		super(mainLayout, false);

		this.machineDTO = machineDTO;
		this.maintenanceDTO = maintenanceDTO;
	}

	public AddOrEditMaintenance(MainLayout mainLayout, MachineDTO machineDTO)
	{
		super(mainLayout, true);

		this.machineDTO = machineDTO;
		this.maintenanceDTO = null;
	}

	@Override
	protected void initializeFields()
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

		List<UserDTO> technicians = userController.getAllTechniekers();

		technicianMap = technicians.stream()
				.collect(Collectors.toMap(t -> String.format("%d %s %s", t.id(), t.firstName(), t.lastName()), t -> t));

		technicianComboBox = new ComboBox<>();
		technicianComboBox.getItems().addAll(technicianMap.keySet());
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

		List<String> statusses = Arrays.stream(MaintenanceStatus.values()).map(Enum::toString).toList();
		if (AuthenticationUtil.hasRole(Role.TECHNIEKER))
		{
			statusses = Arrays.stream(MaintenanceStatus.values()).filter(s -> !s.equals(MaintenanceStatus.INGEPLAND))
					.map(Enum::toString).toList();
		}

		statusComboBox.getItems().addAll(statusses);
		statusComboBox.setPromptText("Selecteer status");

		List<MachineDTO> machines = machineController.getMachineList();
		machineMap = machines.stream()
				.collect(Collectors.toMap(m -> String.format("%d Machine %s", m.id(), m.code()), m -> m));

		machineComboBox.getItems().addAll(machineMap.keySet());
		machineComboBox.setPromptText("Selecteer machine");
		machineComboBox.setPrefWidth(200);

		if (machineDTO != null)
		{
			String key = String.format("%d Machine %s", machineDTO.id(), machineDTO.code());
			machineComboBox.setValue(key);
			machineComboBox.setDisable(true);
		}
	}

	@Override
	protected VBox createLeftBox()
	{
		VBox leftBox = new VBox(20);
		leftBox.setAlignment(Pos.TOP_LEFT);
		leftBox.setMinWidth(600);
		leftBox.setMaxWidth(800);

		VBox dataBox = new VBox(15, createDateSection());
		VBox technicianReasonBox = new VBox(15, createTechnicianReasonSection());

		leftBox.getChildren().addAll(dataBox, technicianReasonBox);

		return leftBox;
	}

	@Override
	protected VBox createRightBox()
	{
		VBox rightBox = new VBox(20);
		rightBox.setAlignment(Pos.TOP_LEFT);
		rightBox.setMinWidth(600);
		rightBox.setMaxWidth(800);

		VBox otherBox = new VBox(15, createOtherSection());

		rightBox.getChildren().addAll(otherBox);

		return rightBox;
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

	@Override
	protected void fillData()
	{
		Platform.runLater(() -> {
			if (maintenanceDTO.executionDate() != null)
			{
				executionDatePicker.setValue(maintenanceDTO.executionDate());
			}

			if (maintenanceDTO.startDate() != null)
			{
				startTimeField.setValue(maintenanceDTO.startDate().toLocalTime());
			}

			if (maintenanceDTO.endDate() != null)
			{
				endTimeField.setValue(maintenanceDTO.endDate().toLocalTime());
			}

			if (maintenanceDTO.technician() != null)
			{
				String key = String.format("%d %s %s", maintenanceDTO.technician().id(),
						maintenanceDTO.technician().firstName(), maintenanceDTO.technician().lastName());
				if (technicianMap.containsKey(key))
				{
					technicianComboBox.setValue(key);
				}
			}

			reasonField.setText(maintenanceDTO.reason() != null ? maintenanceDTO.reason() : "");
			commentsField.setText(maintenanceDTO.comments() != null ? maintenanceDTO.comments() : "");

			if (maintenanceDTO.status() != null)
			{
				statusComboBox.setValue(maintenanceDTO.status().toString());
			}

			if (maintenanceDTO.machine() != null)
			{
				String key = String.format("%d Machine %s", maintenanceDTO.machine().id(),
						maintenanceDTO.machine().code());
				if (machineMap.containsKey(key))
				{
					machineComboBox.setValue(key);
				}
			}
		});
	}

	@Override
	protected void save()
	{
		if (AuthenticationUtil.hasRole(Role.ADMINISTRATOR) || AuthenticationUtil.hasRole(Role.VERANTWOORDELIJKE)
				|| AuthenticationUtil.hasRole(Role.TECHNIEKER))
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
				String selectedTechnician = technicianComboBox.getValue();
				int technicianId = selectedTechnician != null && technicianMap.containsKey(selectedTechnician)
						? technicianMap.get(selectedTechnician).id()
						: 0;
				String selectedMachine = machineComboBox.getValue();
				int machineId = selectedMachine != null && machineMap.containsKey(selectedMachine)
						? machineMap.get(selectedMachine).id()
						: 0;

				// Get status
				MaintenanceStatus status = statusComboBox.getValue() != null
						? Arrays.stream(MaintenanceStatus.values())
								.filter(s -> s.toString().equals(statusComboBox.getValue())).findFirst().orElse(null)
						: null;

				// Use the controller to create or update the maintenance
				if (isNew)
				{
					maintenanceController.createMaintenance(execDate, startDateTime, endDateTime, technicianId,
							reasonField.getText(), commentsField.getText(), status, machineId);
				} else
				{
					maintenanceController.updateMaintenance(maintenanceDTO.id(), execDate, startDateTime, endDateTime,
							technicianId, reasonField.getText(), commentsField.getText(), status, machineId);
				}

				navigateBack();

			} catch (InformationRequiredExceptionMaintenance ex)
			{
				handleInformationRequiredException(ex);
			} catch (Exception ex)
			{
				showError("Er is een fout opgetreden: " + ex.getMessage());
				ex.printStackTrace();
			}
		} else
		{
			mainLayout.showNotAllowedAlert();
		}
	}

	@Override
	protected void navigateBack()
	{
		mainLayout.showMaintenanceList(machineDTO);
	}

	@Override
	protected String getTitleText()
	{
		return "Onderhoud inplannen";
	}

	@Override
	protected void handleInformationRequiredException(Exception e)
	{
		if (e instanceof InformationRequired)
		{
			InformationRequired exception = (InformationRequired) e;
			exception.getRequiredElements().forEach((field, requiredElement) -> {
				String errorMessage = requiredElement.getMessage();
				showFieldError(field, errorMessage);
			});
		}
	}

	@Override
	protected void showFieldError(String fieldName, String message)
	{
		// Deze methode wordt niet gebruikt omdat we de
		// handleInformationRequiredException
		// methode hebben overschreven met een eigen implementatie die veldspecifieke
		// labels gebruikt
	}

	@Override
	protected void resetErrorLabels()
	{
		super.resetErrorLabels();
		startDateErrorLabel.setText("");
		endDateErrorLabel.setText("");
		technicianErrorLabel.setText("");
		reasonErrorLabel.setText("");
		machineErrorLabel.setText("");
		statusErrorLabel.setText("");
		executionDateErrorLabel.setText("");
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