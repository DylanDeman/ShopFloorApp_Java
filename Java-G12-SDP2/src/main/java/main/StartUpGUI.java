package main;

import gui.ChoicePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartUpGUI extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		ChoicePane pane = new ChoicePane(primaryStage);
		
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon-32x32.png")));
		
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
