package gui.maintenance;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.machine.MachineDTO;
import domain.maintenance.MaintenanceController;
import domain.maintenance.MaintenanceDTO;
import gui.MainLayout;
import gui.customComponents.CustomButton;
import gui.customComponents.CustomInformationBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MaintenanceListComponent extends VBox
{
	private final MainLayout mainLayout;
	private MaintenanceController mc;
	private TableView<MaintenanceDTO> table;
	private TextField searchField;
	private List<MaintenanceDTO> allMaintenances;
	private List<MaintenanceDTO> filteredMaintenances;
	private MachineDTO machineDTO;

	private int itemsPerPage = 10;
	private int currentPage = 0;
	private int totalPages = 0;
	private Pagination pagination;

	public MaintenanceListComponent(MainLayout mainLayout)
	{
		this.mainLayout = mainLayout;
		this.mc = mainLayout.getServices().getMaintenanceController();
		this.table = new TableView<>();
		initializeGUI();
	}

	public MaintenanceListComponent(MainLayout mainLayout, MachineDTO machineDTO)
	{
		this.mc = mainLayout.getServices().getMaintenanceController();
		this.machineDTO = machineDTO;
		this.mainLayout = mainLayout;
		this.table = new TableView<>();
		initializeGUI();
	}

	private void initializeGUI()
	{

		allMaintenances = machineDTO == null ? mc.getMaintenances()
				: mc.getMaintenances().stream().filter((m) -> m.machine().equals(machineDTO)).toList();
		filteredMaintenances = allMaintenances;

		VBox titleSection = createTitleSection();
		VBox tableSection = createTableSection();

		this.setSpacing(20);

		this.getChildren().addAll(titleSection, tableSection);
		updateTable(filteredMaintenances);
	}

	private VBox createTitleSection()
	{
		HBox header = createWindowHeader();
		HBox infoBox = new CustomInformationBox("Hieronder vindt u een overzicht van alle onderhouden.");
		return machineDTO == null ? new VBox(10, header, infoBox)
				: new VBox(10, header, new CustomInformationBox(
						String.format("Hieronder vindt u de onderhouden van machine %d", machineDTO.id())));
	}

	private HBox createWindowHeader()
	{
		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.CENTER_LEFT);

		FontIcon icon = new FontIcon("fas-arrow-left");
		icon.setIconSize(20);
		Button backButton = new Button();
		backButton.setGraphic(icon);
		backButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
		backButton.setOnAction(e -> mainLayout.showHomeScreen());

		Label title = new Label("Onderhoudslijst");
		title.setStyle("-fx-font: 40 arial;");
		
		CustomButton maintenancePlanBtn = new CustomButton("Onderhoud inplannen");
		
		maintenancePlanBtn.setOnAction((e) -> goToMaintenancePlanning(mainLayout));

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, title, maintenancePlanBtn, spacer);
		return hbox;
	}

	private VBox createTableSection()
	{
		HBox filterBox = createTableHeaders();

		TableColumn<MaintenanceDTO, String> col1 = createColumn("Datum uitgevoerd", m -> m.executionDate().toString());
		TableColumn<MaintenanceDTO, String> col2 = createColumn("Starttijdstip",
				m -> m.startDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")).toString());
		TableColumn<MaintenanceDTO, String> col3 = createColumn("Eindtijdstip",
				m -> m.endDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")).toString());
		TableColumn<MaintenanceDTO, String> col4 = createColumn("Naam technieker",
				m -> m.technician() != null ? m.technician().getFullName() : "Onbekend");
		TableColumn<MaintenanceDTO, String> col5 = createColumn("Reden", MaintenanceDTO::reason);
		TableColumn<MaintenanceDTO, String> col6 = createColumn("Opmerkingen", MaintenanceDTO::comments);
		TableColumn<MaintenanceDTO, String> col7 = createColumn("Status", m -> m.status().toString());
		TableColumn<MaintenanceDTO, String> col8 = createColumn("Machine",
				m -> String.format("Machine %d", m.machine().id()));
		TableColumn<MaintenanceDTO, Void> col9 = createAddRapportButtonColumn();

		List<TableColumn<MaintenanceDTO, ?>> columns;
		if (machineDTO != null)
		{
			columns = List.of(col1, col2, col3, col4, col5, col6, col7, col9);
		} else
		{
			columns = List.of(col1, col2, col3, col4, col5, col6, col7, col8, col9);
		}
		table.getColumns().addAll(columns);
		table.setPrefHeight(500);

		pagination = createPagination();

		VBox tableWithPagination = new VBox(10, table, pagination);
		VBox.setVgrow(table, Priority.ALWAYS);

		return new VBox(10, filterBox, tableWithPagination);
	}

	private HBox createTableHeaders()
	{
		searchField = new TextField();
		searchField.setPromptText("Zoeken...");
		searchField.setMaxWidth(300);
		searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable(newVal));

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox pageSelector = createPageSelector();

		HBox filterBox = new HBox(10, searchField, spacer, pageSelector);
		filterBox.setAlignment(Pos.CENTER_LEFT);
		return filterBox;
	}

	private HBox createPageSelector()
	{
		Label lbl = new Label("Aantal per pagina:");
		ComboBox<Integer> combo = new ComboBox<>(FXCollections.observableArrayList(10, 20, 50, 100));
		combo.setValue(itemsPerPage);
		combo.setOnAction(e -> updateItemsPerPage(combo.getValue()));

		HBox box = new HBox(10, lbl, combo);
		box.setAlignment(Pos.CENTER_RIGHT);
		return box;
	}

	private void updateItemsPerPage(int value)
	{
		this.itemsPerPage = value;
		this.currentPage = 0;
		updatePagination();
		updateTableItems();
	}

	private void filterTable(String query)
	{
		String lowerCaseQuery = query.toLowerCase();
		filteredMaintenances = allMaintenances.stream().filter(m -> m.reason().toLowerCase().contains(lowerCaseQuery)
				|| m.comments().toLowerCase().contains(lowerCaseQuery)
				|| (m.technician() != null && m.technician().getFullName().toLowerCase().contains(lowerCaseQuery)))
				.collect(Collectors.toList());
		currentPage = 0;
		updatePagination();
		updateTableItems();
	}

	private TableColumn<MaintenanceDTO, String> createColumn(String title, Function<MaintenanceDTO, String> mapper)
	{
		TableColumn<MaintenanceDTO, String> col = new TableColumn<>(title);
		col.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
		return col;
	}

	private TableColumn<MaintenanceDTO, Void> createAddRapportButtonColumn()
	{
		TableColumn<MaintenanceDTO, Void> col = new TableColumn<>("Details");

		col.setCellFactory(param -> new TableCell<>()
		{
			private final CustomButton btn = new CustomButton("Details");
			{
				btn.setOnAction(e -> {
					MaintenanceDTO selectedMaintenance = getTableView().getItems().get(getIndex());
					goToDetails(mainLayout, selectedMaintenance);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				setGraphic(empty ? null : btn);
			}
		});
		return col;
	}

	private Pagination createPagination()
	{
		updateTotalPages();
		Pagination pagination = new Pagination(Math.max(1, totalPages), 0);
		pagination.setPageFactory(this::createPage);
		pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
			currentPage = newIndex.intValue();
			updateTableItems();
		});
		return pagination;
	}

	private HBox createPage(int pageIndex)
	{
		return new HBox();
	}

	private void updatePagination()
	{
		updateTotalPages();
		pagination.setPageCount(Math.max(1, totalPages));
		pagination.setCurrentPageIndex(Math.min(currentPage, Math.max(0, totalPages - 1)));
	}

	private void updateTotalPages()
	{
		totalPages = (int) Math.ceil((double) filteredMaintenances.size() / itemsPerPage);
	}

	private void updateTableItems()
	{
		int fromIndex = currentPage * itemsPerPage;
		int toIndex = Math.min(fromIndex + itemsPerPage, filteredMaintenances.size());

		if (filteredMaintenances.isEmpty())
		{
			table.getItems().clear();
		} else
		{
			List<MaintenanceDTO> currentPageItems = fromIndex < toIndex
					? filteredMaintenances.subList(fromIndex, toIndex)
					: List.of();
			table.getItems().setAll(currentPageItems);
		}
	}

	private void updateTable(List<MaintenanceDTO> list)
	{
		filteredMaintenances = list;
		updatePagination();
		updateTableItems();
	}

	/*
	 * private void goToAddRapport(MainLayout mainLayout, MaintenanceDTO
	 * maintenance) { AddReportForm form = new AddReportForm(mainLayout,
	 * maintenance);
	 * form.getStylesheets().add(getClass().getResource("/css/AddRapport.css").
	 * toExternalForm()); }
	 */
	
	private void goToMaintenancePlanning(MainLayout mainlayout) {
		mainLayout.showMaintenancePlanning(machineDTO);
	}

	private void goToDetails(MainLayout mainLayout, MaintenanceDTO maintenance)
	{
		MaintenanceDetailView form = new MaintenanceDetailView(mainLayout, maintenance);
		form.getStylesheets().add(getClass().getResource("/css/maintenanceDetails.css").toExternalForm());
		mainLayout.showMaintenanceDetails(maintenance);
	}

}
