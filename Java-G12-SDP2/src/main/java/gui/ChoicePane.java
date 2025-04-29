package gui;

import domain.machine.MachineController;
import domain.maintenance.MaintenanceController;
import domain.site.SiteController;
import domain.user.UserController;
import gui.login.LoginPane;
import gui.machine.MachinesListComponent;
import gui.maintenance.MaintenanceListComponent;
import gui.report.AddReportForm;
import gui.sitesList.SitesListComponent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChoicePane extends GridPane
{

	public ChoicePane(Stage primaryStage)
	{
		primaryStage.setMinWidth(500);
		primaryStage.setFullScreen(true);

		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(40));

		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		setBackground(new Background(backgroundImage));

		Text title = new Text("Kies je paneel");
		title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		Button userManagementButton = createButton("Overzicht gebruikers");
		userManagementButton.setOnAction(e -> goToUserManagement(primaryStage));

		Button maintenanceListButton = createButton("Lijst onderhouden");
		maintenanceListButton.setOnAction(e -> goToMaintenanceList(primaryStage));

		Button addRapportButton = createButton("Rapport toevoegen");
		addRapportButton.setOnAction(e -> goToAddRapport(primaryStage));

		Button sitesButton = createButton("Overzicht sites");
		sitesButton.setOnAction(e -> gotToSitesList(primaryStage));

		Button machinesButton = createButton("Machine overzicht");
		machinesButton.setOnAction(e -> goToMachinesList(primaryStage));

		Button logoutButton = createButton("Uitloggen");
		logoutButton.setOnAction(e -> goToLoginPage(primaryStage));

		this.add(title, 0, 0, 2, 1);
		this.add(userManagementButton, 0, 1);
		this.add(maintenanceListButton, 1, 1);
		this.add(addRapportButton, 2, 1);
		this.add(sitesButton, 0, 2);
		this.add(machinesButton, 1, 2);
		this.add(logoutButton, 2, 2);

		for (javafx.scene.Node node : this.getChildren())
		{
			if (node instanceof Button)
			{
				GridPane.setMargin(node, new Insets(10));
			}
		}
	}

	private Button createButton(String text)
	{
		Button button = new Button(text);
		button.setPrefSize(200, 200);
		button.setStyle("-fx-font-size: 16px; " + "-fx-font-weight: bold; " + "-fx-background-color: #d9d9d9; "
				+ "-fx-text-fill: black; " + "-fx-background-radius: 10; " + "-fx-border-radius: 10; ");
		return button;
	}

	private void goToLoginPage(Stage primaryStage)
	{
		LoginPane loginPane = new LoginPane(primaryStage);
		Scene loginPaneScene = new Scene(loginPane);
		primaryStage.setScene(loginPaneScene);
	}

	private void goToUserManagement(Stage primaryStage)
	{
		UserManagementPane userManagementPane = new UserManagementPane(primaryStage);
		primaryStage.setScene(new Scene(userManagementPane, 800, 800));
	}

	private void goToMaintenanceList(Stage primaryStage)
	{
		MaintenanceController mc = new MaintenanceController();
		MaintenanceListComponent maintenanceListComponent = new MaintenanceListComponent(primaryStage, mc);
		Scene maintenanceListScene = new Scene(maintenanceListComponent);
		primaryStage.setScene(maintenanceListScene);
	}

	private void gotToSitesList(Stage primaryStage)
	{
		SiteController sc = new SiteController();
		SitesListComponent sitesListComponents = new SitesListComponent(primaryStage, sc);
		Scene loginPaneScene = new Scene(sitesListComponents);
		primaryStage.setScene(loginPaneScene);
	}

	private void goToAddRapport(Stage primaryStage)
	{
		MaintenanceController mc = new MaintenanceController();
		AddReportForm addRapportForm = new AddReportForm(primaryStage, mc.getMaintenances().getFirst());
		Scene addRapportScene = new Scene(addRapportForm);
		addRapportForm.getStylesheets().add(getClass().getResource("/css/AddRapport.css").toExternalForm());
		primaryStage.setScene(addRapportScene);
	}

	private void goToMachinesList(Stage primaryStage)
	{
		MachineController mc = new MachineController();
		SiteController sc = new SiteController();
		UserController uc = new UserController();
		MachinesListComponent machinesListComponent = new MachinesListComponent(primaryStage, mc, sc, uc);
		Scene machineListScene = new Scene(machinesListComponent, 1200, 800);
		primaryStage.setScene(machineListScene);
		primaryStage.show();
	}
}