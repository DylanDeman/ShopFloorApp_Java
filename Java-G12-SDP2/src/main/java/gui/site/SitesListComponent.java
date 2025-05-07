package gui.site;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.kordamp.ikonli.javafx.FontIcon;
import domain.site.Site;
import domain.site.SiteController;
import domain.site.SiteDTO;
import gui.AddOrEditSiteForm;
import gui.MainLayout;
import gui.customComponents.CustomButton;
import gui.customComponents.CustomInformationBox;
import interfaces.Observer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import repository.SiteRepository;

public class SitesListComponent extends VBox implements Observer {
    private final MainLayout mainLayout;
    private SiteController sc;
    private SiteRepository siteRepo;

    private TableView<SiteDTO> table;
    private TextField searchField;

    private ComboBox<String> statusFilter;
    private ComboBox<String> nameFilter;
    private ComboBox<String> verantwoordelijkeFilter;
    private TextField minMachinesField;
    private TextField maxMachinesField;
    private List<SiteDTO> allSites;
    private List<SiteDTO> filteredSites;

    private int itemsPerPage = 10;
    private int currentPage = 0;
    private int totalPages = 0;
    private Pagination pagination;

    public SitesListComponent(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
        this.sc = mainLayout.getServices().getSiteController();
        this.siteRepo = mainLayout.getServices().getSiteRepo();
        this.siteRepo.addObserver(this);
        this.table = new TableView<>();
        initializeGUI();
        loadSites();
    }

    private void loadSites() {
        allSites = sc.getSites();
        filteredSites = allSites;
        updateFilterOptions();
        updateTable(allSites);
    }

    private void initializeGUI() {
        allSites = sc.getSites();
        filteredSites = allSites;

        VBox titleSection = createTitleSection();
        VBox tableSection = createTableSection();

        this.setSpacing(20);
        this.getChildren().addAll(titleSection, tableSection);

        updateFilterOptions();
        updateTable(filteredSites);
    }

    private VBox createTitleSection() {
        HBox windowHeader = createWindowHeader();
        HBox informationBox = new CustomInformationBox(
                "Hieronder vindt u een overzicht van alle sites. Klik op een site om de details van de site te bekijken!");
        return new VBox(10, windowHeader, informationBox);
    }

