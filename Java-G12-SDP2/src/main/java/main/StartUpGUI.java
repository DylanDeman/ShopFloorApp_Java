package main;

import gui.ChoisePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartUpGUI extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		ChoisePane pane = new ChoisePane(primaryStage);

		Scene scene = new Scene(pane, 600, 200);
		primaryStage.setTitle("Kies je paneel");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
