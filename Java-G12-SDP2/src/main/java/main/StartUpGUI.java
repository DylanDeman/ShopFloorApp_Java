package main;

import gui.UserManagementPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartUpGUI extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		UserManagementPane ump = new UserManagementPane();

		Scene scene = new Scene(ump, 600, 200);
		primaryStage.setResizable(false);
		primaryStage.setTitle("User management pane");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
