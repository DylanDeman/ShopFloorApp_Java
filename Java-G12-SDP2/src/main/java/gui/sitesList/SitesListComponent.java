package gui.sitesList;

import java.util.Collections;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import gui.ChoicePane;
import gui.customComponents.CustomButton;
import gui.customComponents.CustomInformationBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SitesListComponent extends VBox {

	public SitesListComponent(Stage stage) {
		stage.setMinWidth(800);
		
		// add padding to stage for responsiveness & readability:
		updatePadding(stage);
		stage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
	        updatePadding(stage);
	    });
		
		// to make the title and site Add button
		HBox windowHeader = createWindowHeader();
		
		// to make the information box 
		HBox informationBox = createInformationBox();
		
		// To make the search
		createTableHeaders();
		
		// To make filtering box
		createTableFilterBox();
		
		// to make Table with pagination
		createTable();
	
		VBox titleSection = new VBox(10, windowHeader, informationBox);

		TextField searchField = new TextField();
		searchField.setPromptText("Zoeken...");
		searchField.setMaxWidth(300);

		TableView<String> table = new TableView<>();
		TableColumn<String, String> col1 = new TableColumn<>("Nr.");
		TableColumn<String, String> col2 = new TableColumn<>("Naam");
		TableColumn<String, String> col3 = new TableColumn<>("Verantwoordelijke");
		TableColumn<String, String> col4 = new TableColumn<>("Status");
		TableColumn<String, String> col5 = new TableColumn<>("Aantal machines");

		Collections.addAll(table.getColumns(), col1, col2, col3, col4, col5);
		table.setPrefHeight(300);

		// Pagination Controls
		HBox pagination = new HBox(10);
		pagination.setAlignment(Pos.CENTER);
		Button prevPage = new Button("Vorige Pagina");
		Button nextPage = new Button("Volgende Pagina");
		pagination.getChildren().addAll(prevPage, new Button("1"), new Button("2"), new Button("3"), new Button("7"),
				nextPage);

		this.setSpacing(10);
		this.getChildren().addAll(titleSection, searchField, table, pagination);
	}

	private void updatePadding(Stage stage) {
		// For a bit of responsiveness:
		double amountOfPixels = stage.getWidth();
		double calculatedPadding = amountOfPixels < 1200 ? amountOfPixels * 0.05 : amountOfPixels * 0.10;
		this.setPadding(new Insets(50, calculatedPadding , 0, calculatedPadding));
	}

	private HBox createWindowHeader() {
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_LEFT);

		// Title
		Label title = new Label("Sites");
		title.setStyle("-fx-font: 40 arial;");

		// Spacer
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// Site toevoegen button
		Button button = new CustomButton(new FontIcon("fas-plus"), "Site toevoegen");

		hbox.getChildren().addAll(title, spacer, button);
		return hbox;
	}

	private void createTableFilterBox() {
		// TODO Auto-generated method stub
	}

	private void createTableHeaders() {
		// TODO Auto-generated method stub
	}

	private void createTable() {
		// TODO Auto-generated method stub
	}

	private HBox createInformationBox() {
		return new CustomInformationBox(
				"Hieronder vindt u een overzicht van alle sites. Klik op een site om de details van de site te bekijken!");
	}
}
