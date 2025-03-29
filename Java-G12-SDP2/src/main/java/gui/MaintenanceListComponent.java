package gui;
import java.util.Collections;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MaintenanceListComponent extends VBox {

    public MaintenanceListComponent(Stage stage) {
    		
        Button backButton = new Button("← Lijst alle onderhouden");
        
        backButton.setOnAction(e -> handleGoBack(stage));
        
        Label subheading = new Label("Hieronder vindt u een overzicht van de recente onderhouden");

        VBox titleSection = new VBox(5, backButton, subheading);
        titleSection.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Zoeken...");
        searchField.setMaxWidth(300);

        TableView<String> table = new TableView<>();
        TableColumn<String, String> col1 = new TableColumn<>("Nr.");
        TableColumn<String, String> col2 = new TableColumn<>("Starttijdstip");
        TableColumn<String, String> col3 = new TableColumn<>("Eindtijdstip");
        TableColumn<String, String> col4 = new TableColumn<>("Naam technieker");
        TableColumn<String, String> col5 = new TableColumn<>("Reden");
        TableColumn<String, String> col6 = new TableColumn<>("Opmerkingen");
        TableColumn<String, String> col7 = new TableColumn<>("Machine");
        TableColumn<String, String> col8 = new TableColumn<>("Status");

        Collections.addAll(table.getColumns(), col1, col2, col3, col4, col5, col6, col7, col8);
        table.setPrefHeight(300);

        // Pagination Controls
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER);
        Button prevPage = new Button("Vorige Pagina");
        Button nextPage = new Button("Volgende Pagina");
        pagination.getChildren().addAll(prevPage, new Button("1"), new Button("2"), new Button("3"), new Button("7"), nextPage);

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(titleSection, searchField, table, pagination);
    }
    
    private void handleGoBack(Stage stage) {
    	ChoicePane choicePane = new ChoicePane(stage);
    	
    	Scene choicePaneScene = new Scene(choicePane);
    	
    	stage.setScene(choicePaneScene);
    }
}
