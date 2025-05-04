package gui.sitesList;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import repository.SiteRepository;

public class SitesListComponent extends VBox implements Observer
{
	private final MainLayout mainLayout;
	private SiteController sc;
	private SiteRepository siteRepo;
	private TableView<SiteDTO> table;
	private TextField searchField;
	private List<SiteDTO> allSites;
	private List<SiteDTO> filteredSites;

	// Pagination variables
	private int itemsPerPage = 10;
	private int currentPage = 0;
	private int totalPages = 0;
	private Pagination pagination;

	public SitesListComponent(MainLayout mainLayout)
	{
		this.mainLayout = mainLayout;
		this.sc = mainLayout.getServices().getSiteController();
		this.siteRepo = mainLayout.getServices().getSiteRepo();
		this.siteRepo.addObserver(this);
		this.table = new TableView<>();
		initializeGUI();
		loadSites();
	}

	private void loadSites()
	{
		List<Site> sites = siteRepo.getAllSites();

		allSites = makeSiteDTOs(sites);
		filteredSites = allSites;

		updateTable(allSites);
	}

	private List<SiteDTO> makeSiteDTOs(List<Site> sites)
	{
		return siteRepo.makeSiteDTOs(sites);
	}

	private void initializeGUI()
	{

		allSites = sc.getSites();
		filteredSites = allSites;

		VBox titleSection = createTitleSection();
		VBox tableSection = createTableSection();

		this.setSpacing(20);

		this.getChildren().addAll(titleSection, tableSection);
		updateTable(filteredSites);
	}

	private VBox createTitleSection()
	{
		HBox windowHeader = createWindowHeader();
		HBox informationBox = createInformationBox();
		return new VBox(10, windowHeader, informationBox);
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

	private VBox createTableSection()
	{
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

		TableColumn<SiteDTO, Void> editColumn = new TableColumn<>("Bewerken");
		editColumn.setCellFactory(param -> new TableCell<SiteDTO, Void>()
		{
			private final Button editButton = new Button();

			{
				ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
				editIcon.setFitWidth(16);
				editIcon.setFitHeight(16);
				editButton.setGraphic(editIcon);
				editButton.setBackground(Background.EMPTY);
				editButton.setOnAction(event -> openEditSiteForm(siteRepo.makeSiteObject(getTableRow().getItem())));
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				setGraphic(empty ? null : editButton);
			}
		});

		TableColumn<SiteDTO, Void> deleteColumn = new TableColumn<>("Verwijderen");
		deleteColumn.setCellFactory(param -> new TableCell<SiteDTO, Void>()
		{
			private final Button deleteButton = new Button();

			{
				ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
				deleteIcon.setFitHeight(16);
				deleteIcon.setFitWidth(16);
				deleteButton.setGraphic(deleteIcon);
				deleteButton.setBackground(Background.EMPTY);
				deleteButton.setOnAction(event -> deleteSite(getTableRow().getItem()));
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}

		});

		table.getColumns().setAll(col1, col2, col3, col4, col5);
		table.getColumns().add(editColumn);
		table.getColumns().add(deleteColumn);

		table.setPrefHeight(300);

		// Create pagination control
		pagination = createPagination();
		VBox tableWithPagination = new VBox(10, table, pagination);
		VBox.setVgrow(table, Priority.ALWAYS);

		return new VBox(10, filterBox, tableWithPagination);
	}

	private void deleteSite(SiteDTO siteDTO)
	{
		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.setTitle("Bevestig verwijderen");
		confirmation.setHeaderText("Site verwijderen");
		confirmation.setContentText("Weet u zeker dat u site '" + siteDTO.siteName() + "' wilt verwijderen?");

		confirmation.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK)
			{
				try
				{
					Site site = siteRepo.makeSiteObject(siteDTO);
					siteRepo.deleteSite(site);

					allSites.removeIf(s -> s.id() == siteDTO.id());
					filteredSites.removeIf(s -> s.id() == siteDTO.id());

					updateTable(filteredSites);

				} catch (Exception e)
				{
					System.err.println("Fout bij verwijderen: " + e.getMessage());
					e.printStackTrace();

					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Fout bij verwijderen");
						alert.setHeaderText("Kan site niet verwijderen");
						alert.setContentText(e.toString());
						alert.showAndWait();
					});
				}
			}
		});
	}

	private HBox createTableHeaders()
	{
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

	private HBox createPageSelector()
	{
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

	private void updateItemsPerPage(int itemsPerPage)
	{
		this.itemsPerPage = itemsPerPage;
		this.currentPage = 0; // Reset to first page when changing items per page
		updatePagination();
		updateTableItems();
	}

	private void filterTable(String searchQuery)
	{
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
		totalPages = (int) Math.ceil((double) filteredSites.size() / itemsPerPage);
	}

	private void updateTableItems()
	{
		int fromIndex = currentPage * itemsPerPage;
		int toIndex = Math.min(fromIndex + itemsPerPage, filteredSites.size());

		if (filteredSites.isEmpty())
		{
			table.getItems().clear();
		} else
		{
			List<SiteDTO> currentPageItems = fromIndex < toIndex ? filteredSites.subList(fromIndex, toIndex)
					: List.of();
			table.getItems().setAll(currentPageItems);
		}
	}

	private void updateTable(List<SiteDTO> sites)
	{
		filteredSites = sites;
		currentPage = 0;
		updatePagination();
		updateTableItems();
	}

	private HBox createInformationBox()
	{
		return new CustomInformationBox(
				"Hieronder vindt u een overzicht van alle sites. Klik op een site om de details van de site te bekijken!");
	}

	private void openAddSiteForm()
	{
		Parent addSiteForm = new AddOrEditSiteForm(mainLayout, siteRepo, null);
		mainLayout.setContent(addSiteForm, true, false);
	}

	private void openEditSiteForm(Site site)
	{
		Parent editSiteForm = new AddOrEditSiteForm(mainLayout, siteRepo, site);
		mainLayout.setContent(editSiteForm, true, false);
	}

	@Override
	public void update()
	{
		Platform.runLater(() -> {
			List<Site> sites = siteRepo.getAllSites();
			allSites = makeSiteDTOs(sites);
			filteredSites = new ArrayList<>(allSites);
			updateTable(filteredSites);
		});
	}

}