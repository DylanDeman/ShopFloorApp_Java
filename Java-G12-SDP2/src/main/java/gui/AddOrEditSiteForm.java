package gui;

import domain.site.Site;
import domain.user.UserDTO;
import gui.sitesList.SitesListComponent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddOrEditSiteForm extends GridPane
{
	private Site site;
	private final SitesListComponent sitesListComponent;
	private final Stage primaryStage;

	private TextField siteNameField;
	private TextField streetField, houseNumberField, postalCodeField, cityField;
	private ComboBox<UserDTO> employeeBox;

	private Label siteNameError, employeeError;
	private Label streetError, houseNumberError, postalCodeError, cityError;

	private boolean isNewSite;

	public AddOrEditSiteForm(Stage primaryStage, SitesListComponent sitesListComponent, Site site)
	{
		this.primaryStage = primaryStage;
		this.sitesListComponent = sitesListComponent;
		this.site = site;
		this.isNewSite = site == null;

		if (!isNewSite)
		{
			fillSiteData(site);
		}

		buildGUI();
	}

	private void buildGUI()
	{
		this.setPadding(new Insets(20));
		this.setHgap(20);
		this.setVgap(20);

		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		setBackground(new Background(backgroundImage));

		Button backButton = new Button("← Terug");
		backButton.setOnAction(e -> sitesListComponent.returnToSiteList(primaryStage));
		this.add(backButton, 0, 0, 2, 1);

		Label headerLabel = new Label(isNewSite ? "SITE TOEVOEGEN" : "SITE AANPASSEN");
		headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");
		HBox headerBox = new HBox(headerLabel);
		headerBox.setAlignment(Pos.CENTER);
		headerBox.setStyle("-fx-background-color: rgb(240, 69, 60); " + "-fx-padding: 15px; "
				+ "-fx-border-radius: 5 5 5 5; " + "-fx-background-radius: 5 5 5 5;");
		headerBox.setMaxWidth(Double.MAX_VALUE);
		headerBox.setMaxHeight(40);
		this.add(headerBox, 0, 2, 2, 1);

		GridPane.setMargin(headerBox, new Insets(0, 0, 0, 0));

		VBox mainContent = new VBox(30);
		mainContent.setAlignment(Pos.TOP_CENTER);
		mainContent.setStyle(
				"-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");

		mainContent.setMaxWidth(Double.MAX_VALUE);

		VBox siteNameBox = new VBox(15);
		siteNameBox.setPadding(new Insets(20));
		siteNameBox.getChildren().add(createSiteNameField());

		VBox addressBox = new VBox(15, createAddressFieldsSection());

		VBox employeeBox = new VBox(15, createEmployeeBoxSection());

		mainContent.getChildren().addAll(siteNameBox, addressBox, employeeBox);

		mainContent.getChildren().addAll();
		this.add(mainContent, 0, 3, 2, 1);

		Button saveButton = new Button("Opslaan");
		saveButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
		saveButton.setMaxWidth(Double.MAX_VALUE);
		saveButton.setPadding(new Insets(10, 30, 10, 30));
		saveButton.setOnAction(e -> saveSite());

		HBox buttonBox = new HBox(saveButton);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));
		HBox.setHgrow(saveButton, Priority.ALWAYS);
		buttonBox.setMaxWidth(400);

		this.add(buttonBox, 0, 4, 2, 1);
		GridPane.setHalignment(buttonBox, HPos.CENTER);
	}

	private Node createEmployeeBoxSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		employeeError = createErrorLabel();

		employeeBox = new ComboBox<>();
		employeeBox.getItems().addAll(getAllEmployees());
		employeeBox.setPromptText("Selecteer een verantwoordelijke");
		employeeBox.setPrefWidth(200);

		int row = 1;
		pane.add(new Label("Verantwoordelijke:"), 0, row);
		pane.add(employeeBox, 1, row++);
		pane.add(employeeError, 1, row++);

		return pane;
	}

	private UserDTO getAllEmployees()
	{
		return null;
	}

	private void saveSite()
	{
		System.out.println("Gesaved!");
	}

	private GridPane createSiteNameField()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		siteNameError = createErrorLabel();

		siteNameField = new TextField();
		siteNameField.setPrefWidth(200);

		pane.add(new Label("Site:"), 0, 1);
		pane.add(siteNameField, 1, 1);
		pane.add(siteNameError, 1, 2);

		return pane;
	}

	private GridPane createAddressFieldsSection()
	{
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		Label sectionLabel = new Label("Adresgegevens");
		sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		pane.add(sectionLabel, 0, 0, 2, 1);

		streetError = createErrorLabel();
		houseNumberError = createErrorLabel();
		postalCodeError = createErrorLabel();
		cityError = createErrorLabel();

		streetField = new TextField();
		houseNumberField = new TextField();
		postalCodeField = new TextField();
		cityField = new TextField();

		streetField.setPrefWidth(200);
		houseNumberField.setPrefWidth(200);
		postalCodeField.setPrefWidth(200);
		cityField.setPrefWidth(200);

		int row = 1;
		pane.add(new Label("Straat:"), 0, row);
		pane.add(streetField, 1, row++);
		pane.add(streetError, 1, row++);

		pane.add(new Label("Huisnummer:"), 0, row);
		pane.add(houseNumberField, 1, row++);
		pane.add(houseNumberError, 1, row++);

		pane.add(new Label("Postcode:"), 0, row);
		pane.add(postalCodeField, 1, row++);
		pane.add(postalCodeError, 1, row++);

		pane.add(new Label("Stad:"), 0, row);
		pane.add(cityField, 1, row++);
		pane.add(cityError, 1, row++);

		return pane;
	}

	private Label createErrorLabel()
	{
		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		errorLabel.setStyle("-fx-font-size: 10px;");
		errorLabel.setMaxWidth(150);
		errorLabel.setWrapText(true);
		return errorLabel;
	}

	private void fillSiteData(Site site)
	{

	}

}
