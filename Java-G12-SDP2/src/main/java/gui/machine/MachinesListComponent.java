package gui.machine;

import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.machine.MachineController;
import domain.machine.MachineDTO;
import domain.site.SiteController;
import domain.user.UserController;
import gui.MainLayout;
import gui.customComponents.CustomInformationBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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

public class MachinesListComponent extends GridPane
{

	private TableView<MachineDTO> machineTable;
	private MachineController machineController;
	private SiteController siteController;
	private UserController userController;
	private final MainLayout mainLayout;

	private Button addButton;

	public MachinesListComponent(MainLayout mainLayout)
	{
		this.mainLayout = mainLayout;
		this.machineTable = new TableView<>();
		this.machineController = mainLayout.getServices().getMachineController();
		this.siteController = mainLayout.getServices().getSiteController();
		this.userController = mainLayout.getServices().getUserController();
		initializeGUI();
	}

	private void initializeGUI()
	{
		this.getStylesheets().add(getClass().getResource("/css/tablePane.css").toExternalForm());

		this.getChildren().add(createTitleSection());

		machineTable = new TableView<>();

		buildColumns();

		addButton = new Button("Machine toevoegen +");
		addButton.getStyleClass().add("add-button");
		addButton.setOnAction(e -> openAddMachineForm());

		GridPane.setHalignment(addButton, HPos.RIGHT);
		GridPane.setMargin(addButton, new Insets(0, 0, 10, 0));

		add(addButton, 0, 0);
		add(machineTable, 0, 1);

		GridPane.setHgrow(machineTable, Priority.ALWAYS);
		GridPane.setVgrow(machineTable, Priority.ALWAYS);

	}

	private void buildColumns()
	{
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

		TableColumn<MachineDTO, String> statusCol = new TableColumn<>("Machinestatus");
		statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().machineStatus().toString()));

		TableColumn<MachineDTO, String> prodStatusCol = new TableColumn<>("Productiestatus");
		prodStatusCol
				.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().productionStatus().toString()));

		TableColumn<MachineDTO, String> maintenanceCol = new TableColumn<>("Onderhoud gepland");
		maintenanceCol
				.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().futureMaintenance().toString()));

		TableColumn<MachineDTO, Void> editCol = new TableColumn<>("Bewerken");
		editCol.setCellFactory(param -> {
			TableCell<MachineDTO, Void> cell = new TableCell<MachineDTO, Void>()
			{
				private final Button editButton = new Button();

				{
					FontIcon editIcon = new FontIcon("fas-pen");
					editIcon.setIconSize(20);
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

			private final Button onderhoudButton = new Button();
			{
				FontIcon wrenchIcon = new FontIcon("fas-tools");

				wrenchIcon.setIconSize(20);
				onderhoudButton.setGraphic(wrenchIcon);
				onderhoudButton.setBackground(Background.EMPTY);
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

		List<MachineDTO> dtos = machineController.getMachineList();
		machineTable.getItems().setAll(dtos);
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

		Label title = new Label("Machineoverzicht");
		title.getStyleClass().add("title-label");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, title, spacer);

		HBox infoBox = new CustomInformationBox("Hieronder vindt u een overzicht van alle machines.");
		VBox.setMargin(infoBox, new Insets(20, 0, 10, 0));

		return new VBox(10, hbox, infoBox);
	}

	private void openAddMachineForm()
	{
		Parent addMachineForm = new AddOrEditMachineForm(mainLayout, null);
		mainLayout.setContent(addMachineForm, true, false);
	}

	private void openEditMachineForm(MachineDTO machine)
	{
		Parent editMachineForm = new AddOrEditMachineForm(mainLayout, machine);
		mainLayout.setContent(editMachineForm, true, false);
	}

}
