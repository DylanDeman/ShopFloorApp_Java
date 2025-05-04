package gui.machine;

import java.util.List;

import domain.machine.MachineController;
import domain.machine.MachineDTO;
import domain.site.SiteController;
import domain.user.UserController;
import gui.MainLayout;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MachinesListComponent extends VBox
{

	private TableView<MachineDTO> machineTable;
	private MachineController machineController;
	private SiteController siteController;
	private UserController userController;
	private final MainLayout mainLayout;

	public MachinesListComponent(MainLayout mainLayout)
	{
		this.mainLayout = mainLayout;
		this.machineTable = new TableView<>();
		this.machineController = new MachineController();
		this.siteController = new SiteController();
		this.userController = new UserController();
		initializeGUI();
	}

	private void initializeGUI()
	{
		HBox titleSection = createTitleSection();
		this.getChildren().add(titleSection);

		machineTable = new TableView<>();

		TableColumn<MachineDTO, String> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().id())));

		TableColumn<MachineDTO, String> siteCol = new TableColumn<>("Site");
		siteCol.setCellValueFactory(data -> new SimpleStringProperty(
				data.getValue().site() != null ? data.getValue().site().siteName() : "Onbekend"));

		TableColumn<MachineDTO, String> technicianCol = new TableColumn<>("Technieker");
		technicianCol.setCellValueFactory(data -> new SimpleStringProperty(
				data.getValue().technician() != null ? data.getValue().technician().getFullName() : "Onbekend"));

		TableColumn<MachineDTO, String> productInfoCol = new TableColumn<>("Product info");
		productInfoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().productInfo()));

		TableColumn<MachineDTO, String> lastMaintenanceCol = new TableColumn<>("Laatste onderhoud");
		lastMaintenanceCol.setCellValueFactory(data -> new SimpleStringProperty(
				data.getValue().lastMaintenance() != null ? data.getValue().lastMaintenance().toString() : "Geen"));

		TableColumn<MachineDTO, String> daysSinceMaintenanceCol = new TableColumn<>("Dagen sinds laatste onderhoud");
		daysSinceMaintenanceCol.setCellValueFactory(
				data -> new SimpleStringProperty(String.valueOf(data.getValue().numberDaysSinceLastMaintenance())));

		TableColumn<MachineDTO, String> uptimeCol = new TableColumn<>("Uptime (uren)");
		uptimeCol.setCellValueFactory(
				data -> new SimpleStringProperty(String.format("%.2f", data.getValue().upTimeInHours())));

		TableColumn<MachineDTO, String> codeCol = new TableColumn<>("Code");
		codeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().code()));

		TableColumn<MachineDTO, String> locationCol = new TableColumn<>("Locatie");
		locationCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().location()));

		TableColumn<MachineDTO, String> statusCol = new TableColumn<>("Status");
		statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));

		TableColumn<MachineDTO, String> prodStatusCol = new TableColumn<>("Productie");
		prodStatusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().productieStatus()));

		TableColumn<MachineDTO, String> maintenanceCol = new TableColumn<>("Onderhoud gepland");
		maintenanceCol
				.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().futureMaintenance().toString()));

		TableColumn<MachineDTO, Void> editCol = new TableColumn<>("Bewerken");
		editCol.setCellFactory(param -> {
			TableCell<MachineDTO, Void> cell = new TableCell<MachineDTO, Void>()
			{
				private final Button editButton = new Button();

				{
					ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
					editIcon.setFitWidth(16);
					editIcon.setFitHeight(16);
					editButton.setGraphic(editIcon);
					editButton.setBackground(Background.EMPTY);
					editButton.setOnAction(event -> {
						MachineDTO selectedMachine = getTableView().getItems().get(getIndex());
						openEditMachineForm(selectedMachine);
					});
				}

				@Override
				public void updateItem(Void item, boolean empty)
				{
					super.updateItem(item, empty);
					if (empty)
					{
						setGraphic(null);
					} else
					{
						setGraphic(editButton);
					}
				}
			};
			return cell;
		});

		TableColumn<MachineDTO, Void> onderhoudCol = new TableColumn<>("Onderhouden");
		onderhoudCol.setCellFactory(param -> new TableCell<>()
		{

			private final Button onderhoudButton = new Button("🔧");

			{
				onderhoudButton.setOnAction(event -> {
					MachineDTO selectedMachine = getTableView().getItems().get(getIndex());
					System.out.println("Onderhoud for machine: " + selectedMachine.code());
					mainLayout.showMaintenanceList();
				});
				onderhoudButton.setStyle("-fx-background-color: transparent;");
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				if (empty)
				{
					setGraphic(null);
				} else
				{
					setGraphic(onderhoudButton);
				}
			}
		});

		machineTable.getColumns().addAll(editCol, onderhoudCol);

		machineTable.getColumns().addAll(idCol, codeCol, locationCol, statusCol, prodStatusCol, maintenanceCol, siteCol,
				technicianCol, productInfoCol, lastMaintenanceCol, daysSinceMaintenanceCol, uptimeCol);
		machineTable.setPrefHeight(300);

		List<MachineDTO> dtos = machineController.getMachineList();
		machineTable.getItems().setAll(dtos);

		Button backButton = new Button("Terug naar keuzescherm");
		backButton.setOnAction(event -> mainLayout.showHomeScreen());

		backButton.setPrefWidth(200);
		backButton.setStyle("-fx-font-size: 14px;");

		HBox buttonSection = new HBox(backButton);
		buttonSection.setAlignment(Pos.CENTER);
		buttonSection.setSpacing(10);

		this.getChildren().addAll(machineTable, backButton);

	}

	private HBox createTitleSection()
	{
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.setSpacing(10);
		hbox.setPadding(new Insets(0, 0, 20, 0));

		Label title = new Label("Machines");
		title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		Button addButton = new Button("➕ Machine toevoegen");
		addButton.setOnAction(event -> openAddMachineForm());
		addButton.setPrefWidth(200);
		String buttonStyle = "-fx-background-color: #f0453c; " + "-fx-text-fill: white; " + "-fx-font-weight: bold; "
				+ "-fx-padding: 8 15 8 15; " + "-fx-background-radius: 5;";
		addButton.setStyle(buttonStyle);

		hbox.getChildren().addAll(title, spacer, addButton);
		return hbox;
	}

	private void openAddMachineForm()
	{
		Parent addMachineForm = new AddOrEditMachineForm(mainLayout, machineController, null, siteController,
				userController);
		mainLayout.setContent(addMachineForm, true, false);
	}

	private void openEditMachineForm(MachineDTO machine)
	{
		Parent editMachineForm = new AddOrEditMachineForm(mainLayout, machineController, machine, siteController,
				userController);
		mainLayout.setContent(editMachineForm, true, false);
	}

}
