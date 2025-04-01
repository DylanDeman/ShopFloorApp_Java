package gui;

import java.util.List;

import domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import util.JPAUtil;

public class UserManagementPane extends GridPane
{
	private TableView<User> userTable;
	private Button addButton;

	private EntityManager entityManager;
	private Stage primaryStage;

	public UserManagementPane(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
		this.entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

		setVgap(10);
		setHgap(10);
		setPadding(new Insets(20));

		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		setBackground(new Background(backgroundImage));

		userTable = new TableView<>();
		buildColumns();

		addButton = new Button("Gebruiker toevoegen +");
		addButton.setOnAction(e -> openAddUserForm(primaryStage));

		String buttonStyle = "-fx-background-color: #f0453c; " + "-fx-text-fill: white; " + "-fx-font-weight: bold; "
				+ "-fx-padding: 8 15 8 15; " + "-fx-background-radius: 5;";
		addButton.setStyle(buttonStyle);

		GridPane.setHalignment(addButton, HPos.RIGHT);
		GridPane.setMargin(addButton, new Insets(0, 0, 10, 0));

		add(addButton, 0, 0);
		add(userTable, 0, 1);

		GridPane.setHgrow(userTable, Priority.ALWAYS);
		GridPane.setVgrow(userTable, Priority.ALWAYS);

		loadUsersFromDatabase();
	}

	private void buildColumns()
	{
		userTable.getColumns().clear();

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
			private final Button editButton = new Button();

			{
				ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
				editIcon.setFitWidth(16);
				editIcon.setFitHeight(16);
				editButton.setGraphic(editIcon);
				editButton.setBackground(Background.EMPTY);
				editButton.setOnAction(event -> openEditUserForm(primaryStage, getTableRow().getItem()));
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
			private final Button deleteButton = new Button();

			{
				ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
				deleteIcon.setFitHeight(16);
				deleteIcon.setFitWidth(16);
				deleteButton.setGraphic(deleteIcon);
				deleteButton.setBackground(Background.EMPTY);
				deleteButton.setOnAction(event -> deleteUser(getTableRow().getItem()));
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}
		});

		userTable.setPlaceholder(new Label("Geen gebruikers gevonden"));

		userTable.getColumns().add(editColumn);

		userTable.getColumns().add(idColumn);
		userTable.getColumns().add(nameColumn);
		userTable.getColumns().add(emailColumn);
		userTable.getColumns().add(roleColumn);
		userTable.getColumns().add(statusColumn);

		userTable.getColumns().add(deleteColumn);

	}

	private void loadUsersFromDatabase()
	{
		if (!entityManager.isOpen())
		{
			entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
		}
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
		}
	}

	private void openAddUserForm(Stage primaryStage)
	{
		primaryStage.getScene().setRoot(new AddUserForm(primaryStage, this));
	}

	private void openEditUserForm(Stage primaryStage, User user)
	{
		primaryStage.getScene().setRoot(new EditUserForm(primaryStage, user, this));
	}

	public void returnToUserManagement(Stage primaryStage)
	{
		primaryStage.getScene().setRoot(this);
		loadUsersFromDatabase();
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
