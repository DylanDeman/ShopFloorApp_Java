package gui.sitesList;

import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.site.SiteController;
import domain.site.SiteDTO;
import gui.customComponents.CustomButton;
import gui.customComponents.CustomInformationBox;
import interfaces.Observer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    public SitesListComponent(Stage stage, SiteController sc) {
        this.sc = sc;
        this.stage = stage;
        this.table = new TableView<>();
        initializeGUI();
    }

    private void initializeGUI() {
        stage.setMinWidth(800);
        allSites = sc.getSites();
        updatePadding(stage);
        stage.widthProperty().addListener((obs, oldWidth, newWidth) -> updatePadding(stage));

        VBox titleSection = createTitleSection();
        VBox tableSection = createTableSection();

        this.setSpacing(20);
        this.getChildren().addAll(titleSection, tableSection);
        updateTable(allSites);
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
        button.setOnAction(e -> System.out.println("Open 'toevoegen' scherm"));

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

        HBox pagination = createPagination();
        return new VBox(10, filterBox, table, pagination);
    }

    private HBox createTableHeaders() {
        searchField = new TextField();
        searchField.setPromptText("Zoeken...");
        searchField.setMaxWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable(newVal));

        HBox filterBox = new HBox(searchField);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        return filterBox;
    }

    private void filterTable(String searchQuery) {
        String lowerCaseFilter = searchQuery.toLowerCase();
        List<SiteDTO> filtered = allSites.stream()
                .filter(site -> site.siteName().toLowerCase().contains(lowerCaseFilter)
                        || site.verantwoordelijke().getFullName().contains(lowerCaseFilter)
                        || site.status().toString().toLowerCase().contains(lowerCaseFilter))
                .toList();
        updateTable(filtered);
    }

    private HBox createPagination() {
    	HBox pagination = new HBox(10);
		pagination.setAlignment(Pos.CENTER);
		Button prevPage = new Button("Vorige Pagina");
		Button nextPage = new Button("Volgende Pagina");
		pagination.getChildren().addAll(prevPage, new Button("1"), new Button("2"), new Button("3"), new Button("7"),
				nextPage);

		this.setSpacing(10);
		return pagination;
    }

    private void updateTable(List<SiteDTO> sites) {
        table.getItems().setAll(sites);
    }

    private HBox createInformationBox() {
        return new CustomInformationBox("Hieronder vindt u een overzicht van alle sites. Klik op een site om de details van de site te bekijken!");
    }

    @Override
    public void update() {
        initializeGUI();
    }
}
