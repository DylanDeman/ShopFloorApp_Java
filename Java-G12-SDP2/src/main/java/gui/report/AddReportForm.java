package gui.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import domain.maintenance.MaintenanceDTO;
import domain.report.Report;
import domain.report.ReportController;
import domain.user.User;
import gui.ChoicePane;
import gui.customComponents.CustomButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddReportForm extends BorderPane
{
	private Label siteNameLabel, responsiblePersonLabel, maintenanceNumberLabel, titleLabel;
	private ComboBox<String> technicianComboBox;
	private DatePicker startDatePicker, endDatePicker;
	private TextField startTimeField, endTimeField;
	private TextField reasonField;
	private TextArea commentsArea;
	private Label technicianErrorLabel, startDateErrorLabel, startTimeErrorLabel, endDateErrorLabel, endTimeErrorLabel,
			reasonErrorLabel;
	private Label generalMessageLabel;
	private VBox formBox, headerBox;
	private GridPane formGridPane;
	private MaintenanceDTO selectedMaintenanceDTO;
	private ReportController reportController;

	public AddReportForm(Stage primaryStage, MaintenanceDTO maintenanceDTO)
	{
		this.getStyleClass().add("main-pane");

		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		this.setBackground(new Background(backgroundImage));

		reportController = new ReportController();

		if (maintenanceDTO == null)
		{
			returnToChoicePane(primaryStage);
			throw new IllegalArgumentException("Het onderhoud is ongeldig");
		}

		this.selectedMaintenanceDTO = maintenanceDTO;

		// Header
		titleLabel = new Label("Rapport aanmaken");
		titleLabel.getStyleClass().add("header-label");

		// Form
		formGridPane = new GridPane();
		formGridPane.setHgap(10);
		formGridPane.setVgap(5);
		formGridPane.setPadding(new Insets(10));

		siteNameLabel = new Label("TEST SITE");
		responsiblePersonLabel = new Label(maintenanceDTO.technician().getFullName());
		maintenanceNumberLabel = new Label("" + maintenanceDTO.id());

		technicianComboBox = new ComboBox<>();
		startDatePicker = new DatePicker();
		startTimeField = new TextField();
		endDatePicker = new DatePicker();
		endTimeField = new TextField();
		reasonField = new TextField();
		commentsArea = new TextArea();
		commentsArea.setPrefRowCount(5);
		commentsArea.setWrapText(true);

		// Errors
		technicianErrorLabel = createErrorLabel();
		startDateErrorLabel = createErrorLabel();
		startTimeErrorLabel = createErrorLabel();
		endDateErrorLabel = createErrorLabel();
		endTimeErrorLabel = createErrorLabel();
		reasonErrorLabel = createErrorLabel();
		generalMessageLabel = new Label();
		generalMessageLabel.setVisible(false);
		generalMessageLabel.getStyleClass().add("error-label");

		// Fill Form
		int row = 0;

		// Add site, responsible person, maintenance number fields to form with same
		// spacing
		formGridPane.add(createLabeledField("Site:", siteNameLabel), 0, row++, 2, 1);
		formGridPane.add(createLabeledField("Verantwoordelijke:", responsiblePersonLabel), 0, row++, 2, 1);
		formGridPane.add(createLabeledField("Onderhoudsnummer:", maintenanceNumberLabel), 0, row++, 2, 1);

		// Add technician combo box
		formGridPane.add(createLabeledField("Technieker:", technicianComboBox), 0, row++, 2, 1);
		formGridPane.add(technicianErrorLabel, 0, row++, 2, 1);

		// Date and time fields together
		HBox startDateTimeBox = new HBox(10, createLabeledField("Startdatum:", startDatePicker),
				createLabeledField("Starttijd (HH:MM):", startTimeField));
		formGridPane.add(startDateTimeBox, 0, row++, 2, 1);
		formGridPane.add(startDateErrorLabel, 0, row++, 2, 1);
		formGridPane.add(startTimeErrorLabel, 0, row++, 2, 1);

		HBox endDateTimeBox = new HBox(10, createLabeledField("Einddatum:", endDatePicker),
				createLabeledField("Eindtijd (HH:MM):", endTimeField));
		formGridPane.add(endDateTimeBox, 0, row++, 2, 1);
		formGridPane.add(endDateErrorLabel, 0, row++, 2, 1);
		formGridPane.add(endTimeErrorLabel, 0, row++, 2, 1);

		formGridPane.add(createLabeledField("Reden:", reasonField), 0, row++, 2, 1);
		formGridPane.add(reasonErrorLabel, 0, row++, 2, 1);

		formGridPane.add(createLabeledField("Opmerkingen:", commentsArea), 0, row++, 2, 1);

		// Form Container
		formBox = new VBox(15, formGridPane);
		formBox.getStyleClass().add("form-box");
		formBox.setAlignment(Pos.TOP_CENTER);
		formBox.setMaxWidth(900);
		formBox.setMinWidth(400);

		// Button
		FontIcon saveIcon = new FontIcon(BootstrapIcons.SAVE);
		CustomButton createReportBtn = new CustomButton(saveIcon, "Rapport aanmaken");
		createReportBtn.getStyleClass().add("create-report-button");
		createReportBtn.setPadding(new Insets(10, 30, 10, 30));
		createReportBtn.setOnAction(e -> createReport());

		HBox buttonBox = new HBox(createReportBtn);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));

		VBox content = new VBox(10, titleLabel, generalMessageLabel, formBox, buttonBox);
		content.getStyleClass().add("content-container");
		content.setAlignment(Pos.TOP_CENTER);
		content.setPadding(new Insets(20));

		this.widthProperty().addListener((obs, oldVal, newVal) -> adjustFormLayout(newVal.doubleValue()));

		this.setTop(headerBox);
		this.setCenter(content);

		loadTechnicians();
	}

	private void adjustFormLayout(double width)
	{
		if (width < 700)
		{
			formBox.setPadding(new Insets(15));
		} else
		{
			formBox.setPadding(new Insets(30));
		}
	}

	private void createReport()
	{
		try
		{
			clearAllErrorMessages();

			boolean hasValidationErrors = validateFields();
			if (hasValidationErrors)
			{
				return;
			}

			User selectedTechnician = reportController.getTechnicians().stream()
					.filter(user -> user.getFullName().equals(technicianComboBox.getValue())).findFirst().orElse(null);

			String reason = reasonField.getText().trim();
			String comments = commentsArea.getText().trim();
			LocalDate startDate = startDatePicker.getValue();
			LocalTime startTime = parseTime(startTimeField.getText());
			LocalDate endDate = endDatePicker.getValue();
			LocalTime endTime = parseTime(endTimeField.getText());

			Report report = new Report(selectedMaintenanceDTO, selectedTechnician, startDate, startTime, endDate,
					endTime, reason, comments);
			reportController.createReport(report, selectedMaintenanceDTO);

			showSuccess("Rapport succesvol aangemaakt!");
			clearUserInputFields();

		} catch (Exception e)
		{
			showGeneralError("Er is een fout opgetreden: " + e.getMessage());
		}
	}

	private boolean validateFields()
	{
		boolean hasErrors = false;

		if (technicianComboBox.getValue() == null)
		{
			technicianErrorLabel.setVisible(true);
			hasErrors = true;
		}

		if (startDatePicker.getValue() == null)
		{
			startDateErrorLabel.setVisible(true);
			hasErrors = true;
		}

		if (startTimeField.getText() == null || startTimeField.getText().trim().isEmpty())
		{
			startTimeErrorLabel.setVisible(true);
			hasErrors = true;
		} else
		{
			try
			{
				parseTime(startTimeField.getText());
			} catch (DateTimeParseException e)
			{
				startTimeErrorLabel.setText("Ongeldige tijd. Gebruik HH:MM format.");
				startTimeErrorLabel.setVisible(true);
				hasErrors = true;
			}
		}

		if (endDatePicker.getValue() == null)
		{
			endDateErrorLabel.setVisible(true);
			hasErrors = true;
		}

		if (endTimeField.getText() == null || endTimeField.getText().trim().isEmpty())
		{
			endTimeErrorLabel.setVisible(true);
			hasErrors = true;
		} else
		{
			try
			{
				parseTime(endTimeField.getText());
			} catch (DateTimeParseException e)
			{
				endTimeErrorLabel.setText("Ongeldige tijd. Gebruik HH:MM format.");
				endTimeErrorLabel.setVisible(true);
				hasErrors = true;
			}
		}

		if (reasonField.getText() == null || reasonField.getText().trim().isEmpty())
		{
			reasonErrorLabel.setVisible(true);
			hasErrors = true;
		}

		return hasErrors;
	}

	private void clearAllErrorMessages()
	{
		technicianErrorLabel.setVisible(false);
		startDateErrorLabel.setVisible(false);
		startTimeErrorLabel.setVisible(false);
		endDateErrorLabel.setVisible(false);
		endTimeErrorLabel.setVisible(false);
		reasonErrorLabel.setVisible(false);
		generalMessageLabel.setVisible(false);
	}

	private LocalTime parseTime(String timeStr)
	{
		if (timeStr == null || timeStr.trim().isEmpty())
		{
			throw new DateTimeParseException("Tijd kan niet leeg zijn", timeStr, 0);
		}
		return LocalTime.parse(timeStr.trim(), DateTimeFormatter.ofPattern("HH:mm"));
	}

	private void showGeneralError(String message)
	{
		generalMessageLabel.setText(message);
		generalMessageLabel.setStyle("-fx-text-fill: red;");
		generalMessageLabel.setVisible(true);
	}

	private void showSuccess(String message)
	{
		generalMessageLabel.setText(message);
		generalMessageLabel.setStyle("-fx-text-fill: green;");
		generalMessageLabel.setVisible(true);
	}

	private void clearUserInputFields()
	{
		technicianComboBox.setValue(null);
		startDatePicker.setValue(null);
		startTimeField.clear();
		endDatePicker.setValue(null);
		endTimeField.clear();
		reasonField.clear();
		commentsArea.clear();
	}

	private void loadTechnicians()
	{
		List<String> technicianNames = reportController.getTechnicians().stream().map(User::getFullName)
				.collect(Collectors.toList());
		technicianComboBox.getItems().addAll(technicianNames);
	}

	private void returnToChoicePane(Stage primaryStage)
	{
		Scene scene = new Scene(new ChoicePane(primaryStage), 800, 600);
		scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
		primaryStage.setScene(scene);
	}

	private HBox createLabeledField(String labelText, javafx.scene.Node field)
	{
		Label label = new Label(labelText);
		label.getStyleClass().add("form-label");

		// Ensure all form fields are aligned properly
		HBox box = new HBox(10, label, field);
		box.getStyleClass().add("form-field-container");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setMaxWidth(Double.MAX_VALUE);

		return box;
	}

	private Label createErrorLabel()
	{
		Label errorLabel = new Label();
		errorLabel.getStyleClass().add("error-label");
		errorLabel.setVisible(false);
		return errorLabel;
	}
}
