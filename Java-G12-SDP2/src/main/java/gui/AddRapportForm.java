package gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import domain.machine.Machine;
import domain.rapport.ConcreteRapportBuilder;
import domain.rapport.Rapport;
import domain.rapport.RapportBuilder;
import domain.rapport.RapportDirector;
import domain.site.Site;
import domain.user.User;
import exceptions.InvalidRapportException;
import jakarta.persistence.TypedQuery;
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
import repository.GenericDaoJpa;
import util.Role;

public class AddRapportForm extends BorderPane
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

	// DAOs
	private GenericDaoJpa<Site> siteDao;
	private GenericDaoJpa<User> userDao;
	private GenericDaoJpa<Rapport> rapportDao;
	private GenericDaoJpa<Machine> machineDao;

	public void start(Stage primaryStage, Machine machine)
	{
		AddRapportForm form = new AddRapportForm(primaryStage, machine);

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

	public AddRapportForm(Stage primaryStage, Machine machine)
	{
		this(primaryStage, machine, new GenericDaoJpa<>(Site.class), new GenericDaoJpa<>(User.class),
				new GenericDaoJpa<>(Rapport.class), new GenericDaoJpa<>(Machine.class));
	}

	public AddRapportForm(Stage primaryStage, Machine machine, GenericDaoJpa<Site> siteDao, GenericDaoJpa<User> userDao,
			GenericDaoJpa<Rapport> rapportDao, GenericDaoJpa<Machine> machineDao)
	{
		this.siteDao = siteDao;
		this.userDao = userDao;
		this.rapportDao = rapportDao;
		this.machineDao = machineDao;
		if (this.selectedMachine != null && machine != null)
		{
			this.selectedMachine = machine;
		} else
		{
			returnToChoicePane(primaryStage);
			throw new IllegalArgumentException("de machine is ongeldig");
		}

		// Get site from the selected machine
		this.site = machine.getSite();

		this.getStyleClass().add("main-pane");

		// Background with responsive sizing
		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		this.setBackground(new Background(backgroundImage));

		Button backButton = new Button("← Terug");
		backButton.setOnAction(e -> returnToChoicePane(primaryStage));

		// Error Label
		errorLabel = new Label();
		errorLabel.getStyleClass().add("error-label");
		errorLabel.setWrapText(true);
		errorLabel.setMaxWidth(Double.MAX_VALUE);
		errorLabel.setVisible(false); // Initially hidden

		// Title
		Label headerLabel = new Label("RAPPORT AANMAKEN");
		headerLabel.getStyleClass().add("header-label");

		HBox headerBox = new HBox(headerLabel);
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

		// Generate next maintenance number
		String nextMaintenanceNumber = generateNextMaintenanceNumber();
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

		// Add action for the button using our Builder pattern
		createReportBtn.setOnAction(e -> createRapport());

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

	// Load technicians directly from userDao
	private void loadTechnicians()
	{
		List<User> technicians = userDao.findAll().stream().filter(user -> user.getRole() == Role.TECHNIEKER)
				.collect(Collectors.toList());

		technicianComboBox.getItems().clear();
		technicianComboBox.getItems().addAll(technicians);
	}

	// Generate the next maintenance number by counting existing reports for this
	// site
	private String generateNextMaintenanceNumber()
	{
		// Query to count reports for this site
		TypedQuery<Long> query = GenericDaoJpa.em
				.createQuery("SELECT COUNT(r) FROM Rapport r WHERE r.site.id = :siteId", Long.class);
		query.setParameter("siteId", site.getId());
		Long reportCount = query.getSingleResult();

		// Format: SITE-XXX where XXX is a sequential number
		String sitePrefix = site.getSiteName().substring(0, Math.min(site.getSiteName().length(), 4)).toUpperCase()
				.replaceAll("[^A-Z0-9]", "");

		// Increment by 1 for the new report
		return String.format("%s-%03d", sitePrefix, reportCount + 1);
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

	// Get reports by technician directly using JPQL
	public List<Rapport> getRapportenByTechnieker(User technieker)
	{
		if (technieker == null)
		{
			throw new InvalidRapportException("Technieker cannot be null");
		}

		TypedQuery<Rapport> query = GenericDaoJpa.em.createNamedQuery("Rapport.findByTechnieker", Rapport.class);
		query.setParameter("technieker", technieker);
		return query.getResultList();
	}

	// Get reports by site directly using JPQL
	public List<Rapport> getRapportenBySite(Site site)
	{
		if (site == null)
		{
			throw new InvalidRapportException("Site cannot be null");
		}

		TypedQuery<Rapport> query = GenericDaoJpa.em.createNamedQuery("Rapport.findBySite", Rapport.class);
		query.setParameter("site", site);
		return query.getResultList();
	}

	// Get reports by date range directly using JPQL
	public List<Rapport> getRapportenByDateRange(LocalDate startDate, LocalDate endDate)
	{
		if (startDate == null || endDate == null)
		{
			throw new InvalidRapportException("Date range cannot be null");
		}

		if (endDate.isBefore(startDate))
		{
			throw new InvalidRapportException("End date cannot be before start date");
		}

		TypedQuery<Rapport> query = GenericDaoJpa.em.createNamedQuery("Rapport.findByDateRange", Rapport.class);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		return query.getResultList();
	}

	private void createRapport()
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

			// Validate required fields
			if (selectedTechnician == null)
			{
				throw new IllegalStateException("Technieker moet geselecteerd worden");
			}

			if (reason.isEmpty())
			{
				throw new IllegalStateException("Reden mag niet leeg zijn");
			}

			// Validate dates and times
			LocalDate startDate = startDatePicker.getValue();
			LocalTime startTime = parseTime(startTimeField.getText());
			LocalDate endDate = endDatePicker.getValue();
			LocalTime endTime = parseTime(endTimeField.getText());

			if (startDate == null || endDate == null)
			{
				throw new IllegalStateException("Start- en einddatum moeten ingevuld worden");
			}

			// Start transaction for database operations
			rapportDao.startTransaction();

			try
			{
				// Generate unique ID for the new rapport
				String rapportId = generateRapportId();

				// Use the Builder pattern to create the rapport
				RapportBuilder builder = new ConcreteRapportBuilder(rapportId);

				// Either use director for standard flows or build directly for custom flows
				Rapport newRapport;

				if (reason.equalsIgnoreCase("Regulier onderhoud"))
				{
					// Use Director for standard maintenance rapport
					RapportDirector director = new RapportDirector(builder);
					newRapport = director.constructStandardMaintenanceRapport(rapportId, site, maintenanceNumber,
							selectedTechnician, startDate, startTime, endDate, endTime);
				} else
				{
					// Use builder directly for custom rapport
					newRapport = builder.setSite(site).setOnderhoudsNr(maintenanceNumber)
							.setTechnieker(selectedTechnician).setStartDate(startDate).setStartTime(startTime)
							.setEndDate(endDate).setEndTime(endTime).setReden(reason).setOpmerkingen(comments).build();
				}

				// Save the new rapport
				rapportDao.insert(newRapport);

				// Commit the transaction
				rapportDao.commitTransaction();

				// Show success message
				showSuccess("Rapport succesvol aangemaakt!");

				// Clear user input fields only
				clearUserInputFields();

				// Update maintenance number for next report
				maintenanceNumberLabel.setText(generateNextMaintenanceNumber());

			} catch (Exception e)
			{
				// Rollback the transaction in case of any error
				rapportDao.rollbackTransaction();
				throw e;
			}

		} catch (IllegalStateException e)
		{
			// Show validation errors from the builder
			showError("Validatiefout: " + e.getMessage());
		} catch (DateTimeParseException e)
		{
			showError("Ongeldige tijdsindeling. Gebruik HH:MM format.");
		} catch (Exception e)
		{
			showError("Er is een fout opgetreden: " + e.getMessage());
		}
	}

	private String generateRapportId()
	{
		// Simple UUID-based ID generation, could be replaced with a more
		// domain-specific ID
		return "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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