package gui;

import gui.machine.MachinesListComponent;
import gui.maintenance.MaintenanceListComponent;
import gui.report.AddReportForm;
import gui.sitesList.SitesListComponent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class ChoicePane extends GridPane
{

	private final MainLayout mainLayout;
	private final AppServices services;

	public ChoicePane(MainLayout mainLayout)
	{
		this.mainLayout = mainLayout;
		this.services = mainLayout.getServices();
		setupLayout();
	}

	private void setupLayout()
	{

		Text title = new Text("Kies je paneel");
		title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		Button userManagementButton = createButton("Overzicht gebruikers");
		userManagementButton.setOnAction(e -> goToUserManagement());

		Button maintenanceListButton = createButton("Lijst onderhouden");
		maintenanceListButton.setOnAction(e -> goToMaintenanceList());

		Button addRapportButton = createButton("Rapport toevoegen");
		addRapportButton.setOnAction(e -> goToAddRapport());

		Button sitesButton = createButton("Overzicht sites");
		sitesButton.setOnAction(e -> gotToSitesList());

		Button machinesButton = createButton("Machine overzicht");
		machinesButton.setOnAction(e -> goToMachinesList());

		Button logoutButton = createButton("Uitloggen");
		logoutButton.setOnAction(e -> mainLayout.showLoginScreen());

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

	private void goToUserManagement()
	{
		UserManagementPane userManagementPane = new UserManagementPane(mainLayout);
		mainLayout.setContent(userManagementPane, true, false);
	}

	private void goToMaintenanceList()
	{
		MaintenanceListComponent maintenanceList = new MaintenanceListComponent(mainLayout);
		mainLayout.setContent(maintenanceList, true, false);
	}

	private void gotToSitesList()
	{
		SitesListComponent sitesList = new SitesListComponent(mainLayout);
		mainLayout.setContent(sitesList, true, false);
	}

	private void goToAddRapport()
	{
		AddReportForm addReportForm = new AddReportForm(mainLayout,
				services.getMaintenanceController().getMaintenances().getFirst());
		mainLayout.setContent(addReportForm, true, false);
	}

	private void goToMachinesList()
	{
		MachinesListComponent machinesList = new MachinesListComponent(mainLayout);
		mainLayout.setContent(machinesList, true, false);
	}
}