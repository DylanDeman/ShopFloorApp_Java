package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.kordamp.ikonli.javafx.FontIcon;
import domain.SiteController;
import dto.MachineDTO;
import dto.SiteDTOWithMachines;
import dto.UserDTO;
import interfaces.Observer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import util.CurrentPage;

public class SiteDetailsComponent extends VBox implements Observer
{
	private final MainLayout mainLayout;
	private SiteController sc;

	private ComboBox<String> locationFilter;
	private ComboBox<String> statusFilter;
	private ComboBox<String> productionStatusFilter;
	private ComboBox<String> technicianFilter;

	private final int siteId;
	private final SiteDTOWithMachines site;

	private TableView<MachineDTO> table;
	private TextField searchField;
	private List<MachineDTO> allMachines;
	private List<MachineDTO> filteredMachines;

	private TableColumn<MachineDTO, Void> editColumn;
	private TableColumn<MachineDTO, Number> idColumn;
	private TableColumn<MachineDTO, String> locationColumn;
	private TableColumn<MachineDTO, String> statusColumn;
	private TableColumn<MachineDTO, String> productionStatusColumn;
	private TableColumn<MachineDTO, String> technicianColumn;
	private TableColumn<MachineDTO, String> viewColumn;

	// Pagination variables
	private int itemsPerPage = 10;
	private int currentPage = 0;
	private int totalPages = 0;
	private Pagination pagination;

	public SiteDetailsComponent(MainLayout mainLayout, int siteId)
	{
		this.mainLayout = mainLayout;
		this.sc = mainLayout.getServices().getSiteController();

		this.siteId = siteId;
		this.site = sc.getSite(siteId);

		this.sc.addObserver(this);

		this.table = new TableView<>();
		table.setPlaceholder(new Label("Geen machines beschikbaar voor deze site!"));
		table.setEditable(false);

		table.getStyleClass().add("machine-table");
		
		initializeGUI();
		loadMachines();

	}

	private void loadMachines()
	{
		try
		{
			SiteDTOWithMachines currentSite = sc.getSite(this.siteId);
			allMachines = currentSite.machines().stream().toList();
			filteredMachines = new ArrayList<>(allMachines);
			updateFilterOptions();
			updateTable(filteredMachines);
		} catch (Exception e)
		{
			System.err.println("Error loading machines: " + e.getMessage());
			e.printStackTrace();
			allMachines = new ArrayList<>();
			filteredMachines = new ArrayList<>();
			updateFilterOptions();
			updateTable(filteredMachines);
		}
	}

	private void initializeGUI()
	{
		this.getStylesheets().add(getClass().getResource("/css/tablePane.css").toExternalForm());

		allMachines = new ArrayList<>();
		filteredMachines = new ArrayList<>();

		VBox titleSection = createTitleSection();
		VBox tableSection = createTableSection();
		this.setSpacing(20);

		this.getChildren().addAll(titleSection, tableSection);
		configureTableLayout();
	}

	private void configureTableLayout()
	{
		table.setMinHeight(300);
		table.setPrefHeight(500);
		table.setMaxHeight(Double.MAX_VALUE);

		VBox.setVgrow(table, Priority.ALWAYS);
	}

	private VBox createTitleSection()
	{
		HBox windowHeader = createWindowHeader();

		UserDTO verantwoordelijke = site.verantwoordelijke();

		HBox informationBox1 = new CustomInformationBox(
				"Hieronder vindt u een overzicht van alle machines voor deze site. Klik op een machine om de details te bekijken!");

		HBox informationBox2 = new CustomInformationBox(
				"Verantwoordelijke: %s %s".formatted(verantwoordelijke.firstName(), verantwoordelijke.lastName()));
		return new VBox(10, windowHeader, informationBox1, informationBox2);
	}

	private HBox createWindowHeader()
	{
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.setSpacing(10);

		// Back button
		Button backButton = new Button();
		FontIcon icon = new FontIcon("fas-arrow-left");
		icon.setIconSize(20);
		backButton.setGraphic(icon);
		backButton.getStyleClass().add("back-button");
		backButton.setOnAction(e -> mainLayout.showSitesList());

		Label title = new Label("Site Details");
		title.getStyleClass().add("title-label");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// TODO met icon werken hier:
		Button addButton = new Button("+ Machine toevoegen");
		addButton.setOnAction(e -> openAddMachineForm());
		addButton.getStyleClass().add("add-button");

		hbox.getChildren().addAll(backButton, title, spacer, addButton);
		return hbox;
	}

