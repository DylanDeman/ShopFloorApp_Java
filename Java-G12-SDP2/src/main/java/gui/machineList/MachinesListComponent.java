package gui.machineList;

import java.util.List;

import domain.machine.MachineController;
import domain.machine.MachineDTO;
import gui.ChoicePane;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MachinesListComponent extends VBox {

    private TableView<MachineDTO> machineTable;
    private MachineController machineController;
    private Stage stage;

    public MachinesListComponent(Stage stage, MachineController machineController) {
        this.stage = stage;
        this.machineTable = new TableView<>();
        this.machineController = machineController;
        initializeGUI();
    }

    private void initializeGUI() {
        HBox titleSection = createTitleSection();
        this.getChildren().add(titleSection);

        machineTable = new TableView<>();

        TableColumn<MachineDTO, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().code()));

        TableColumn<MachineDTO, String> locationCol = new TableColumn<>("Locatie");
        locationCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().location()));

        TableColumn<MachineDTO, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));

        TableColumn<MachineDTO, String> prodStatusCol = new TableColumn<>("Productie");
        prodStatusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().productieStatus()));

        TableColumn<MachineDTO, String> maintenanceCol = new TableColumn<>("Onderhoud gepland");
        maintenanceCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().futureMaintenance().toString()));

        machineTable.getColumns().addAll(codeCol, locationCol, statusCol, prodStatusCol, maintenanceCol);
        machineTable.setPrefHeight(300);

        List<MachineDTO> dtos = machineController.getMachineList();
        machineTable.getItems().setAll(dtos);

        Button backButton = new Button("Back to ChoicePane");
        backButton.setOnAction(event -> goBackToChoicePane());

        backButton.setPrefWidth(200); 
        backButton.setStyle("-fx-font-size: 14px;"); // Optional: Increase font size for better visibility

        HBox buttonSection = new HBox(backButton);
        buttonSection.setAlignment(Pos.CENTER);
        buttonSection.setSpacing(10);

        // Ensure VBox has enough space
        this.setPrefHeight(600); // Set a preferred height for the VBox
        this.setPadding(new Insets(20, 20, 20, 20)); // Add padding (top, right, bottom, left)

        // Add both the table and the button section
        this.getChildren().addAll(machineTable, buttonSection);
        


		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		setBackground(new Background(backgroundImage));
    }

    private void goBackToChoicePane() {
        // Assuming you have a ChoicePane class, you can switch to that scene
        ChoicePane choicePane = new ChoicePane(stage); // Make sure ChoicePane takes Stage as constructor parameter
        Scene choiceScene = new Scene(choicePane, 800, 600); // Adjust the size as needed
        stage.setScene(choiceScene);
    }

    private HBox createTitleSection() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);

        Label title = new Label("Machines");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        hbox.getChildren().add(title);
        return hbox;
    }
}
