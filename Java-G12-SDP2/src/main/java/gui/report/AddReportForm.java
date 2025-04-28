package gui.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import domain.machine.Machine;
import domain.report.ReportController;
import domain.report.ReportDTO;
import domain.site.Site;
import domain.user.User;
import exceptions.InvalidRapportException;
import gui.ChoicePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class AddReportForm extends BorderPane
{
	// Non-editable fields (pre-filled)
	private Label siteNameLabel, responsiblePersonLabel, maintenanceNumberLabel;

	// User input fields
	private ComboBox<User> technicianComboBox;
	private DatePicker startDatePicker, endDatePicker;
	private TextField startTimeField, endTimeField;
	private TextField reasonField;
	private TextArea commentsArea;

	private Label errorLabel;
	private VBox formBox;

	// Context information
	private Machine selectedMachine;
	private Site site;

	// Controller for business logic
	private ReportController reportController;

	public void start(Stage primaryStage, Machine machine)
	{
		AddReportForm form = new AddReportForm(primaryStage, machine);

		// Create scene without fixed dimensions
		Scene scene = new Scene(form);

		// Apply CSS stylesheet
		scene.getStylesheets().add(getClass().getResource("/css/rapport-form.css").toExternalForm());

		// Set minimum size to prevent elements from becoming too compressed
		primaryStage.setMinWidth(600);
		primaryStage.setMinHeight(500);

		// Set preferred size (will adapt based on screen size)
		primaryStage.setWidth(1000);
		primaryStage.setHeight(700);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Rapport aanmaken");
		primaryStage.show();
	}

	public AddReportForm(Stage primaryStage, Machine machine)
	{
		this(primaryStage, machine, new ReportController());
	}

	public AddReportForm(Stage primaryStage, Machine machine, ReportController reportController)
	{
		this.reportController = reportController;

		if (machine == null)
		{
			returnToChoicePane(primaryStage);
			throw new IllegalArgumentException("De machine is ongeldig");
		}

		this.selectedMachine = machine;
		// Get site from the selected machine
		this.site = machine.getSite();

		this.getStyleClass().add("main-pane");

		// Background with responsive sizing
		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		this.setBackground(new Background(backgroundImage));

		// Error Label
		errorLabel = new Label();
		errorLabel.getStyleClass().add("error-label");
		errorLabel.setWrapText(true);
		errorLabel.setMaxWidth(Double.MAX_VALUE);
		errorLabel.setVisible(false); // Initially hidden

		// Create back button
		Button backButton = new Button("← Terug");
		backButton.setOnAction(e -> returnToChoicePane(primaryStage));
		backButton.getStyleClass().add("back-button");

		// Title
		Label headerLabel = new Label("RAPPORT AANMAKEN");
		headerLabel.getStyleClass().add("header-label");

		// Update header box to include the back button
		HBox headerBox = new HBox(20);
		headerBox.getChildren().addAll(backButton, headerLabel);
		headerBox.getStyleClass().add("header-box");
		headerBox.setAlignment(Pos.CENTER);
		headerBox.setPadding(new Insets(20, 0, 10, 0));

		// Main form area with responsive sizing
		formBox = new VBox(15);
		formBox.getStyleClass().add("form-box");
		formBox.setPadding(new Insets(30));
		formBox.setAlignment(Pos.TOP_CENTER);
		formBox.setMaxWidth(900); // Maximum width on large screens
		formBox.setMinWidth(400); // Minimum width on small screens

		// Initialize display labels for pre-filled information
		siteNameLabel = createInfoLabel(site.getSiteName());
		responsiblePersonLabel = createInfoLabel(site.getVerantwoordelijke().getFullName());

		// Generate next maintenance number through controller
		String nextMaintenanceNumber = reportController.generateNextMaintenanceNumber(site);
		maintenanceNumberLabel = createInfoLabel(nextMaintenanceNumber);

		// Setup technician combo box with actual User objects
		technicianComboBox = new ComboBox<>();
		loadTechnicians();
		technicianComboBox.setPromptText("Selecteer technieker");
		technicianComboBox.setMaxWidth(Double.MAX_VALUE);

		// Set up converter to display user names in the combobox
		technicianComboBox.setConverter(new StringConverter<User>()
		{
			@Override
			public String toString(User user)
			{
				return user == null ? "" : user.getFullName();
			}

			@Override
			public User fromString(String string)
			{
				return null; // Not needed for this use case
			}
		});

		reasonField = createResponsiveTextField();
		reasonField.setPromptText("Voer reden in");

		commentsArea = new TextArea();
		commentsArea.setPrefRowCount(5);
		commentsArea.setWrapText(true);
		commentsArea.setMaxWidth(Double.MAX_VALUE);
		commentsArea.setPromptText("Voer opmerkingen in (optioneel)");

		// Add all form fields - pre-filled info displayed as labels
		formBox.getChildren().addAll(createLabeledDisplayField("SiteNaam:", siteNameLabel),
				createLabeledDisplayField("Verantwoordelijke:", responsiblePersonLabel),
				createLabeledDisplayField("Onderhoudsnr:", maintenanceNumberLabel),
				createLabeledField("Technieker:", technicianComboBox),
				createLabeledField("Start (datum + tijd):", createDateTimeInput(true)),
				createLabeledField("Einde (datum + tijd):", createDateTimeInput(false)),
				createLabeledField("Reden:", reasonField), createLabeledField("Opmerkingen:", commentsArea));

		// Button
		Button createReportBtn = new Button("Rapport aanmaken");
		createReportBtn.getStyleClass().add("create-report-button");
		createReportBtn.setPadding(new Insets(10, 30, 10, 30));

		// Add action for the button using our controller
		createReportBtn.setOnAction(e -> createReport());

		HBox buttonBox = new HBox(createReportBtn);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));

		// Assemble layout with responsive container
		VBox content = new VBox(10, errorLabel, formBox, buttonBox);
		content.getStyleClass().add("content-container");
		content.setAlignment(Pos.TOP_CENTER);
		content.setPadding(new Insets(20));

		// Add listeners for window size changes
		this.widthProperty().addListener((obs, oldVal, newVal) ->
		{
			adjustFormLayout(newVal.doubleValue());
		});

		this.setTop(headerBox);
		this.setCenter(content);
	}

	// Create label for displaying pre-filled information
	private Label createInfoLabel(String text)
	{
		Label label = new Label(text);
		label.getStyleClass().add("info-display-label");
		label.setMaxWidth(Double.MAX_VALUE);
		return label;
	}

	// Load technicians from controller
	private void loadTechnicians()
	{
		technicianComboBox.getItems().clear();
		technicianComboBox.getItems().addAll(reportController.getTechnicians());
	}

	// Create a responsive text field
	private TextField createResponsiveTextField()
	{
		TextField field = new TextField();
		field.setMaxWidth(Double.MAX_VALUE);
		return field;
	}

	// Adjust layout based on window width
	private void adjustFormLayout(double width)
	{
		// Adjust padding based on available width
		if (width < 700)
		{
			formBox.setPadding(new Insets(15));
		} else
		{
			formBox.setPadding(new Insets(30));
		}
	}

	private HBox createDateTimeInput(boolean isStart)
	{
		DatePicker datePicker = new DatePicker();
		datePicker.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(datePicker, Priority.ALWAYS);

		TextField timeField = new TextField();
		timeField.setPromptText("HH:MM");
		timeField.setPrefWidth(100);

		if (isStart)
		{
			startDatePicker = datePicker;
			startTimeField = timeField;
			// Default to current date and time
			startDatePicker.setValue(LocalDate.now());
			startTimeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
		} else
		{
			endDatePicker = datePicker;
			endTimeField = timeField;
			// Default to current date and time + 1 hour
			endDatePicker.setValue(LocalDate.now());
			endTimeField.setText(LocalTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm")));
		}

		HBox box = new HBox(10, datePicker, timeField);
		box.getStyleClass().add("date-time-box");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setMaxWidth(Double.MAX_VALUE);
		return box;
	}

	// For editable fields
	private HBox createLabeledField(String labelText, Control field)
	{
		Label label = new Label(labelText);
		label.getStyleClass().add("form-label");
		label.setMinWidth(150);
		label.setPrefWidth(180);

		field.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(field, Priority.ALWAYS);

		HBox box = new HBox(10, label, field);
		box.getStyleClass().add("form-field-container");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setMaxWidth(Double.MAX_VALUE);
		return box;
	}

	// For read-only display fields
	private HBox createLabeledDisplayField(String labelText, Label valueLabel)
	{
		Label label = new Label(labelText);
		label.getStyleClass().add("form-label");
		label.setMinWidth(150);
		label.setPrefWidth(180);

		HBox.setHgrow(valueLabel, Priority.ALWAYS);

		HBox box = new HBox(10, label, valueLabel);
		box.getStyleClass().addAll("form-field-container", "display-field-container");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setMaxWidth(Double.MAX_VALUE);
		return box;
	}

	private HBox createLabeledField(String labelText, Node fieldGroup)
	{
		Label label = new Label(labelText);
		label.getStyleClass().add("form-label");
		label.setMinWidth(150);
		label.setPrefWidth(180);

		HBox.setHgrow(fieldGroup, Priority.ALWAYS);

		HBox box = new HBox(10, label, fieldGroup);
		box.getStyleClass().add("form-field-container");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setMaxWidth(Double.MAX_VALUE);
		return box;
	}

	private void createReport()
	{
		try
		{
			// Clear previous error messages
			errorLabel.setVisible(false);

			// Get pre-filled values
			String maintenanceNumber = maintenanceNumberLabel.getText();

			// Get user input values
			User selectedTechnician = technicianComboBox.getValue();
			String reason = reasonField.getText().trim();
			String comments = commentsArea.getText().trim();

			// Parse dates and times
			LocalDate startDate = startDatePicker.getValue();
			LocalTime startTime = parseTime(startTimeField.getText());
			LocalDate endDate = endDatePicker.getValue();
			LocalTime endTime = parseTime(endTimeField.getText());

			// Create DTO with all needed data
			ReportDTO reportDTO = new ReportDTO(maintenanceNumber, site, selectedTechnician, startDate, startTime,
					endDate, endTime, reason, comments);

			// Use controller to create the report
			reportController.createReport(reportDTO);

			// Show success message
			showSuccess("Rapport succesvol aangemaakt!");

			// Clear user input fields only
			clearUserInputFields();

			// Update maintenance number for next report
			maintenanceNumberLabel.setText(reportController.generateNextMaintenanceNumber(site));

		} catch (IllegalStateException e)
		{
			// Show validation errors from the builder
			showError("Validatiefout: " + e.getMessage());
		} catch (DateTimeParseException e)
		{
			showError("Ongeldige tijdsindeling. Gebruik HH:MM format.");
		} catch (InvalidRapportException e)
		{
			showError(e.getMessage());
		} catch (Exception e)
		{
			showError("Er is een fout opgetreden: " + e.getMessage());
		}
	}

	private LocalTime parseTime(String timeStr)
	{
		if (timeStr == null || timeStr.trim().isEmpty())
		{
			throw new DateTimeParseException("Tijd kan niet leeg zijn", timeStr, 0);
		}

		// Parse time in format HH:MM
		return LocalTime.parse(timeStr.trim(), DateTimeFormatter.ofPattern("HH:mm"));
	}

	private void showError(String message)
	{
		errorLabel.setText(message);
		errorLabel.getStyleClass().setAll("error-label");
		errorLabel.setVisible(true);
	}

	private void showSuccess(String message)
	{
		errorLabel.setText(message);
		errorLabel.getStyleClass().setAll("success-label");
		errorLabel.setVisible(true);
	}

	private void clearUserInputFields()
	{
		technicianComboBox.setValue(null);

		// Reset dates to current
		startDatePicker.setValue(LocalDate.now());
		startTimeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
		endDatePicker.setValue(LocalDate.now());
		endTimeField.setText(LocalTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm")));

		reasonField.clear();
		commentsArea.clear();
	}

	private void returnToChoicePane(Stage stage)
	{
		ChoicePane choicePane = new ChoicePane(stage);
		Scene choicePaneScene = new Scene(choicePane);
		stage.setScene(choicePaneScene);
	}
}