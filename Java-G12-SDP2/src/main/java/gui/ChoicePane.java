package gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChoicePane extends GridPane
{
	public ChoicePane(Stage primaryStage)
	{
		Button userManagementButton = new Button("Ga naar Gebruikersbeheer");
		userManagementButton.setOnAction(e -> goToUserManagement(primaryStage));

		this.add(userManagementButton, 0, 0);
		
		Button maintenanceListButton = new Button("Ga naar lijst onderhouden");
		maintenanceListButton.setOnAction(e -> goToMaintenanceList(primaryStage));
		
		this.add(maintenanceListButton, 0, 10);
	}

	private void goToUserManagement(Stage primaryStage)
	{
		UserManagementPane userManagementPane = new UserManagementPane();

		Scene userManagementScene = new Scene(userManagementPane, 400, 600);
		primaryStage.setScene(userManagementScene);
	}
	
	private void goToMaintenanceList(Stage primaryStage)
	{
		MaintenanceListComponent maintenanceListComponent = new MaintenanceListComponent(primaryStage);
		
		Scene maintenanceListScene = new Scene(maintenanceListComponent);
		
		primaryStage.setScene(maintenanceListScene);
	}
}
