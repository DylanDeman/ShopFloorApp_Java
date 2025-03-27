package gui;

import domain.User;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

public class UserManagementPane extends GridPane
{

	public UserManagementPane()
	{
		setVgap(10);
		setHgap(10);
		setPadding(new Insets(20));

		TableView<User> userTable = new TableView<>();

		add(userTable, 0, 0, 2, 1);

		Button addButton = new Button("Gebruiker toevoegen +");

		add(addButton, 0, 1);
	}
}