    private HBox createWindowHeader() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);

        Button backButton = new Button();
        FontIcon icon = new FontIcon("fas-arrow-left");
        icon.setIconSize(20);
        backButton.setGraphic(icon);
        backButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        backButton.setOnAction(e -> mainLayout.showHomeScreen());

        Label title = new Label("Sites");
        title.setStyle("-fx-font: 40 arial;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = new CustomButton(new FontIcon("fas-plus"), "Site toevoegen");
        addButton.setOnAction(e -> openAddSiteForm());

        hbox.getChildren().addAll(backButton, title, spacer, addButton);
        return hbox;
    }

    private VBox createTableSection() {
        HBox filterBox = createTableHeaders();

        // Edit Column
        TableColumn<SiteDTO, Void> editColumn = new TableColumn<>("");
        editColumn.setMaxWidth(50);
        editColumn.setMinWidth(50);
        editColumn.setCellFactory(param -> new TableCell<SiteDTO, Void>() {
            private final Button editButton = new Button();
            {
                FontIcon editIcon = new FontIcon("fas-pen");
                editIcon.setIconSize(12);
                editButton.setGraphic(editIcon);
                editButton.setBackground(Background.EMPTY);
                editButton.setOnAction(event -> {
                    SiteDTO site = getTableRow().getItem();
                    if (site != null) {
                        // Using the controller to get the site object
                        openEditSiteForm(sc.getSiteObject(site));
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });

        // ID Column
        TableColumn<SiteDTO, Number> col1 = new TableColumn<>("Nr.");
        col1.setMaxWidth(70);
        col1.setMinWidth(70);
        col1.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()));

        // Name Column
        TableColumn<SiteDTO, String> col2 = new TableColumn<>("Naam");
        col2.setPrefWidth(200);
        col2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().siteName()));

        // Verantwoordelijke Column
        TableColumn<SiteDTO, String> col3 = new TableColumn<>("Verantwoordelijke");
        col3.setPrefWidth(200);
        col3.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().verantwoordelijke().getFullName()));

        // Status Column
        TableColumn<SiteDTO, String> col4 = new TableColumn<>("Status");
        col4.setPrefWidth(100);
        col4.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status().toString()));

        // Machines Count Column
        TableColumn<SiteDTO, Number> col5 = new TableColumn<>("Aantal machines");
        col5.setPrefWidth(150);
        col5.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().machines().size()));

        // Details Column
        TableColumn<SiteDTO, String> showColumn = new TableColumn<>("");
        showColumn.setMaxWidth(100);
        showColumn.setMinWidth(100);
        showColumn.setCellFactory(param -> new TableCell<SiteDTO, String>() {
            private final Button viewButton = new Button("Details");
            {
                viewButton.setOnAction(event -> {
                    SiteDTO site = getTableRow().getItem();
                    if (site != null) {
                        openSiteDetails(site.id());
                    }
                });
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

        // Create pagination control and page selector
        HBox paginationControls = new HBox(20);
        pagination = createPagination();
        
        paginationControls.getChildren().addAll(pagination);
        paginationControls.setAlignment(Pos.CENTER);

        VBox tableWithPagination = new VBox(10, table, paginationControls);
        VBox.setVgrow(table, Priority.ALWAYS);

        return new VBox(10, filterBox, tableWithPagination);
    }

    private HBox createTableHeaders() {
        searchField = new TextField();
        searchField.setPromptText("Zoeken...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable());

        // Filtering for statussen
        statusFilter = new ComboBox<>();
        statusFilter.setPromptText("Statussen");
        statusFilter.setPrefWidth(150);
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

        // Filtering for naam
        nameFilter = new ComboBox<>();
        nameFilter.setPromptText("Site naam");
        nameFilter.setPrefWidth(150);
        nameFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

        // filtering for verantwoordelijke
        verantwoordelijkeFilter = new ComboBox<>();
        verantwoordelijkeFilter.setPromptText("Verantwoordelijke");
        verantwoordelijkeFilter.setPrefWidth(200);
        verantwoordelijkeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

        // filtering for min max machine
        minMachinesField = new TextField();
        minMachinesField.setPromptText("Min Machines");
        minMachinesField.setMaxWidth(100);
        minMachinesField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                minMachinesField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            filterTable();
        });

        maxMachinesField = new TextField();
        maxMachinesField.setPromptText("Max Machines");
        maxMachinesField.setMaxWidth(100);
        maxMachinesField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                maxMachinesField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            filterTable();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox pageSelector = createPageSelector();
        
        
        HBox filterBox = new HBox(10, searchField, statusFilter, nameFilter, verantwoordelijkeFilter, minMachinesField,
                maxMachinesField, spacer, pageSelector);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        return filterBox;
    }

    private HBox createPageSelector() {
        Label lblItemsPerPage = new Label("Aantal per pagina:");

        ComboBox<Integer> comboItemsPerPage = new ComboBox<>(FXCollections.observableArrayList(10, 20, 50, 100));
        comboItemsPerPage.setValue(itemsPerPage);
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
        this.currentPage = 0;
        updatePagination();
        updateTableItems();
    }

    private void updateFilterOptions() {
        List<String> statussen = new ArrayList<>();
        statussen.add(null);
        statussen.addAll(sc.getAllStatusses());
        statusFilter.setItems(FXCollections.observableArrayList(statussen));

        List<String> siteNames = new ArrayList<>();
        siteNames.add(null);
        siteNames.addAll(sc.getAllSiteNames());
        nameFilter.setItems(FXCollections.observableArrayList(siteNames));

        List<String> verantwoordelijken = new ArrayList<>();
        verantwoordelijken.add(null);
        verantwoordelijken.addAll(sc.getAllVerantwoordelijken());
        verantwoordelijkeFilter.setItems(FXCollections.observableArrayList(verantwoordelijken));
    }

    private void filterTable() {
        String searchQuery = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();
        String selectedName = nameFilter.getValue();
        String selectedVerantwoordelijke = verantwoordelijkeFilter.getValue();

        int minMachines = parseIntSafely(minMachinesField.getText(), Integer.MIN_VALUE);
        int maxMachines = parseIntSafely(maxMachinesField.getText(), Integer.MAX_VALUE);
        filteredSites = sc.getFilteredSites(searchQuery, selectedStatus, selectedName, 
                selectedVerantwoordelijke, minMachines, maxMachines);
        
        currentPage = 0;
        updatePagination();
        updateTableItems();
    }

    private int parseIntSafely(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
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
        totalPages = (int) Math.ceil((double) filteredSites.size() / itemsPerPage);
    }

    private void updateTableItems() {
        int fromIndex = currentPage * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredSites.size());

        if (filteredSites.isEmpty()) {
            table.getItems().clear();
        } else {
            List<SiteDTO> currentPageItems = fromIndex < toIndex ? filteredSites.subList(fromIndex, toIndex)
                    : List.of();
            table.getItems().setAll(currentPageItems);
        }
    }

    private void updateTable(List<SiteDTO> sites) {
        filteredSites = sites;
        currentPage = 0;
        updatePagination();
        updateTableItems();
    }

    private void openAddSiteForm() {
        Parent addSiteForm = new AddOrEditSiteForm(mainLayout, siteRepo, null);
        mainLayout.setContent(addSiteForm, true, false);
    }

    private void openEditSiteForm(Site site) {
        Parent editSiteForm = new AddOrEditSiteForm(mainLayout, siteRepo, site);
        mainLayout.setContent(editSiteForm, true, false);
    }

    private void openSiteDetails(int siteId) {
        Parent siteDetails = new SiteDetailsComponent(mainLayout, siteId);
        mainLayout.setContent(siteDetails, true, false);
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            allSites = sc.getSites();
            filteredSites = new ArrayList<>(allSites);
            updateFilterOptions();
            updateTable(filteredSites);
        });
    }
}