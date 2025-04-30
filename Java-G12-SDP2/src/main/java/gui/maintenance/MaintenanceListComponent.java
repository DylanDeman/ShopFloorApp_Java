package gui.maintenance;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.machine.MachineDTO;
import domain.maintenance.MaintenanceController;
import domain.maintenance.MaintenanceDTO;
import gui.ChoicePane;
import gui.customComponents.CustomButton;
import gui.customComponents.CustomInformationBox;
import gui.report.AddReportForm;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MaintenanceListComponent extends VBox
{

	private MaintenanceController mc;
	private Stage stage;
	private TableView<MaintenanceDTO> table;
	private TextField searchField;
	private List<MaintenanceDTO> allMaintenances;
	private List<MaintenanceDTO> filteredMaintenances;
	private MachineDTO machineDTO;

	private int itemsPerPage = 10;
	private int currentPage = 0;
	private int totalPages = 0;
	private Pagination pagination;

	public MaintenanceListComponent(Stage stage, MaintenanceController mc)
	{
		this.mc = mc;
		this.stage = stage;
		this.table = new TableView<>();
		initializeGUI();
	}

	public MaintenanceListComponent(Stage stage, MaintenanceController mc, MachineDTO machineDTO)
	{
		this.mc = mc;
		this.machineDTO = machineDTO;
		this.stage = stage;
		this.table = new TableView<>();
		initializeGUI();
	}

	private void initializeGUI()
	{
		stage.setMinWidth(800);
		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		setBackground(new Background(backgroundImage));

		allMaintenances = machineDTO == null ? mc.getMaintenances()
				: mc.getMaintenances().stream().filter((m) -> m.machine().equals(machineDTO)).toList();
		filteredMaintenances = allMaintenances;
		updatePadding(stage);
		stage.widthProperty().addListener((obs, oldWidth, newWidth) -> updatePadding(stage));

		VBox titleSection = createTitleSection();
		VBox tableSection = createTableSection();

		this.setSpacing(20);
		this.getChildren().addAll(titleSection, tableSection);
		updateTable(filteredMaintenances);
	}

	private void updatePadding(Stage stage)
	{
		double amountOfPixels = stage.getWidth();
		double calculatedPadding = amountOfPixels < 1200 ? amountOfPixels * 0.05 : amountOfPixels * 0.10;
		this.setPadding(new Insets(50, calculatedPadding, 0, calculatedPadding));
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
		backButton.setOnAction(e -> handleGoBack(stage));

		Label title = new Label("Onderhoudslijst");
		title.setStyle("-fx-font: 40 arial;");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().addAll(backButton, title, spacer);
		return hbox;
	}

	private VBox createTableSection()
	{
		HBox filterBox = createTableHeaders();

		TableColumn<MaintenanceDTO, String> col1 = createColumn("Datum uitgevoerd", m -> m.executionDate().toString());
		TableColumn<MaintenanceDTO, String> col2 = createColumn("Starttijdstip",
				m -> m.startDate().toLocalTime().toString());
		TableColumn<MaintenanceDTO, String> col3 = createColumn("Eindtijdstip",
				m -> m.endDate().toLocalTime().toString());
		TableColumn<MaintenanceDTO, String> col4 = createColumn("Naam technieker",
				m -> m.technician() != null ? m.technician().getFullName() : "Onbekend");
		TableColumn<MaintenanceDTO, String> col5 = createColumn("Reden", MaintenanceDTO::reason);
		TableColumn<MaintenanceDTO, String> col6 = createColumn("Opmerkingen", MaintenanceDTO::comments);
		TableColumn<MaintenanceDTO, String> col7 = createColumn("Status", m -> m.status().toString());
		TableColumn<MaintenanceDTO, Void> col8 = createAddRapportButtonColumn(stage);
		TableColumn<MaintenanceDTO, String> col9 = createColumn("Machine",
				m -> String.format("Machine %d", m.machine().id()));

		List<TableColumn<MaintenanceDTO, ?>> columns;
		if (machineDTO != null)
		{
			columns = List.of(col1, col2, col3, col4, col5, col6, col7, col8);
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

	private TableColumn<MaintenanceDTO, Void> createAddRapportButtonColumn(Stage stage)
	{
		TableColumn<MaintenanceDTO, Void> col = new TableColumn<>("Rapport toevoegen");

		col.setCellFactory(param -> new TableCell<>()
		{
			private final CustomButton btn = new CustomButton("Toevoegen");
			{
				btn.setOnAction(e ->
				{
					MaintenanceDTO selectedMaintenance = getTableView().getItems().get(getIndex());
					goToAddRapport(stage, selectedMaintenance);
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

	private void handleGoBack(Stage stage)
	{
		stage.setScene(new Scene(new ChoicePane(stage)));
	}

	private void goToAddRapport(Stage stage, MaintenanceDTO maintenance)
	{
		AddReportForm form = new AddReportForm(stage, maintenance);
		Scene scene = new Scene(form);
		form.getStylesheets().add(getClass().getResource("/css/AddRapport.css").toExternalForm());
		stage.setScene(scene);
	}

}
