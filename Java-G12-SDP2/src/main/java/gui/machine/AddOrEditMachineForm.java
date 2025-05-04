package gui.machine;

import domain.machine.MachineController;
import domain.machine.MachineDTO;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import domain.site.SiteController;
import domain.site.SiteDTO;
import domain.user.User;
import domain.user.UserController;

public class AddOrEditMachineForm extends GridPane {

    private Stage stage;
    private MachineController machineController;
    private MachineDTO machineDTO; // If null, it's add mode
    private SiteController siteController;
    private UserController userController;
    
    private TextField codeField, locationField, productInfoField;
    private DatePicker futureMaintenancePicker;
    private ComboBox<SiteDTO> siteCb;
    private ComboBox<User> techniekerCb;
    private Label errorLabel;
    private Label codeError, locationError, siteError, technicianError, productInfoError, maintenanceError;

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
        this.setPadding(new Insets(20));
        this.setHgap(20);
        this.setVgap(20);

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResourceAsStream("/images/background.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true));
        setBackground(new Background(backgroundImage));

        Button backButton = new Button("← Terug");
        backButton.setOnAction(e -> goBack());
        this.add(backButton, 0, 0, 2, 1);

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);
        this.add(errorLabel, 0, 1, 2, 1);

        Label headerLabel = new Label(machineDTO == null ? "MACHINE TOEVOEGEN" : "MACHINE AANPASSEN");
        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");
        HBox headerBox = new HBox(headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setStyle("-fx-background-color: rgb(240, 69, 60); " +
                "-fx-padding: 15px; " +
                "-fx-border-radius: 5 5 5 5; " +
                "-fx-background-radius: 5 5 5 5;");
        headerBox.setMaxWidth(Double.MAX_VALUE);
        headerBox.setMaxHeight(40);
        this.add(headerBox, 0, 2, 2, 1);
        
        GridPane.setMargin(headerBox, new Insets(0, 0, 0, 0));

        HBox mainContent = new HBox(30);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");
        mainContent.setMaxWidth(Double.MAX_VALUE);

        VBox leftFieldsBox = new VBox(15);
        leftFieldsBox.setPadding(new Insets(20));
        leftFieldsBox.getChildren().add(createMachineDetailsSection());

        Line divider = new Line(0, 0, 0, 400);
        divider.setStroke(Color.LIGHTGRAY);
        divider.setStrokeWidth(1);

        VBox rightFieldsBox = new VBox(15);
        rightFieldsBox.setPadding(new Insets(20));
        rightFieldsBox.getChildren().add(createLocationAndMaintenanceSection());

        mainContent.getChildren().addAll(leftFieldsBox, divider, rightFieldsBox);
        this.add(mainContent, 0, 3, 2, 1);

        Button saveButton = new Button("Opslaan");
        saveButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setPadding(new Insets(10, 30, 10, 30));
        saveButton.setOnAction(e -> saveMachine());

        HBox buttonBox = new HBox(saveButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        HBox.setHgrow(saveButton, Priority.ALWAYS);
        buttonBox.setMaxWidth(400);

        this.add(buttonBox, 0, 4, 2, 1);
        GridPane.setHalignment(buttonBox, HPos.CENTER);

        // Prefill fields if editing
        if (machineDTO != null) {
            fillMachineData(machineDTO);
        }
    }

    private GridPane createMachineDetailsSection() {
        GridPane pane = new GridPane();
        pane.setVgap(5);
        pane.setHgap(10);

        Label sectionLabel = new Label("Machine Details");
        sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        pane.add(sectionLabel, 0, 0, 2, 1);

        codeError = createErrorLabel();
        technicianError = createErrorLabel();
        siteError = createErrorLabel();
        productInfoError = createErrorLabel();

        codeField = new TextField();
        techniekerCb = new ComboBox<>();
        siteCb = new ComboBox<>();
        productInfoField = new TextField();
        
        // Setup ComboBoxes
        techniekerCb.setItems(FXCollections.observableArrayList(userController.getAllTechniekers()));
        techniekerCb.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user == null ? "" : user.getFullName();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });
        
        List<SiteDTO> sites = siteController.getSites();
        siteCb.setItems(FXCollections.observableArrayList(sites));
        siteCb.setConverter(new javafx.util.StringConverter<SiteDTO>() {
            @Override
            public String toString(SiteDTO site) {
                return site == null ? "" : site.siteName();
            }

            @Override
            public SiteDTO fromString(String string) {
                return null;
            }
        });

        codeField.setPrefWidth(200);
        techniekerCb.setPrefWidth(200);
        siteCb.setPrefWidth(200);
        productInfoField.setPrefWidth(200);
        
        int row = 1;
        pane.add(new Label("Code:"), 0, row);
        pane.add(codeField, 1, row++);
        pane.add(codeError, 1, row++);

        pane.add(new Label("Site:"), 0, row);
        pane.add(siteCb, 1, row++);
        pane.add(siteError, 1, row++);

