package gui.machine;

import domain.machine.MachineController;
import domain.machine.MachineDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import domain.site.SiteController;
import domain.site.SiteDTO;
import domain.user.User;
import domain.user.UserController;

public class AddOrEditMachineForm extends VBox {

    private Stage stage;
    private MachineController machineController;
    private MachineDTO machineDTO; // If null, it's add mode
    private SiteController siteController;
    private UserController userController;

    public AddOrEditMachineForm(Stage stage, MachineController machineController, 
    		MachineDTO machineDTO,
    		SiteController siteController,
    		UserController userController
    		) {
        this.stage = stage;
        this.machineController = machineController;
        this.machineDTO = machineDTO;
        this.siteController = siteController;
        this.userController = userController;

        initializeGUI();
    }

    private void initializeGUI() {
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_CENTER);

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResourceAsStream("/images/background.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true));
        this.setBackground(new Background(backgroundImage));

        Label title = new Label(machineDTO == null ? "Nieuwe machine toevoegen" : "Machine bewerken");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField codeField = new TextField();
        codeField.setPromptText("Code");

        TextField locationField = new TextField();
        locationField.setPromptText("Locatie");
        
        ChoiceBox<SiteDTO> siteCb = new ChoiceBox<>();
        siteCb.setItems(FXCollections.observableArrayList(siteController.getSites()));

        ChoiceBox<User> techniekerCb = new ChoiceBox<>();
        techniekerCb.setItems(FXCollections.observableArrayList(userController.getAllTechniekers()));

        TextField productInfoField = new TextField();
        productInfoField.setPromptText("Product info");

        DatePicker futureMaintenancePicker = new DatePicker();
        futureMaintenancePicker.setPromptText("Volgende onderhoudsdatum");



        // Prefill fields if editing
        if (machineDTO != null) {
            codeField.setText(machineDTO.code());
            locationField.setText(machineDTO.location());
        }

        Button saveButton = new Button("Opslaan");
        saveButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
        saveButton.setOnAction(e -> {
            try {
                String code = codeField.getText();
                String location = locationField.getText();
                SiteDTO selectedSite = siteCb.getValue();
                User selectedTechnician = techniekerCb.getValue();
                String productInfo = productInfoField.getText();
                LocalDate futureMaintenanceDate = futureMaintenancePicker.getValue();

                if (code.isBlank() || location.isBlank() || selectedSite == null || selectedTechnician == null) {
                    // Show an alert maybe?
                    System.out.println("All fields must be filled.");
                    return;
                }

                // Build MachineDTO
                MachineDTO newMachine = new MachineDTO(
                    0, // Assuming ID is auto-generated
                    selectedSite,
                    selectedTechnician,
                    code,
                    "Actief",           // Default status?
                    "Productie OK",     // Default productieStatus?
                    location,
                    productInfo,
                    LocalDateTime.now(),                      // lastMaintenance = now
                    futureMaintenanceDate.atStartOfDay(),     // convert to LocalDateTime
                    0,                                         // number of days since last maintenance
                    0.0                                        // uptime in hours
                );

                machineController.addNewMachine(newMachine);
                goBack();
            } catch (Exception ex) {
                ex.printStackTrace(); // For debugging
            }
        });


        Button cancelButton = new Button("Annuleer");
        cancelButton.setOnAction(e -> goBack());

        HBox buttonBox = new HBox(10, cancelButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(title, codeField, locationField, buttonBox, siteCb, 
        		techniekerCb, productInfoField, futureMaintenancePicker);
    }

    private void goBack() {
        MachinesListComponent machineList = new MachinesListComponent(stage, machineController, siteController, userController);
        Scene machineScene = new Scene(machineList, 800, 600);
        stage.setScene(machineScene);
    }
}
