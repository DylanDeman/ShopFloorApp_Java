package gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChoisePane extends GridPane
{
	public ChoisePane(Stage primaryStage)
	{
		Button userManagementButton = new Button("Ga naar Gebruikersbeheer");
		userManagementButton.setOnAction(e -> goToUserManagement(primaryStage));

		this.add(userManagementButton, 0, 0);
	}

	private void goToUserManagement(Stage primaryStage)
	{
		UserManagementPane userManagementPane = new UserManagementPane();

		Scene userManagementScene = new Scene(userManagementPane, 400, 600);
		primaryStage.setScene(userManagementScene);
	}
}
