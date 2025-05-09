package gui;

import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.user.User;
import gui.customComponents.CustomInformationBox;
import interfaces.Observer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import repository.UserRepository;
import util.CurrentPage;

public class UserManagementPane extends GridPane implements Observer
{

	private TableView<User> userTable;
	private Button addButton;
	private final UserRepository userRepo;
	private final MainLayout mainLayout;

	public UserManagementPane(MainLayout mainLayout)
	{
		this.mainLayout = mainLayout;
		this.userRepo = mainLayout.getServices().getUserRepo();
		this.userRepo.addObserver(this);

		buildGUI();
		loadUsers();
	}

	private void buildGUI()
	{
		this.getStylesheets().add(getClass().getResource("/css/tablePane.css").toExternalForm());

		this.getChildren().add(createTitleSection());

		userTable = new TableView<>();
		userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		buildColumns();

		addButton = new Button("Gebruiker toevoegen +");
		addButton.getStyleClass().add("add-button");
		addButton.setOnAction(e -> openAddUserForm());

		GridPane.setHalignment(addButton, HPos.RIGHT);
		GridPane.setMargin(addButton, new Insets(0, 0, 10, 0));

		add(addButton, 0, 0);
		add(userTable, 0, 1);

		GridPane.setHgrow(userTable, Priority.ALWAYS);
		GridPane.setVgrow(userTable, Priority.ALWAYS);
	}

	private VBox createTitleSection()
	{
		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.CENTER_LEFT);

		FontIcon icon = new FontIcon("fas-arrow-left");
		icon.setIconSize(20);
		Button backButton = new Button();
		backButton.setGraphic(icon);
		backButton.getStyleClass().add("back-button");
		backButton.setOnAction(e -> mainLayout.showHomeScreen());
		this.add(backButton, 0, 0, 2, 1);

		Label title = new Label("Gebruikerslijst");
		title.getStyleClass().add("title-label");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, title, spacer);

		HBox infoBox = new CustomInformationBox("Hieronder vindt u een overzicht van alle gebruikers.");
		VBox.setMargin(infoBox, new Insets(20, 0, 10, 0));

		return new VBox(10, hbox, infoBox);
	}

	private void buildColumns()
	{
		userTable.getColumns().clear();

		TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

		TableColumn<User, String> firstnameColumn = new TableColumn<>("Voornaam");
		firstnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));

		TableColumn<User, String> lastnameColumn = new TableColumn<>("Voornaam");
		lastnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));

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
				FontIcon editIcon = new FontIcon("fas-pen");
				editIcon.setIconSize(20);
				editButton.setGraphic(editIcon);
				editButton.setBackground(Background.EMPTY);
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
			private final Button deleteButton = new Button();

			{
				FontIcon deleteIcon = new FontIcon("far-trash-alt");
				deleteIcon.setIconSize(20);
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
		userTable.getColumns().add(firstnameColumn);
		userTable.getColumns().add(lastnameColumn);
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

	private void openAddUserForm()
	{
		Parent addUserForm = new AddOrEditUserForm(mainLayout, userRepo, null);
		mainLayout.setContent(addUserForm, true, false, CurrentPage.NONE);
	}

	private void openEditUserForm(User user)
	{
		Parent editUserForm = new AddOrEditUserForm(mainLayout, userRepo, user);
		mainLayout.setContent(editUserForm, true, false, CurrentPage.NONE);
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
