package gui.site;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.machine.MachineDTO;
import domain.site.SiteController;
import domain.site.SiteDTO;
import domain.user.User;
import gui.MainLayout;
import gui.customComponents.CustomButton;
import gui.customComponents.CustomInformationBox;
import interfaces.Observer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import repository.SiteRepository;

public class SiteDetailsComponent extends VBox implements Observer {
	private final MainLayout mainLayout;
	private SiteController sc;
	private SiteRepository siteRepo;


	private ComboBox<String> locationFilter;
	private ComboBox<String> statusFilter;
	private ComboBox<String> productionStatusFilter;
	private ComboBox<String> technicianFilter;

	private final int siteId;
	private final SiteDTO site;

	// Table with machines
	private TableView<MachineDTO> table;
	private TextField searchField;
	private List<MachineDTO> allMachines;
	private List<MachineDTO> filteredMachines;

	// Pagination variables
	private int itemsPerPage = 10;
	private int currentPage = 0;
	private int totalPages = 0;
	private Pagination pagination;

	public SiteDetailsComponent(MainLayout mainLayout, int siteId) {
		this.mainLayout = mainLayout;
		this.sc = mainLayout.getServices().getSiteController();
		this.siteRepo = mainLayout.getServices().getSiteRepo();

		this.siteId = siteId;
		this.site = sc.getSite(siteId);

		// Fix the observer registration - use 'this' instead of null
		this.siteRepo.addObserver(this);
		this.table = new TableView<>();
		initializeGUI();
		loadMachines();
	}

	private void loadMachines() {
	    allMachines = sc.getSite(this.siteId).machines().stream().toList();
	    filteredMachines = allMachines;
	    updateFilterOptions();
	    updateTable(allMachines);
	}

	private void initializeGUI() {
		allMachines = sc.getSite(siteId).machines().stream().toList();
		filteredMachines = allMachines;

		VBox titleSection = createTitleSection();
		VBox tableSection = createTableSection();
		this.setSpacing(20);

		this.getChildren().addAll(titleSection, tableSection);
		updateTable(filteredMachines);
	}
	
	private VBox createTitleSection() {
		HBox windowHeader = createWindowHeader();

		// TODO Replace User with UserDTO as suggested in the comment
		User verantwoordelijke = site.verantwoordelijke();

		HBox informationBox1 = new CustomInformationBox(
				"Hieronder vindt u een overzicht van alle machines voor deze site. Klik op een machine om de details te bekijken!");

		HBox informationBox2 = new CustomInformationBox("Verantwoordelijke: %s %s"
				.formatted(verantwoordelijke.getFirstName(), verantwoordelijke.getLastName()));
		return new VBox(10, windowHeader, informationBox1, informationBox2);
	}
	
	private HBox createWindowHeader() {
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.setSpacing(10);

		// Back button
		Button backButton = new Button();
		FontIcon icon = new FontIcon("fas-arrow-left");
		icon.setIconSize(20);
		backButton.setGraphic(icon);
		backButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
		backButton.setOnAction(e -> mainLayout.showSitesList());

		Label title = new Label("Site Details");
		title.setStyle("-fx-font: 40 arial;");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		Button addButton = new CustomButton(new FontIcon("fas-plus"), "Machine toevoegen");
		addButton.setOnAction(e -> openAddMachineForm());

		hbox.getChildren().addAll(backButton, title, spacer, addButton);
		return hbox;
	}

	private VBox createTableSection() {
		HBox filterBox = createTableHeaders();

		TableColumn<MachineDTO, Void> editColumn = new TableColumn<>("");
		editColumn.setCellFactory(param -> new TableCell<MachineDTO, Void>() {
			private final Button editButton = new Button();
			{
				FontIcon editIcon = new FontIcon("fas-pen");
				editIcon.setIconSize(12);
				editButton.setGraphic(editIcon);
				editButton.setBackground(Background.EMPTY);
				editButton.setOnAction(event -> openEditMachineForm(getTableRow().getItem()));
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : editButton);
			}
		});