	private VBox createTableSection()
	{
		HBox filterBox = createTableHeaders();

		createTableColumns();

		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		editColumn.setPrefWidth(40);
		idColumn.setPrefWidth(60);
		locationColumn.setPrefWidth(150);
		statusColumn.setPrefWidth(120);
		productionStatusColumn.setPrefWidth(150);
		technicianColumn.setPrefWidth(150);
		viewColumn.setPrefWidth(80);

		table.setPrefWidth(Region.USE_COMPUTED_SIZE);
		table.setMinWidth(Region.USE_COMPUTED_SIZE);
		table.setMaxWidth(Double.MAX_VALUE);

		table.setPrefHeight(400);

		pagination = createPagination();
		VBox tableWithPagination = new VBox(10, table, pagination);
		VBox.setVgrow(table, Priority.ALWAYS);

		return new VBox(10, filterBox, tableWithPagination);
	}

	private void createTableColumns()
	{

		editColumn = new TableColumn<>("");
		editColumn.setCellFactory(param -> new TableCell<MachineDTO, Void>()
		{
			private final Button editButton = new Button();
			{
				FontIcon editIcon = new FontIcon("fas-pen");
				editIcon.setIconSize(12);
				editButton.setGraphic(editIcon);
				editButton.setBackground(Background.EMPTY);
				editButton.setOnAction(event ->
				{
					MachineDTO machine = getTableRow().getItem();
					if (machine != null)
					{
						openEditMachineForm(machine);
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				if (empty || getTableRow() == null || getTableRow().getItem() == null)
				{
					setGraphic(null);
				} else
				{
					setGraphic(editButton);
				}
			}
		});

		idColumn = new TableColumn<>("Nr.");
		idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()));

		locationColumn = new TableColumn<>("Locatie");
		locationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().location()));

