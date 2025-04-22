package gui.sitesList;

import java.util.List;
import java.util.stream.Collectors;
import org.kordamp.ikonli.javafx.FontIcon;
import domain.site.SiteController;
import domain.site.SiteDTO;
import gui.customComponents.CustomButton;
import gui.customComponents.CustomInformationBox;
import interfaces.Observer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SitesListComponent extends VBox implements Observer {
	private SiteController sc;
	private Stage stage;
	private TableView<SiteDTO> table;
	private TextField searchField;
	private List<SiteDTO> allSites;
	private List<SiteDTO> filteredSites;
	
	// Pagination variables
	private int itemsPerPage = 10;
	private int currentPage = 0;
	private int totalPages = 0;
	private Pagination pagination;

	public SitesListComponent(Stage stage, SiteController sc) {
		this.sc = sc;
		this.stage = stage;
		this.table = new TableView<>();
		initializeGUI();
	}

	private void initializeGUI() {
		stage.setMinWidth(800);
		allSites = sc.getSites();
		filteredSites = allSites;
		updatePadding(stage);
		stage.widthProperty().addListener((obs, oldWidth, newWidth) -> updatePadding(stage));

		VBox titleSection = createTitleSection();
		VBox tableSection = createTableSection();

		this.setSpacing(20);
		this.getChildren().addAll(titleSection, tableSection);
		updateTable(filteredSites);
	}

	private void updatePadding(Stage stage) {
		double amountOfPixels = stage.getWidth();
		double calculatedPadding = amountOfPixels < 1200 ? amountOfPixels * 0.05 : amountOfPixels * 0.10;
		this.setPadding(new Insets(50, calculatedPadding, 0, calculatedPadding));
	}

	private VBox createTitleSection() {
		HBox windowHeader = createWindowHeader();
		HBox informationBox = createInformationBox();
		return new VBox(10, windowHeader, informationBox);
	}

	private HBox createWindowHeader() {
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_LEFT);

		Label title = new Label("Sites");
		title.setStyle("-fx-font: 40 arial;");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		Button button = new CustomButton(new FontIcon("fas-plus"), "Site toevoegen");
		button.setOnAction(e -> System.out.println("Open toevoegen scherm"));

		hbox.getChildren().addAll(title, spacer, button);
		return hbox;
	}

	private VBox createTableSection() {
		HBox filterBox = createTableHeaders();

		TableColumn<SiteDTO, Number> col1 = new TableColumn<>("Nr.");
		col1.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()));

		TableColumn<SiteDTO, String> col2 = new TableColumn<>("Naam");
		col2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().siteName()));

		TableColumn<SiteDTO, String> col3 = new TableColumn<>("Verantwoordelijke");
		col3.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().verantwoordelijke().getFullName()));

		TableColumn<SiteDTO, String> col4 = new TableColumn<>("Status");
		col4.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status().toString()));

		TableColumn<SiteDTO, Number> col5 = new TableColumn<>("Aantal machines");
		col5.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().machines().size()));

		table.getColumns().setAll(col1, col2, col3, col4, col5);
		table.setPrefHeight(300);

		// Create pagination control
		pagination = createPagination();
		VBox tableWithPagination = new VBox(10, table, pagination);
		VBox.setVgrow(table, Priority.ALWAYS);

		return new VBox(10, filterBox, tableWithPagination);
	}

	private HBox createTableHeaders() {
		searchField = new TextField();
		searchField.setPromptText("Zoeken...");
		searchField.setMaxWidth(300);
		searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable(newVal));
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		// Page selector component
		HBox pageSelector = createPageSelector();
		
		HBox filterBox = new HBox(10, searchField, spacer, pageSelector);
		filterBox.setAlignment(Pos.CENTER_LEFT);
		return filterBox;
	}

	private HBox createPageSelector() {
		Label lblItemsPerPage = new Label("Aantal per pagina:");
		
		ComboBox<Integer> comboItemsPerPage = new ComboBox<>(
			FXCollections.observableArrayList(10, 20, 50, 100)
		);
		
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

	private void filterTable(String searchQuery) {
		String lowerCaseFilter = searchQuery.toLowerCase();
		filteredSites = allSites.stream()
				.filter(site -> site.siteName().toLowerCase().contains(lowerCaseFilter)
						|| site.verantwoordelijke().getFullName().toLowerCase().contains(lowerCaseFilter)
						|| site.status().toString().toLowerCase().contains(lowerCaseFilter))
				.collect(Collectors.toList());
		
		currentPage = 0; // Reset to first page when filter changes
		updatePagination();
		updateTableItems();
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
			List<SiteDTO> currentPageItems = fromIndex < toIndex 
					? filteredSites.subList(fromIndex, toIndex) 
					: List.of();
			table.getItems().setAll(currentPageItems);
		}
	}

	private void updateTable(List<SiteDTO> sites) {
		filteredSites = sites;
		updatePagination();
		updateTableItems();
	}

	private HBox createInformationBox() {
		return new CustomInformationBox(
				"Hieronder vindt u een overzicht van alle sites. Klik op een site om de details van de site te bekijken!");
	}

	@Override
	public void update() {
		allSites = sc.getSites();
		filteredSites = allSites;
		updatePagination();
		updateTableItems();
	}
}