package gui;

import java.time.LocalDate;

import domain.Address;
import domain.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import utils.Role;
import utils.Status;

public class UserManagementPane extends GridPane
{
	private TableView<User> userTable;
	private Button addButton;

	public UserManagementPane()
	{
		setVgap(10);
		setHgap(10);
		setPadding(new Insets(20));

		userTable = new TableView<>();
		buildColumns();

		add(userTable, 0, 0, 2, 1);

		addButton = new Button("Gebruiker toevoegen +");
		addButton.setOnAction(e -> openAddUserForm());

		add(addButton, 0, 1);
	}

	private void buildColumns()
	{
		TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

		TableColumn<User, String> nameColumn = new TableColumn<>("Naam");
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
				cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));

		TableColumn<User, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

		TableColumn<User, String> roleColumn = new TableColumn<>("Rol");
		roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().toString()));

		TableColumn<User, String> statusColumn = new TableColumn<>("Status");
		statusColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));

		TableColumn<User, Void> editColumn = new TableColumn<>("Bewerken");
		editColumn.setCellFactory(param -> new TableCell<User, Void>()
		{
			private final Button editButton = new Button("✏️");

			{
				editButton.setStyle("-fx-background-color: yellow;");
				editButton.setOnAction(event -> openEditUserForm(getTableRow().getItem()));
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				setGraphic(empty ? null : editButton);
			}
		});

		TableColumn<User, Void> deleteColumn = new TableColumn<>("Verwijderen");
		deleteColumn.setCellFactory(param -> new TableCell<User, Void>()
		{
			private final Button deleteButton = new Button("🗑️");

			{
				deleteButton.setStyle("-fx-background-color: red;");
				deleteButton.setOnAction(event -> deleteUser(getTableRow().getItem()));
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}
		});

		userTable.getColumns().addAll(editColumn, idColumn, nameColumn, emailColumn, roleColumn, statusColumn,
				deleteColumn);

		userTable.getItems().addAll(
				new User(1, "Jan", "Janssen", "jan@email.com", "0612345678", "password", LocalDate.of(1990, 1, 1),
						new Address("Straat 1", 10, 1000, "Stad"), Status.INACTIEF, Role.TECHNIEKER),
				new User(2, "Piet", "Pietersen", "piet@email.com", "0623456789", "password", LocalDate.of(1985, 5, 15),
						new Address("Straat 2", 20, 2000, "Stad"), Status.INACTIEF, Role.ADMIN),
				new User(3, "Anna", "Dekker", "anna@email.com", "0634567890", "password", LocalDate.of(1995, 3, 12),
						new Address("Straat 3", 30, 3000, "Stad"), Status.ACTIEF, Role.VERANTWOORDELIJKE),
				new User(4, "Eva", "Smit", "eva@email.com", "0645678901", "password", LocalDate.of(1988, 7, 22),
						new Address("Straat 4", 40, 4000, "Stad"), Status.ACTIEF, Role.TECHNIEKER),
				new User(5, "Mark", "Visser", "mark@email.com", "0656789012", "password", LocalDate.of(1992, 11, 5),
						new Address("Straat 5", 50, 5000, "Stad"), Status.INACTIEF, Role.ADMIN),
				new User(6, "Sophie", "Koster", "sophie@email.com", "0667890123", "password", LocalDate.of(1993, 9, 18),
						new Address("Straat 6", 60, 6000, "Stad"), Status.ACTIEF, Role.MANAGER),
				new User(7, "Tom", "Hendriks", "tom@email.com", "0678901234", "password", LocalDate.of(1987, 12, 30),
						new Address("Straat 7", 70, 7000, "Stad"), Status.INACTIEF, Role.TECHNIEKER),
				new User(8, "Laura", "Bakker", "laura@email.com", "0689012345", "password", LocalDate.of(1994, 2, 25),
						new Address("Straat 8", 80, 8000, "Stad"), Status.ACTIEF, Role.VERANTWOORDELIJKE),
				new User(9, "Rob", "Jansen", "rob@email.com", "0690123456", "password", LocalDate.of(1986, 6, 10),
						new Address("Straat 9", 90, 9000, "Stad"), Status.ACTIEF, Role.ADMIN),
				new User(10, "Kim", "De Vries", "kim@email.com", "0612345678", "password", LocalDate.of(1991, 4, 20),
						new Address("Straat 10", 100, 10000, "Stad"), Status.INACTIEF, Role.MANAGER));

	}

	private void openAddUserForm()
	{
		Stage formStage = new Stage();
		new AddUserForm(formStage);
	}

	private void openEditUserForm(User user)
	{
		Stage formStage = new Stage();
		new EditUserForm(formStage, user);
	}

	private void deleteUser(User user)
	{
		userTable.getItems().remove(user);
		System.out.println("User deleted: " + user.getFirstName());
	}
}