		statusColumn = new TableColumn<>("Status");
		statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().machineStatus().toString()));

		productionStatusColumn = new TableColumn<>("Productiestatus");
		productionStatusColumn
				.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().productionStatus().toString()));

		technicianColumn = new TableColumn<>("Technieker");
		technicianColumn.setCellValueFactory(data ->
		{
			UserDTO technician = data.getValue().technician();
			return new SimpleStringProperty(technician != null ? technician.firstName() : "");
		});

		viewColumn = new TableColumn<>("");
		viewColumn.setCellFactory(param -> new TableCell<MachineDTO, String>()
		{
			private final Button viewButton = new Button("Bekijk");
			{
				viewButton.setOnAction(event ->
				{
					MachineDTO machine = getTableRow().getItem();
					if (machine != null)
					{
						showMachineDetails(machine);
					}
				});
			}

			@Override
			protected void updateItem(String item, boolean empty)
			{
				super.updateItem(item, empty);
				if (empty || getTableRow() == null || getTableRow().getItem() == null)
				{
					setGraphic(null);
				} else
				{
					setGraphic(viewButton);
				}
			}
		});

		table.getColumns().add(idColumn);
		table.getColumns().add(locationColumn);
		table.getColumns().add(statusColumn);
		table.getColumns().add(productionStatusColumn);
		table.getColumns().add(technicianColumn);
		table.getColumns().add(editColumn);
		table.getColumns().add(viewColumn);
	}

	private Pagination createPagination()
	{
		updateTotalPages();
		Pagination pagination = new Pagination(Math.max(1, totalPages), 0);
		pagination.setPageFactory(this::createPage);
		pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
		{
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
		int maxPageIndex = Math.max(0, totalPages - 1);
		pagination.setCurrentPageIndex(Math.min(currentPage, maxPageIndex));
	}

	private void updateTotalPages()
	{
		totalPages = (int) Math.ceil((double) filteredMachines.size() / itemsPerPage);
		if (totalPages < 1)
		{
			totalPages = 1; // Always at least one page
		}
	}

	private HBox createTableHeaders()
	{
		searchField = new TextField();
		searchField.setPromptText("Zoeken...");
		searchField.setMaxWidth(300);
		searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable());

		locationFilter = new ComboBox<>();
		locationFilter.setPromptText("Locatie");
		locationFilter.setPrefWidth(150);
		locationFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

		statusFilter = new ComboBox<>();
		statusFilter.setPromptText("Status");
		statusFilter.setPrefWidth(150);
		statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

		productionStatusFilter = new ComboBox<>();
		productionStatusFilter.setPromptText("Productiestatus");
		productionStatusFilter.setPrefWidth(150);
		productionStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

		technicianFilter = new ComboBox<>();
		technicianFilter.setPromptText("Technieker");
		technicianFilter.setPrefWidth(200);
		technicianFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox pageSelector = createPageSelector();

		HBox filterBox = new HBox(10, searchField, locationFilter, statusFilter, productionStatusFilter,
				technicianFilter, spacer, pageSelector);
		filterBox.setAlignment(Pos.CENTER_LEFT);
		return filterBox;
	}

	private void updateFilterOptions()
	{

		List<String> locations = new ArrayList<>();
		locations.add(null); // null option for "All"
		locations.addAll(allMachines.stream().map(MachineDTO::location).filter(loc -> loc != null && !loc.isEmpty())
				.distinct().sorted().collect(Collectors.toList()));
		locationFilter.setItems(FXCollections.observableArrayList(locations));

		List<String> statuses = new ArrayList<>();
		statuses.add(null); // null option for "All"
		statuses.addAll(allMachines.stream().map(m -> m.machineStatus().toString()).distinct().sorted()
				.collect(Collectors.toList()));
		statusFilter.setItems(FXCollections.observableArrayList(statuses));

		List<String> productionStatuses = new ArrayList<>();
		productionStatuses.add(null); // null option for "All"
		productionStatuses.addAll(allMachines.stream().map(m -> m.productionStatus().toString()).distinct().sorted()
				.collect(Collectors.toList()));
		productionStatusFilter.setItems(FXCollections.observableArrayList(productionStatuses));

		List<String> technicians = new ArrayList<>();
		technicians.add(null); // null option for "All"
		technicians.addAll(allMachines.stream().map(m -> m.technician().firstName())
				.filter(name -> name != null && !name.isEmpty()).distinct().sorted().collect(Collectors.toList()));
		technicianFilter.setItems(FXCollections.observableArrayList(technicians));
	}

	private HBox createPageSelector()
	{
		Label lblItemsPerPage = new Label("Aantal per pagina:");

		ComboBox<Integer> comboItemsPerPage = new ComboBox<>(FXCollections.observableArrayList(10, 20, 50, 100));

		comboItemsPerPage.setValue(itemsPerPage);
		comboItemsPerPage.setOnAction(e ->
		{
			int selectedValue = comboItemsPerPage.getValue();
			updateItemsPerPage(selectedValue);
		});

		HBox pageSelector = new HBox(10, lblItemsPerPage, comboItemsPerPage);
		pageSelector.setAlignment(Pos.CENTER_RIGHT);
		return pageSelector;
	}

	private void updateItemsPerPage(int itemsPerPage)
	{
		this.itemsPerPage = itemsPerPage;
		this.currentPage = 0;
		updatePagination();
		updateTableItems();
	}

	private void updateTable(List<MachineDTO> machines)
	{
		filteredMachines = new ArrayList<>(machines);
		currentPage = 0;
		updatePagination();
		updateTableItems();
	}

	private void filterTable()
	{
		String searchQuery = searchField.getText().toLowerCase().trim();
		String selectedLocation = locationFilter.getValue();
		String selectedStatus = statusFilter.getValue();
		String selectedProductionStatus = productionStatusFilter.getValue();
		String selectedTechnician = technicianFilter.getValue();

		List<MachineDTO> newFilteredList = sc.getFilteredMachines(siteId, searchQuery, selectedLocation, selectedStatus, selectedProductionStatus, selectedTechnician);

		filteredMachines = newFilteredList;
		currentPage = 0;
		updatePagination();
		updateTableItems();
	}

	private void updateTableItems()
	{
		int fromIndex = currentPage * itemsPerPage;
		int toIndex = Math.min(fromIndex + itemsPerPage, filteredMachines.size());

		if (filteredMachines.isEmpty())
		{
			table.setItems(FXCollections.observableArrayList());
			System.out.println("No machines to display");
		} else
		{
			List<MachineDTO> currentPageItems;
			if (fromIndex < toIndex)
			{
				currentPageItems = filteredMachines.subList(fromIndex, toIndex);
			} else
			{
				currentPageItems = List.of();
			}

			ObservableList<MachineDTO> items = FXCollections.observableArrayList(currentPageItems);
			table.setItems(items);
		}
	}

	private void openAddMachineForm()
	{
		Parent addMachineForm = new AddOrEditMachineForm(mainLayout);
		mainLayout.setContent(addMachineForm, true, false, CurrentPage.NONE);
	}

	private void openEditMachineForm(MachineDTO machine)
	{
		Parent editMachineForm = new AddOrEditMachineForm(mainLayout, machine.id());
		mainLayout.setContent(editMachineForm, true, false, CurrentPage.NONE);
	}

	private void showMachineDetails(MachineDTO machine)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Machine Details");
		alert.setHeaderText("Details van machine " + machine.id());

		String details = String.format("ID: %d\nLocatie: %s\nStatus: %s\nProductiestatus: %s\nTechnieker: %s %s",
				machine.id(), machine.location(), machine.machineStatus().toString(),
				machine.productionStatus().toString(),
				machine.technician() != null ? machine.technician().firstName() : "N/A",
				machine.technician() != null ? machine.technician().lastName() : "");

		alert.setContentText(details);
		alert.showAndWait();
	}

	@Override
	public void update(String message)
	{
		Platform.runLater(() ->
		{
				SiteDTOWithMachines updatedSite = sc.getSite(siteId);
				allMachines = updatedSite.machines().stream().toList();
				filteredMachines = new ArrayList<>(allMachines);
				updateFilterOptions();
				updateTable(filteredMachines);
		});
	}
}