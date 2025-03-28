package gui;

import java.util.List;

import domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.JPAUtil;

public class UserManagementPane extends GridPane
{
	private TableView<User> userTable;
	private Button addButton;

	private EntityManager entityManager;

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

		entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
		loadUsersFromDatabase();
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

	}

	private void loadUsersFromDatabase()
	{
		entityManager.getTransaction().begin();

		try
		{
			TypedQuery<User> userQuery = entityManager.createNamedQuery("User.getAllWithAddress", User.class);
			List<User> userList = userQuery.getResultList();
			userTable.getItems().setAll(userList);
			entityManager.getTransaction().commit();
		} catch (Exception e)
		{
			System.err.println("Error loading users: " + e.getMessage());
			e.printStackTrace();
		} finally
		{
			if (entityManager != null && entityManager.isOpen())
			{
				entityManager.close();
			}
		}
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
		entityManager.getTransaction().begin();
		entityManager.remove(user);
		entityManager.getTransaction().commit();
	}
}