		TableColumn<MachineDTO, Number> col1 = new TableColumn<>("Nr.");
		col1.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()));

		TableColumn<MachineDTO, String> col2 = new TableColumn<>("Locatie");
		col2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().location()));

		TableColumn<MachineDTO, String> col3 = new TableColumn<>("Status");
		col3.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().machineStatus().toString()));

		TableColumn<MachineDTO, String> col4 = new TableColumn<>("Productiestatus");
		col4.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().productionStatus().toString()));

		TableColumn<MachineDTO, String> col5 = new TableColumn<>("Technieker");
		col5.setCellValueFactory(data -> {
			User technician = data.getValue().technician();
			return new SimpleStringProperty(technician.getFirstName());
		});

		TableColumn<MachineDTO, String> showColumn = new TableColumn<>("");
		showColumn.setCellFactory(param -> new TableCell<MachineDTO, String>() {
			private final Button viewButton = new Button("Bekijk");
			{
				viewButton.setOnAction(event -> showMachineDetails(getTableRow().getItem()));
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : viewButton);
			}
		});

		table.getColumns().add(editColumn);
		table.getColumns().addAll(col1, col2, col3, col4, col5);
		table.getColumns().add(showColumn);

		table.setPrefHeight(300);

		// Create pagination control
		pagination = createPagination();
		VBox tableWithPagination = new VBox(10, table, pagination);
		VBox.setVgrow(table, Priority.ALWAYS);

		return new VBox(10, filterBox, tableWithPagination);
	}

	private Pagination createPagination() {
		updateTotalPages();
		Pagination pagination = new Pagination(Math.max(1, totalPages), 0);
		pagination.setPageFactory(this::createPage);
		pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
			currentPage = newIndex.intValue();
			updateTableItems();
		});
		return pagination;
	}
	
	private HBox createPage(int pageIndex) {
		return new HBox();
	}
	
	private void updatePagination() {
		updateTotalPages();
		pagination.setPageCount(Math.max(1, totalPages));
		pagination.setCurrentPageIndex(Math.min(currentPage, Math.max(0, totalPages - 1)));
	}
	
	private void updateTotalPages() {
		totalPages = (int) Math.ceil((double) filteredMachines.size() / itemsPerPage);
	}

	private HBox createTableHeaders() {
	    searchField = new TextField();
	    searchField.setPromptText("Zoeken...");
	    searchField.setMaxWidth(300);
	    searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable());

	    // Location filter
	    locationFilter = new ComboBox<>();
	    locationFilter.setPromptText("Locatie");
	    locationFilter.setPrefWidth(150);
	    locationFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

	    // Status filter
	    statusFilter = new ComboBox<>();
	    statusFilter.setPromptText("Status");
	    statusFilter.setPrefWidth(150);
	    statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

	    // Production status filter
	    productionStatusFilter = new ComboBox<>();
	    productionStatusFilter.setPromptText("Productiestatus");
	    productionStatusFilter.setPrefWidth(150);
	    productionStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

	    // Technician filter
	    technicianFilter = new ComboBox<>();
	    technicianFilter.setPromptText("Technieker");
	    technicianFilter.setPrefWidth(200);
	    technicianFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

	    Region spacer = new Region();
	    HBox.setHgrow(spacer, Priority.ALWAYS);

	    // Page selector component
	    HBox pageSelector = createPageSelector();

	    HBox filterBox = new HBox(10, searchField, locationFilter, statusFilter, 
	            productionStatusFilter, technicianFilter, spacer, pageSelector);
	    filterBox.setAlignment(Pos.CENTER_LEFT);
	    return filterBox;
	}
	
	private void updateFilterOptions() {
	    // Location filter options
	    List<String> locations = new ArrayList<>();
	    locations.add(null);
	    locations.addAll(allMachines.stream()
	            .map(MachineDTO::location)
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList()));
	    locationFilter.setItems(FXCollections.observableArrayList(locations));

	    // Status filter options
	    List<String> statuses = new ArrayList<>();
	    statuses.add(null);
	    statuses.addAll(allMachines.stream()
	            .map(m -> m.machineStatus().toString())
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList()));
	    statusFilter.setItems(FXCollections.observableArrayList(statuses));

	    // Production status filter options
	    List<String> productionStatuses = new ArrayList<>();
	    productionStatuses.add(null);
	    productionStatuses.addAll(allMachines.stream()
	            .map(m -> m.productionStatus().toString())
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList()));
	    productionStatusFilter.setItems(FXCollections.observableArrayList(productionStatuses));

	    // Technician filter options
	    List<String> technicians = new ArrayList<>();
	    technicians.add(null);
	    technicians.addAll(allMachines.stream()
	            .map(m -> m.technician().getFullName())
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList()));
	    technicianFilter.setItems(FXCollections.observableArrayList(technicians));
	}
	
	private HBox createPageSelector() {
		Label lblItemsPerPage = new Label("Aantal per pagina:");

		ComboBox<Integer> comboItemsPerPage = new ComboBox<>(FXCollections.observableArrayList(10, 20, 50, 100));

		comboItemsPerPage.setValue(itemsPerPage); // Default value
		comboItemsPerPage.setOnAction(e -> {
			int selectedValue = comboItemsPerPage.getValue();
			updateItemsPerPage(selectedValue);
		});

		HBox pageSelector = new HBox(10, lblItemsPerPage, comboItemsPerPage);
		pageSelector.setAlignment(Pos.CENTER_RIGHT);
		return pageSelector;
	}
	
	private void updateItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
		this.currentPage = 0; // Reset to first page when changing items per page
		updatePagination();
		updateTableItems();
	}

	private void updateTable(List<MachineDTO> machines) {
		filteredMachines = machines;
		currentPage = 0;
		updatePagination();
		updateTableItems();
	}
	
	private void filterTable() {
	    String searchQuery = searchField.getText().toLowerCase();
	    String selectedLocation = locationFilter.getValue();
	    String selectedStatus = statusFilter.getValue();
	    String selectedProductionStatus = productionStatusFilter.getValue();
	    String selectedTechnician = technicianFilter.getValue();

	    filteredMachines = allMachines.stream()
	            .filter(machine -> {
	                boolean matchesSearch = machine.location().toLowerCase().contains(searchQuery) ||
	                        machine.machineStatus().toString().toLowerCase().contains(searchQuery) ||
	                        machine.productionStatus().toString().toLowerCase().contains(searchQuery) ||
	                        machine.technician().getFullName().toLowerCase().contains(searchQuery);

	                boolean matchesLocation = selectedLocation == null || 
	                        machine.location().equals(selectedLocation);
	                
	                boolean matchesStatus = selectedStatus == null || 
	                        machine.machineStatus().toString().equals(selectedStatus);
	                
	                boolean matchesProductionStatus = selectedProductionStatus == null || 
	                        machine.productionStatus().toString().equals(selectedProductionStatus);
	                
	                boolean matchesTechnician = selectedTechnician == null || 
	                        machine.technician().getFullName().equals(selectedTechnician);

	                return matchesSearch && matchesLocation && matchesStatus && 
	                       matchesProductionStatus && matchesTechnician;
	            })
	            .collect(Collectors.toList());

	    currentPage = 0;
	    updatePagination();
	    updateTableItems();
	}

	private void updateTableItems() {
		int fromIndex = currentPage * itemsPerPage;
		int toIndex = Math.min(fromIndex + itemsPerPage, filteredMachines.size());

		if (filteredMachines.isEmpty()) {
			table.getItems().clear();
		} else {
			List<MachineDTO> currentPageItems = fromIndex < toIndex ? filteredMachines.subList(fromIndex, toIndex)
					: List.of();
			table.getItems().setAll(currentPageItems);
		}
	}

	private void openAddMachineForm() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Functionaliteit niet geïmplementeerd");
		alert.setHeaderText("Machine toevoegen");
		alert.setContentText("Deze functionaliteit moet nog worden geïmplementeerd.");
		alert.showAndWait();
	}

	private void openEditMachineForm(MachineDTO machine) {
		// Implementation for editing a machine
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Functionaliteit niet geïmplementeerd");
		alert.setHeaderText("Machine bewerken");
		alert.setContentText("Deze functionaliteit moet nog worden geïmplementeerd voor machine ID: " + machine.id());
		alert.showAndWait();
	}

	private void showMachineDetails(MachineDTO machine) {
		// Implementation for showing machine details
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Machine Details");
		alert.setHeaderText("Details van machine " + machine.id());
		
		String details = String.format(
			"ID: %d\nLocatie: %s\nStatus: %s\nProductiestatus: %s\nTechnieker: %s %s",
			machine.id(),
			machine.location(),
			machine.machineStatus().toString(),
			machine.productionStatus().toString(),
			machine.technician().getFirstName(),
			machine.technician().getLastName()
		);
		
		alert.setContentText(details);
		alert.showAndWait();
	}

	private void deleteMachine(MachineDTO machineDTO) {
		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.setTitle("Bevestig verwijderen");
		confirmation.setHeaderText("Machine verwijderen");
		confirmation.setContentText("Weet u zeker dat u machine met ID '" + machineDTO.id() + "' wilt verwijderen?");

		confirmation.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Functionaliteit niet geïmplementeerd");
					alert.setHeaderText("Machine verwijderen");
					alert.setContentText("Deze functionaliteit moet nog worden geïmplementeerd.");
					alert.showAndWait();
				} catch (Exception e) {
					System.err.println("Fout bij verwijderen: " + e.getMessage());
					e.printStackTrace();

					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Fout bij verwijderen");
						alert.setHeaderText("Kan machine niet verwijderen");
						alert.setContentText(e.toString());
						alert.showAndWait();
					});
				}
			}
		});
	}

	@Override
	public void update() {
	    Platform.runLater(() -> {
	        SiteDTO updatedSite = sc.getSite(siteId);
	        allMachines = updatedSite.machines().stream().toList();
	        filteredMachines = new ArrayList<>(allMachines);
	        updateFilterOptions();
	        updateTable(filteredMachines);
	    });
	}
}