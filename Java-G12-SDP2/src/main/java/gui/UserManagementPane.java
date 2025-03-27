package gui;

import domain.User;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

public class UserManagementPane extends GridPane
{

	private TableView<User> userTable;

	public UserManagementPane()
	{
		setVgap(10);
		setHgap(10);
		setPadding(new Insets(20));

		userTable = new TableView<>();
		buildColumns();

		add(userTable, 0, 0, 2, 1);

		Button addButton = new Button("Gebruiker toevoegen +");

		add(addButton, 0, 1);
	}

	private void buildColumns()
	{
		TableColumn<User, Integer> idColumn = new TableColumn<>("ID");

		TableColumn<User, String> nameColumn = new TableColumn<>("Naam");

		TableColumn<User, String> emailColumn = new TableColumn<>("Email");

		TableColumn<User, String> roleColumn = new TableColumn<>("Rol");

		TableColumn<User, String> statusColumn = new TableColumn<>("Status");

		userTable.getColumns().addAll(idColumn, nameColumn, emailColumn, roleColumn, statusColumn);
	}
}
