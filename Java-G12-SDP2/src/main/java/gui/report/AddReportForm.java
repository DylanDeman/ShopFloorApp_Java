package gui.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import domain.maintenance.MaintenanceController;
import domain.maintenance.MaintenanceDTO;
import domain.report.Report;
import domain.report.ReportController;
import domain.site.Site;
import domain.site.SiteController;
import domain.user.User;
import gui.ChoicePane;
import gui.customComponents.CustomButton;
import gui.customComponents.CustomInformationBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
	private VBox formBox;
	private GridPane formGridPane;
	private MaintenanceDTO selectedMaintenanceDTO;
	private ReportController reportController;
	private MaintenanceController maintenanceController;
	private SiteController siteController;

	public AddReportForm(Stage primaryStage, MaintenanceDTO maintenanceDTO)
	{
		maintenanceController = new MaintenanceController();
		siteController = new SiteController();

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

		VBox titleSection = createTitleSection(primaryStage);

		generalMessageLabel = new Label();
		generalMessageLabel.setVisible(false);
		generalMessageLabel.getStyleClass().add("error-label");
		generalMessageLabel.setMaxWidth(Double.MAX_VALUE);
		generalMessageLabel.setAlignment(Pos.CENTER);

		formGridPane = new GridPane();
		formGridPane.setHgap(15);
		formGridPane.setVgap(12);
		formGridPane.setPadding(new Insets(20));
		initializeFormComponents();

		organizeFormLayout();

		formBox = new VBox(15, formGridPane);
		formBox.getStyleClass().add("form-box");
		formBox.setAlignment(Pos.TOP_CENTER);
		formBox.setMaxWidth(800);
		formBox.setMinWidth(400);

		FontIcon saveIcon = new FontIcon(BootstrapIcons.SAVE);
		CustomButton createReportBtn = new CustomButton(saveIcon, "Rapport aanmaken");
		createReportBtn.getStyleClass().add("create-report-button");
		createReportBtn.setPadding(new Insets(10, 30, 10, 30));
		createReportBtn.setOnAction(e -> createReport());

		HBox buttonBox = new HBox(createReportBtn);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 30, 0));

		VBox content = new VBox(20, titleSection, generalMessageLabel, formBox, buttonBox);
		content.setAlignment(Pos.TOP_CENTER);
		content.setPadding(new Insets(50, 80, 0, 80));

		this.widthProperty().addListener((obs, oldVal, newVal) ->
		{
			adjustFormLayout(newVal.doubleValue());
			updatePadding(primaryStage);
		});

		this.setCenter(content);

		loadTechnicians();
	}

	private void updatePadding(Stage stage)
	{
		double amountOfPixels = stage.getWidth();
		double calculatedPadding = amountOfPixels < 1200 ? amountOfPixels * 0.05 : amountOfPixels * 0.10;
		this.setPadding(new Insets(50, calculatedPadding, 0, calculatedPadding));
	}

	private VBox createTitleSection(Stage primaryStage)
	{
		HBox header = createWindowHeader(primaryStage);
		HBox infoBox = new CustomInformationBox("Maak een rapport aan voor het geselecteerde onderhoud.");
		return new VBox(10, header, infoBox);
	}

	private HBox createWindowHeader(Stage primaryStage)
	{
		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.CENTER_LEFT);

		FontIcon icon = new FontIcon("fas-arrow-left");
		icon.setIconSize(20);
		Button backButton = new Button();
		backButton.setGraphic(icon);
		backButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
		backButton.setOnAction(e -> returnToChoicePane(primaryStage));

		titleLabel = new Label("Rapport aanmaken");
		titleLabel.setStyle("-fx-font: 40 arial;");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, titleLabel, spacer);
		return hbox;
	}

	private void initializeFormComponents()
	{
		siteNameLabel = new Label(selectedMaintenanceDTO.machine().site().siteName());
		siteNameLabel.getStyleClass().add("info-value");

		responsiblePersonLabel = new Label(selectedMaintenanceDTO.technician().getFullName());
		responsiblePersonLabel.getStyleClass().add("info-value");

		maintenanceNumberLabel = new Label("" + selectedMaintenanceDTO.id());
		maintenanceNumberLabel.getStyleClass().add("info-value");

		// Initialize input fields
		technicianComboBox = new ComboBox<>();
		technicianComboBox.setPromptText("Selecteer een technieker");
		technicianComboBox.setMaxWidth(Double.MAX_VALUE);

		startDatePicker = new DatePicker();
		startDatePicker.setPromptText("Kies startdatum");

		startTimeField = new TextField();
		startTimeField.setPromptText("HH:MM");

		endDatePicker = new DatePicker();
		endDatePicker.setPromptText("Kies einddatum");

		endTimeField = new TextField();
		endTimeField.setPromptText("HH:MM");

		reasonField = new TextField();
		reasonField.setPromptText("Voer reden in");

		commentsArea = new TextArea();
		commentsArea.setPrefRowCount(5);
		commentsArea.setWrapText(true);
		commentsArea.setPromptText("Voer eventuele opmerkingen in");

		// Initialize error labels
		technicianErrorLabel = createErrorLabel("Selecteer een technieker");
		startDateErrorLabel = createErrorLabel("Startdatum is verplicht");
		startTimeErrorLabel = createErrorLabel("Voer starttijd in (HH:MM)");
		endDateErrorLabel = createErrorLabel("Einddatum is verplicht");
		endTimeErrorLabel = createErrorLabel("Voer eindtijd in (HH:MM)");
		reasonErrorLabel = createErrorLabel("Reden is verplicht");
	}

	private void organizeFormLayout()
	{
		int row = 0;

		Label infoSectionLabel = new Label("Onderhoudsgegevens");
		infoSectionLabel.getStyleClass().add("section-label");
		formGridPane.add(infoSectionLabel, 0, row++, 2, 1);

		double labelWidth = 150;

		formGridPane.add(createInfoField("Site:", siteNameLabel, labelWidth), 0, row++, 2, 1);
		formGridPane.add(createInfoField("Verantwoordelijke:", responsiblePersonLabel, labelWidth), 0, row++, 2, 1);
		formGridPane.add(createInfoField("Onderhoudsnummer:", maintenanceNumberLabel, labelWidth), 0, row++, 2, 1);

		VBox spacer = new VBox();
		spacer.setMinHeight(15);
		formGridPane.add(spacer, 0, row++);

		Label reportSectionLabel = new Label("Rapport informatie");
		reportSectionLabel.getStyleClass().add("section-label");
		formGridPane.add(reportSectionLabel, 0, row++, 2, 1);

		formGridPane.add(createLabeledField("Technieker:", technicianComboBox, labelWidth), 0, row++, 2, 1);
		formGridPane.add(technicianErrorLabel, 0, row++, 2, 1);

		double timeFieldLabelWidth = 80;

		HBox startDateTimeBox = new HBox(15);
		startDateTimeBox.setAlignment(Pos.CENTER_LEFT);

		HBox startDateField = createLabeledField("Startdatum:", startDatePicker, labelWidth);
		HBox startTimeField = createLabeledField("Starttijd:", this.startTimeField, timeFieldLabelWidth);

		startDateTimeBox.getChildren().addAll(startDateField, startTimeField);
		formGridPane.add(startDateTimeBox, 0, row++, 2, 1);

		HBox startErrorBox = new HBox(20, startDateErrorLabel, startTimeErrorLabel);
		formGridPane.add(startErrorBox, 0, row++, 2, 1);

		HBox endDateTimeBox = new HBox(15);
		endDateTimeBox.setAlignment(Pos.CENTER_LEFT);

		HBox endDateField = createLabeledField("Einddatum:", endDatePicker, labelWidth);
		HBox endTimeField = createLabeledField("Eindtijd:", this.endTimeField, timeFieldLabelWidth);

		endDateTimeBox.getChildren().addAll(endDateField, endTimeField);
		formGridPane.add(endDateTimeBox, 0, row++, 2, 1);

		HBox endErrorBox = new HBox(20, endDateErrorLabel, endTimeErrorLabel);
		formGridPane.add(endErrorBox, 0, row++, 2, 1);

		formGridPane.add(createLabeledField("Reden:", reasonField, labelWidth), 0, row++, 2, 1);
		formGridPane.add(reasonErrorLabel, 0, row++, 2, 1);

		formGridPane.add(createLabeledField("Opmerkingen:", commentsArea, labelWidth), 0, row++, 2, 1);
	}

	private HBox createInfoField(String labelText, javafx.scene.Node field, double labelWidth)
	{
		Label label = new Label(labelText);
		label.getStyleClass().add("form-label");

		// Set fixed width for consistent alignment
		label.setPrefWidth(labelWidth);

		// Create container with better alignment
		HBox box = new HBox(10, label, field);
		box.getStyleClass().add("form-field-container");
		box.setAlignment(Pos.CENTER_LEFT);

		return box;
	}

	private HBox createLabeledField(String labelText, javafx.scene.Node field, double labelWidth)
	{
		Label label = new Label(labelText);
		label.getStyleClass().add("form-label");

		label.setPrefWidth(labelWidth);

		if (field instanceof TextField || field instanceof DatePicker || field instanceof ComboBox)
		{
			field.setStyle("-fx-pref-width: 200px;");
		}

		// Create container with better alignment
		HBox box = new HBox(10, label, field);
		box.getStyleClass().add("form-field-container");
		box.setAlignment(Pos.CENTER_LEFT);

		return box;
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
			Site site = siteController.getSite(selectedMaintenanceDTO.machine().site().id());

			Report report = new Report(maintenanceController.getMaintenance(selectedMaintenanceDTO.id()),
					selectedTechnician, startDate, startTime, endDate, endTime, reason, comments, site);
			reportController.createReport(report, selectedMaintenanceDTO);

			showSuccess("Rapport succesvol aangemaakt!");
			clearUserInputFields();

		} catch (Exception e)
		{
			e.printStackTrace();
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

	private Label createErrorLabel(String text)
	{
		Label errorLabel = new Label(text);
		errorLabel.getStyleClass().add("error-label");
		errorLabel.setVisible(false);
		return errorLabel;
	}
}