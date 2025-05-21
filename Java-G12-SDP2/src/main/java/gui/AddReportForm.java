package gui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import dto.MaintenanceDTO;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;
import exceptions.InformationRequired;
import exceptions.InformationRequiredExceptionReport;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AddReportForm extends AddOrEditAbstract
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

	private MaintenanceDTO maintenanceDTO;

	public AddReportForm(MainLayout mainLayout, MaintenanceDTO maintenanceDTO)
	{
		super(mainLayout, true);
		this.maintenanceDTO = maintenanceDTO;

		if (maintenanceDTO == null)
		{
			mainLayout.showHomeScreen();
			throw new IllegalArgumentException("Het onderhoud is ongeldig");
		}
	}

	@Override
	protected void initializeFields()
	{
		siteNameLabel = new Label(maintenanceDTO != null ? maintenanceDTO.machine().site().siteName() : "");
		siteNameLabel.getStyleClass().add("info-value");

		responsiblePersonLabel = new Label(maintenanceDTO != null ? maintenanceDTO.technician().firstName() : "");
		responsiblePersonLabel.getStyleClass().add("info-value");

		maintenanceNumberLabel = new Label(maintenanceDTO != null ? "" + maintenanceDTO.id() : "");
		maintenanceNumberLabel.getStyleClass().add("info-value");

		technicianComboBox = new ComboBox<>();
		technicianComboBox.setPromptText("Selecteer een technieker");
		technicianComboBox.getItems().addAll(
				userController.getAllTechniekers().stream().map(user -> user.firstName()).collect(Collectors.toList()));

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

	@Override
	protected VBox createLeftBox()
	{
		VBox leftBox = new VBox(20);
		leftBox.setAlignment(Pos.TOP_LEFT);
		leftBox.setMinWidth(400);
		leftBox.setMaxWidth(600);

		leftBox.getChildren().addAll(createInformationBox(), createTechnicianBox());

		return leftBox;
	}

	@Override
	protected VBox createRightBox()
	{
		VBox rightBox = new VBox(20);
		rightBox.setAlignment(Pos.TOP_LEFT);
		rightBox.setMinWidth(400);
		rightBox.setMaxWidth(600);

		rightBox.getChildren().addAll(createDatesBox(), createLowBox());

		return rightBox;
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

	@Override
	protected void fillData()
	{
		// Rapporten hebben geen bestaande gegevens om te vullen
		// Deze methode is leeg omdat rapporten altijd nieuw zijn
	}

	@Override
	protected void save()
	{
		resetErrorLabels();
		try
		{
			UserDTO selectedTechnician = null;
			if (technicianComboBox.getValue() != null)
			{
				selectedTechnician = userController.getAllTechniekers().stream()
						.filter(user -> user.firstName().equals(technicianComboBox.getValue())).findFirst()
						.orElse(null);
			}

			SiteDTOWithoutMachines siteWoMachines = maintenanceDTO.machine().site();
			MaintenanceDTO maintenance = maintenanceController.getMaintenanceDTO(maintenanceDTO.id());
			UserDTO technician = selectedTechnician != null ? userController.getUserById(selectedTechnician.id())
					: null;

			reportController.createReport(siteWoMachines, maintenance, technician, startDatePicker.getValue(),
					startTimeField.getValue(), endDatePicker.getValue(), endTimeField.getValue(),
					reasonField.getText().trim(), commentsArea.getText().trim());

			navigateBack();
		} catch (InformationRequiredExceptionReport e)
		{
			handleInformationRequiredException(e);
		} catch (Exception e)
		{
			e.printStackTrace();
			showError("Er is een fout opgetreden: " + e.getMessage());
		}
	}

	@Override
	protected void navigateBack()
	{
		mainLayout.showMaintenanceDetails(maintenanceDTO);
	}

	@Override
	protected String getTitleText()
	{
		return "Rapport aanmaken";
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

}