package gui;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.user.UserController;
import dto.UserDTO;
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
import util.AuthenticationUtil;
import util.CurrentPage;
import util.Role;

public class UserManagementPane extends GridPane implements Observer
{

	private TableView<UserDTO> userTable;
	private Button addButton;
	private final MainLayout mainLayout;

	private UserController uc;

	public UserManagementPane(MainLayout mainLayout)
	{
		this.mainLayout = mainLayout;

		this.uc = AppServices.getInstance().getUserController();

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

		addButton = new Button("+ Gebruiker toevoegen");
		addButton.getStyleClass().add("add-button");
		addButton.setOnAction(e -> {
			if (AuthenticationUtil.hasRole(Role.ADMIN))
			{
				openAddUserForm();
			} else
			{
				mainLayout.showNotAllowedAlert();
			}
		});

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

		TableColumn<UserDTO, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().id()).asObject());

		TableColumn<UserDTO, String> firstnameColumn = new TableColumn<>("Voornaam");
		firstnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().firstName()));

		TableColumn<UserDTO, String> lastnameColumn = new TableColumn<>("Achternaam");
		lastnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().lastName()));

		TableColumn<UserDTO, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().email()));

		TableColumn<UserDTO, String> roleColumn = new TableColumn<>("Rol");
		roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().role().toString()));

		TableColumn<UserDTO, String> statusColumn = new TableColumn<>("Status");
		statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().toString()));

		TableColumn<UserDTO, Void> editColumn = new TableColumn<>("Bewerken");
		editColumn.setCellFactory(param -> new TableCell<UserDTO, Void>()
		{
			private final Button editButton = new Button();

			{
				FontIcon editIcon = new FontIcon("fas-pen");
				editIcon.setIconSize(20);
				editButton.setGraphic(editIcon);
				editButton.setBackground(Background.EMPTY);
				editButton.setOnAction(event -> {
					if (AuthenticationUtil.hasRole(Role.ADMIN))
					{
						openEditUserForm(getTableRow().getItem().id());
					} else
					{
						mainLayout.showNotAllowedAlert();
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				setGraphic(empty ? null : editButton);
			}
		});

		TableColumn<UserDTO, Void> deleteColumn = new TableColumn<>("Verwijderen");
		deleteColumn.setCellFactory(param -> new TableCell<UserDTO, Void>()
		{
			private final Button deleteButton = new Button();

			{
				FontIcon deleteIcon = new FontIcon("far-trash-alt");
				deleteIcon.setIconSize(20);
				deleteButton.setGraphic(deleteIcon);
				deleteButton.setBackground(Background.EMPTY);
				deleteButton.setOnAction(event -> {
					if (AuthenticationUtil.hasRole(Role.ADMIN))
					{
						deleteUser(getTableRow().getItem().id());
					} else
					{
						mainLayout.showNotAllowedAlert();
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}
		});

		userTable.setPlaceholder(new Label("Geen gebruikers gevonden"));

		userTable.getColumns().add(idColumn);
		userTable.getColumns().add(firstnameColumn);
		userTable.getColumns().add(lastnameColumn);
		userTable.getColumns().add(emailColumn);
		userTable.getColumns().add(roleColumn);
		userTable.getColumns().add(statusColumn);

		userTable.getColumns().add(editColumn);
		userTable.getColumns().add(deleteColumn);

	}

	private void loadUsers()
	{
		userTable.getItems().setAll(uc.getAllUsers());
	}

	private void openAddUserForm()
	{
		Parent addUserForm = new AddOrEditUserForm(mainLayout);
		mainLayout.setContent(addUserForm, true, false, CurrentPage.NONE);
	}

	private void openEditUserForm(int userId)
	{
		Parent editUserForm = new AddOrEditUserForm(mainLayout, userId);
		mainLayout.setContent(editUserForm, true, false, CurrentPage.NONE);
	}

	private void deleteUser(int userId)
	{
		try
		{
			uc.delete(userId);
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
