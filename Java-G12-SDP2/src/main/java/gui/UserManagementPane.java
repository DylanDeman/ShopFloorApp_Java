package gui;

import java.util.List;

import domain.user.User;
import interfaces.Observer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import repository.UserRepository;

public class UserManagementPane extends GridPane implements Observer
{

	private TableView<User> userTable;
	private Button addButton;
	private Stage primaryStage;
	private final UserRepository userRepo;

	public UserManagementPane(Stage primaryStage, UserRepository userRepo)
	{
		this.userRepo = userRepo;
		this.userRepo.addObserver(this);
		this.primaryStage = primaryStage;

		buildGUI();
		loadUsers();
	}

	private void buildGUI()
	{
		setVgap(10);
		setHgap(10);
		setPadding(new Insets(20));

		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		setBackground(new Background(backgroundImage));

		Button backButton = new Button("← Terug");
		backButton.setOnAction(e -> returnToChoicePane(primaryStage));
		this.add(backButton, 0, 0, 2, 1);

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

	private void loadUsers()
	{
		List<User> users = userRepo.getAllUsers();
		userTable.getItems().setAll(users);
	}

	private void openAddUserForm(Stage primaryStage)
	{
		primaryStage.getScene().setRoot(new AddOrEditUserForm(primaryStage, userRepo, this, null));
	}

	private void openEditUserForm(Stage primaryStage, User user)
	{
		primaryStage.getScene().setRoot(new AddOrEditUserForm(primaryStage, userRepo, this, user));
	}

	public void returnToUserManagement(Stage primaryStage)
	{
		primaryStage.getScene().setRoot(this);
		loadUsers();
	}

	private void returnToChoicePane(Stage stage)
	{
		ChoicePane choicePane = new ChoicePane(stage);
		Scene choicePaneScene = new Scene(choicePane);
		stage.setScene(choicePaneScene);
	}

	private void deleteUser(User user)
	{
		try
		{
			userRepo.deleteUser(user);
		} catch (Exception e)
		{
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Fout bij verwijderen");
				alert.setHeaderText("Kan gebruiker niet verwijderen");
				alert.setContentText("Deze gebruiker is nog gekoppeld aan een site");
				alert.showAndWait();
			});
		}
	}

	@Override
	public void update()
	{
		Platform.runLater(this::loadUsers);
	}

}