        pane.add(new Label("Technieker:"), 0, row);
        pane.add(techniekerCb, 1, row++);
        pane.add(technicianError, 1, row++);

        pane.add(new Label("Product Info:"), 0, row);
        pane.add(productInfoField, 1, row++);
        pane.add(productInfoError, 1, row++);

        return pane;
    }

    private GridPane createLocationAndMaintenanceSection() {
        GridPane pane = new GridPane();
        pane.setVgap(5);
        pane.setHgap(10);

        Label sectionLabel = new Label("Locatie & Onderhoud");
        sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        pane.add(sectionLabel, 0, 0, 2, 1);

        locationError = createErrorLabel();
        maintenanceError = createErrorLabel();

        locationField = new TextField();
        futureMaintenancePicker = new DatePicker();
        futureMaintenancePicker.setEditable(false);

        locationField.setPrefWidth(200);
        futureMaintenancePicker.setPrefWidth(200);

        int row = 1;
        pane.add(new Label("Locatie:"), 0, row);
        pane.add(locationField, 1, row++);
        pane.add(locationError, 1, row++);

        pane.add(new Label("Volgende onderhoudsdatum:"), 0, row);
        pane.add(futureMaintenancePicker, 1, row++);
        pane.add(maintenanceError, 1, row++);

        return pane;
    }

    private void fillMachineData(MachineDTO machine) {
        codeField.setText(machine.code());
        locationField.setText(machine.location());
        productInfoField.setText(machine.productInfo());
        
        if (machine.futureMaintenance() != null) {
            futureMaintenancePicker.setValue(machine.futureMaintenance().toLocalDate());
        }
        
        // Set selected site and technician if available
        if (machine.site() != null) {
            siteCb.getItems().stream()
                .filter(site -> site.id() == machine.site().id())
                .findFirst()
                .ifPresent(siteCb::setValue);
        }
        
        if (machine.technician() != null) {
            techniekerCb.getItems().stream()
                .filter(tech -> tech.getId() == machine.technician().getId())
                .findFirst()
                .ifPresent(techniekerCb::setValue);
        }
    }

    private void saveMachine() {
        try {
            resetErrorLabels();
            
            String code = codeField.getText();
            String location = locationField.getText();
            SiteDTO selectedSite = siteCb.getValue();
            User selectedTechnician = techniekerCb.getValue();
            String productInfo = productInfoField.getText();
            LocalDate futureMaintenanceDate = futureMaintenancePicker.getValue();

            // Simple validation
            boolean hasErrors = false;
            
            if (code.isBlank()) {
                codeError.setText("Code is verplicht");
                hasErrors = true;
            }
            
            if (location.isBlank()) {
                locationError.setText("Locatie is verplicht");
                hasErrors = true;
            }
            
            if (selectedSite == null) {
                siteError.setText("Site is verplicht");
                hasErrors = true;
            }
            
            if (selectedTechnician == null) {
                technicianError.setText("Technieker is verplicht");
                hasErrors = true;
            }
            
            if (futureMaintenanceDate == null) {
                maintenanceError.setText("Onderhoudsdatum is verplicht");
                hasErrors = true;
            }
            
            if (hasErrors) {
                return;
            }

            MachineDTO updatedMachine;

            if (machineDTO != null) {
                // Edit existing machine
                updatedMachine = new MachineDTO(
                    machineDTO.id(),
                    selectedSite,
                    selectedTechnician,
                    code,
                    machineDTO.status(),
                    machineDTO.productieStatus(),
                    location,
                    productInfo,
                    machineDTO.lastMaintenance(),
                    futureMaintenanceDate.atStartOfDay(),
                    machineDTO.numberDaysSinceLastMaintenance(),
                    machineDTO.upTimeInHours()
                );

                machineController.updateMachine(machineController.convertDTOToMachine(updatedMachine));
            } else {
                // Add new machine
                updatedMachine = new MachineDTO(
                    0,
                    selectedSite,
                    selectedTechnician,
                    code,
                    "Actief",
                    "Productie OK",
                    location,
                    productInfo,
                    LocalDateTime.now(),
                    futureMaintenanceDate.atStartOfDay(),
                    0,
                    0.0
                );

                machineController.addNewMachine(updatedMachine);
            }

            goBack();
        } catch (Exception ex) {
            showError("Er is een fout opgetreden: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 10px;");
        errorLabel.setMaxWidth(150);
        errorLabel.setWrapText(true);
        return errorLabel;
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void resetErrorLabels() {
        errorLabel.setText("");
        codeError.setText("");
        locationError.setText("");
        siteError.setText("");
        technicianError.setText("");
        productInfoError.setText("");
        maintenanceError.setText("");
    }

    private void goBack() {
        MachinesListComponent machineList = new MachinesListComponent(stage, machineController, siteController, userController);
        Scene machineScene = new Scene(machineList, 800, 600);
        stage.setScene(machineScene);
    }
}