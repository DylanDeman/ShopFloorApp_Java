package gui;

import domain.machine.MachineController;
import domain.site.SiteController;
import domain.user.UserController;
import gui.login.LoginPane;
import gui.machine.MachinesListComponent;
import gui.maintenance.MaintenanceListComponent;
import gui.sitesList.SitesListComponent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChoicePane extends GridPane
{

	public ChoicePane(Stage primaryStage)
	{
		primaryStage.setMinWidth(500);
		Button userManagementButton = new Button("Ga naar Gebruikersbeheer");
		userManagementButton.setOnAction(e -> goToUserManagement(primaryStage));

		Button maintenanceListButton = new Button("Ga naar lijst onderhouden");
		maintenanceListButton.setOnAction(e -> goToMaintenanceList(primaryStage));

		Button addRapportButton = new Button("Ga naar een rapport toevoegen");
		addRapportButton.setOnAction(e -> goToAddRapport(primaryStage));

		Button sitesButton = new Button("Ga naar sites overzicht");
		sitesButton.setOnAction(e -> gotToSitesList(primaryStage));

		Button machinesButton = new Button("Ga naar machines overzicht");
		machinesButton.setOnAction(e -> goToMachinesList(primaryStage));

		// Logout button
		Button logoutButton = new Button("Uitloggen");
		logoutButton.setOnAction(e -> GoToLoginPage(primaryStage));

		this.add(userManagementButton, 0, 0);
		this.add(maintenanceListButton, 0, 20);
		this.add(addRapportButton, 0, 40);
		this.add(sitesButton, 0, 60);
		this.add(machinesButton, 0, 80);
		this.add(logoutButton, 0, 100);
	}

	private void GoToLoginPage(Stage primaryStage)
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
		MaintenanceListComponent maintenanceListComponent = new MaintenanceListComponent(primaryStage);

		Scene maintenanceListScene = new Scene(maintenanceListComponent);

		primaryStage.setScene(maintenanceListScene);
	}

	private void goToLogin(Stage primaryStage)
	{
		LoginPane loginPane = new LoginPane(primaryStage);
		Scene loginPaneScene = new Scene(loginPane);
		loginPane.getStylesheets().add(getClass().getResource("/css/loginstyles.css").toExternalForm());
		primaryStage.setScene(loginPaneScene);
		primaryStage.setMaximized(true);
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
		MachineController mc = new MachineController();
		AddRapportForm addRapportForm = new AddRapportForm(primaryStage, mc.getMachineList2().getFirst());
		Scene addRapportScene = new Scene(addRapportForm);
		addRapportForm.getStylesheets().add(getClass().getResource("/css/AddRapport.css").toExternalForm());
		primaryStage.setScene(addRapportScene);

	}

	private void goToMachinesList(Stage primaryStage)
	{
		MachineController mc = new MachineController(); // or however you get machine data
		SiteController sc = new SiteController();
		UserController uc = new UserController();
		MachinesListComponent machinesListComponent = new MachinesListComponent(primaryStage, mc, sc, uc);
		Scene machineListScene = new Scene(machinesListComponent, 1200, 800);

		primaryStage.setScene(machineListScene);
		primaryStage.show();
	}
}
