package gui;

import gui.login.LoginPane;
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
		
		Button loginButton = new Button("Ga naar aanmelden");
		loginButton.setOnAction(e -> goToLogin(primaryStage));
		
		this.add(maintenanceListButton, 0, 10);
		this.add(loginButton, 0, 20);
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
	
	private void goToLogin(Stage primaryStage) {
		LoginPane loginPane = new LoginPane();
		Scene loginPaneScene = new Scene(loginPane);
		loginPane.getStylesheets().add(getClass().getResource("/css/loginstyles.css").toExternalForm());
		primaryStage.setMaximized(true);
		primaryStage.setScene(loginPaneScene);
	}
}
