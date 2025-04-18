package gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import domain.ConcreteRapportBuilder;
import domain.Rapport;
import domain.RapportBuilder;
import domain.RapportDirector;
import domain.RapportService;
import domain.Site;
import domain.SiteService;
import domain.User;
import domain.UserService;
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
import util.Role;

public class AddRapportForm extends BorderPane
{

	private TextField siteNaamField, verantwoordelijkeField, onderhoudsNrField;
	private ComboBox<User> techniekerComboBox;
	private DatePicker startDatePicker, eindDatePicker;
	private TextField startTijdField, eindTijdField;
	private TextField redenField;
	private TextArea opmerkingenArea;
	private Label errorLabel;
	private VBox formBox;

	// Service references
	private SiteService siteService;
	private UserService userService;
	private RapportService rapportService;

	public void start(Stage primaryStage)
	{
		AddRapportForm form = new AddRapportForm(primaryStage);

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

	public AddRapportForm(Stage primaryStage)
	{
		this(primaryStage, new SiteService(), new UserService(User.class), new RapportService(Rapport.class));
	}

	public AddRapportForm(Stage primaryStage, SiteService siteService, UserService userService,
			RapportService rapportService)
	{
		this.siteService = siteService;
		this.userService = userService;
		this.rapportService = rapportService;

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

		// Create form controls with responsive properties
		siteNaamField = createResponsiveTextField();
		verantwoordelijkeField = createResponsiveTextField();
		onderhoudsNrField = createResponsiveTextField();

		// Setup technieker combo box with actual User objects
		techniekerComboBox = new ComboBox<>();
		loadTechniekers();
		techniekerComboBox.setPromptText("Selecteer technieker");
		techniekerComboBox.setMaxWidth(Double.MAX_VALUE);

		// Set up converter to display user names in the combobox
		techniekerComboBox.setConverter(new StringConverter<User>()
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

		redenField = createResponsiveTextField();

		opmerkingenArea = new TextArea();
		opmerkingenArea.setPrefRowCount(5);
		opmerkingenArea.setWrapText(true);
		opmerkingenArea.setMaxWidth(Double.MAX_VALUE);

		// Add all form fields
		formBox.getChildren().addAll(createLabeledField("SiteNaam:", siteNaamField),
				createLabeledField("Verantwoordelijke:", verantwoordelijkeField),
				createLabeledField("Onderhoudsnr:", onderhoudsNrField),
				createLabeledField("Technieker:", techniekerComboBox),
				createLabeledField("Start (datum + tijd):", createDateTimeInput(true)),
				createLabeledField("Einde (datum + tijd):", createDateTimeInput(false)),
				createLabeledField("Reden:", redenField), createLabeledField("Opmerkingen:", opmerkingenArea));

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

	// Load techniekers from service
	private void loadTechniekers()
	{
		List<User> techniekers = userService.findAll().stream().filter(user -> user.getRole() == Role.TECHNIEKER)
				.collect(Collectors.toList());

		techniekerComboBox.getItems().clear();
		techniekerComboBox.getItems().addAll(techniekers);
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
			startTijdField = timeField;
		} else
		{
			eindDatePicker = datePicker;
			eindTijdField = timeField;
		}

		HBox box = new HBox(10, datePicker, timeField);
		box.getStyleClass().add("date-time-box");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setMaxWidth(Double.MAX_VALUE);
		return box;
	}

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

	private void createRapport()
	{
		try
		{
			// Clear previous error messages
			errorLabel.setVisible(false);

			// Parse and validate form data
			String siteNaam = siteNaamField.getText().trim();
			String onderhoudsNr = onderhoudsNrField.getText().trim();
			User selectedTechnieker = techniekerComboBox.getValue();
			String reden = redenField.getText().trim();
			String opmerkingen = opmerkingenArea.getText().trim();

			// Validate dates and times
			LocalDate startDate = startDatePicker.getValue();
			LocalTime startTime = parseTime(startTijdField.getText());
			LocalDate endDate = eindDatePicker.getValue();
			LocalTime endTime = parseTime(eindTijdField.getText());

			// Get or create site
			Site site = siteService.findBySiteNaam(siteNaam);
			if (site == null)
			{
				String verantwoordelijke = verantwoordelijkeField.getText().trim();
				site = siteService.createSite(siteNaam, verantwoordelijke);
			}

			// Generate unique ID for the new rapport
			String rapportId = generateRapportId();

			// Use the Builder pattern to create the rapport
			RapportBuilder builder = new ConcreteRapportBuilder(rapportId);

			// Either use director for standard flows or build directly for custom flows
			Rapport newRapport;

			if (reden.equalsIgnoreCase("Regulier onderhoud"))
			{
				// Use Director for standard maintenance rapport
				RapportDirector director = new RapportDirector(builder);
				newRapport = director.constructStandardMaintenanceRapport(rapportId, site, onderhoudsNr,
						selectedTechnieker, startDate, startTime, endDate, endTime);
			} else
			{
				// Use builder directly for custom rapport
				newRapport = builder.setSite(site).setOnderhoudsNr(onderhoudsNr).setTechnieker(selectedTechnieker)
						.setStartDate(startDate).setStartTime(startTime).setEndDate(endDate).setEndTime(endTime)
						.setReden(reden).setOpmerkingen(opmerkingen).build();
			}

			// Save the new rapport
			rapportService.insert(newRapport);

			// Show success message or navigate to another screen
			showSuccess("Rapport succesvol aangemaakt!");

			// Clear form
			clearForm();

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

	private void clearForm()
	{
		siteNaamField.clear();
		verantwoordelijkeField.clear();
		onderhoudsNrField.clear();
		techniekerComboBox.setValue(null);
		startDatePicker.setValue(null);
		startTijdField.clear();
		eindDatePicker.setValue(null);
		eindTijdField.clear();
		redenField.clear();
		opmerkingenArea.clear();
	}
}