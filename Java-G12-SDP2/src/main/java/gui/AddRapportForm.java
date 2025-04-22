package gui;

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

public class AddRapportForm extends BorderPane {

	private TextField siteNaamField, verantwoordelijkeField, onderhoudsNrField;
	private ComboBox<String> techniekerComboBox;
	private DatePicker startDatePicker, eindDatePicker;
	private TextField startTijdField, eindTijdField;
	private TextField redenField;
	private TextArea opmerkingenArea;
	private Label errorLabel;
	private VBox formBox;

	public void start(Stage primaryStage) {
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

	public AddRapportForm(Stage primaryStage) {
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

		techniekerComboBox = new ComboBox<>();
		techniekerComboBox.getItems().addAll("Technieker A", "Technieker B", "Technieker C");
		techniekerComboBox.setPromptText("Selecteer technieker");
		techniekerComboBox.setMaxWidth(Double.MAX_VALUE);

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

		HBox buttonBox = new HBox(createReportBtn);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));

		// Assemble layout with responsive container
		VBox content = new VBox(10, errorLabel, formBox, buttonBox);
		content.getStyleClass().add("content-container");
		content.setAlignment(Pos.TOP_CENTER);
		content.setPadding(new Insets(20));

		// Add listeners for window size changes
		this.widthProperty().addListener((obs, oldVal, newVal) -> {
			adjustFormLayout(newVal.doubleValue());
		});

		this.setTop(headerBox);
		this.setCenter(content);
	}

	// Create a responsive text field
	private TextField createResponsiveTextField() {
		TextField field = new TextField();
		field.setMaxWidth(Double.MAX_VALUE);
		return field;
	}

	// Adjust layout based on window width
	private void adjustFormLayout(double width) {
		// Adjust padding based on available width
		if (width < 700) {
			formBox.setPadding(new Insets(15));
		} else {
			formBox.setPadding(new Insets(30));
		}
	}

	private HBox createDateTimeInput(boolean isStart) {
		DatePicker datePicker = new DatePicker();
		datePicker.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(datePicker, Priority.ALWAYS);

		TextField timeField = new TextField();
		timeField.setPromptText("HH:MM");
		timeField.setPrefWidth(100);

		if (isStart) {
			startDatePicker = datePicker;
			startTijdField = timeField;
		} else {
			eindDatePicker = datePicker;
			eindTijdField = timeField;
		}

		HBox box = new HBox(10, datePicker, timeField);
		box.getStyleClass().add("date-time-box");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setMaxWidth(Double.MAX_VALUE);
		return box;
	}

	private HBox createLabeledField(String labelText, Control field) {
		Label label = new Label(labelText);
		label.getStyleClass().add("form-label");
		label.setMinWidth(150); // Reduced from 180 to work better on tablets
		label.setPrefWidth(180);

		// Make field take all available space
		field.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(field, Priority.ALWAYS);

		HBox box = new HBox(10, label, field);
		box.getStyleClass().add("form-field-container");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setMaxWidth(Double.MAX_VALUE);
		return box;
	}

	private HBox createLabeledField(String labelText, Node fieldGroup) {
		Label label = new Label(labelText);
		label.getStyleClass().add("form-label");
		label.setMinWidth(150); // Reduced from 180 to work better on tablets
		label.setPrefWidth(180);

		// Make the field group take all available space
		HBox.setHgrow(fieldGroup, Priority.ALWAYS);

		HBox box = new HBox(10, label, fieldGroup);
		box.getStyleClass().add("form-field-container");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setMaxWidth(Double.MAX_VALUE);
		return box;
	}
}