package gui.maintenance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.machine.Machine;
import domain.machine.MachineController;
import domain.machine.MachineDTO;
import domain.maintenance.MaintenanceBuilder;
import domain.maintenance.MaintenanceController;
import domain.user.User;
import domain.user.UserController;
import exceptions.InformationRequiredExceptionMaintenance;
import gui.MainLayout;
import gui.customComponents.CustomButton;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import util.MaintenanceStatus;

public class MaintenancePlanningForm extends VBox {

    private final MainLayout mainLayout;
    private final MachineDTO machineDTO;
    private final MachineController mc;
    private final MaintenanceController mntcc;
    private final UserController uc;

    private Label errorLabel;
    private Label startDateErrorLabel, endDateErrorLabel,
            machineErrorLabel, statusErrorLabel, reasonErrorLabel, technicianErrorLabel, executionDateErrorLabel;

    public MaintenancePlanningForm(MainLayout mainLayout, MachineDTO machineDTO) {
        this.mainLayout = mainLayout;
        this.machineDTO = machineDTO;
        this.mc = new MachineController();
        this.mntcc = new MaintenanceController();
        this.uc = new UserController();
        initializeForm();
    }

    private void populateTimePicker(ComboBox<LocalTime> timePicker) {
        LocalTime time = LocalTime.of(0, 0);
        while (time.isBefore(LocalTime.of(23, 45))) {
            timePicker.getItems().add(time);
            time = time.plusMinutes(15);
        }

        timePicker.setConverter(new javafx.util.StringConverter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            @Override
            public String toString(LocalTime time) {
                return time != null ? formatter.format(time) : "";
            }

            @Override
            public LocalTime fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalTime.parse(string, formatter) : null;
            }
        });
    }

    private void initializeForm() {
        this.getStylesheets().add(getClass().getResource("/css/form.css").toExternalForm());
        this.getStylesheets().add(getClass().getResource("/css/maintenanceForm.css").toExternalForm());

        this.setSpacing(20);
        this.getStyleClass().add("content-container");
        
		FontIcon icon = new FontIcon("fas-arrow-left");
		icon.setIconSize(20);
		Button backButton = new Button();
		backButton.setGraphic(icon);
		backButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
		backButton.setOnAction(e -> goToMaintenanceList(machineDTO));
		backButton.setCursor(Cursor.HAND);

        Label headerLabel = new Label("Onderhoud inplannen");
        headerLabel.getStyleClass().add("header-label");
		headerLabel.setStyle("-fx-font: 40 arial;");
        headerLabel.setAlignment(Pos.CENTER);
        
        HBox title = new HBox(10, backButton, headerLabel);
        title.setAlignment(Pos.CENTER_LEFT);
        
        CustomButton saveButton = new CustomButton("Opslaan");
        
        VBox footer = new VBox();
        footer.setAlignment(Pos.CENTER);
        footer.getChildren().add(saveButton);
        

        VBox leftColumn = new VBox(15);
        VBox rightColumn = new VBox(15);
        
        HBox formRowBox = new HBox(20, leftColumn, rightColumn);
        this.setAlignment(Pos.CENTER);
        formRowBox.setAlignment(Pos.TOP_CENTER);
        
        VBox form = new VBox(15, formRowBox, footer);
        form.getStyleClass().add("form-box");

        DatePicker executionDatePicker = new DatePicker();
        ComboBox<LocalTime> startTimeField = new ComboBox<>();
        populateTimePicker(startTimeField);
        ComboBox<LocalTime> endTimeField = new ComboBox<>();
        populateTimePicker(endTimeField);

        startTimeField.setPromptText("Starttijd");
        endTimeField.setPromptText("Eindtijd");

        startDateErrorLabel = createErrorLabel();
        endDateErrorLabel = createErrorLabel();
        machineErrorLabel = createErrorLabel();
        statusErrorLabel = createErrorLabel();
        technicianErrorLabel = createErrorLabel();
        reasonErrorLabel = createErrorLabel();
        executionDateErrorLabel = createErrorLabel();
        errorLabel = createErrorLabel();

        ComboBox<User> technicianComboBox = new ComboBox<>();
        technicianComboBox.getItems().addAll(uc.getAllTechniekers());
        technicianComboBox.setPromptText("Selecteer technieker");
        technicianComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("Technieker %s", item.getFullName()));
            }
        });
        technicianComboBox.setButtonCell(technicianComboBox.getCellFactory().call(null));

        TextField reasonField = new TextField();
        TextArea commentsField = new TextArea();

        ComboBox<String> statusComboBox = new ComboBox<>();
        ComboBox<MachineDTO> machineComboBox = new ComboBox<>();

        statusComboBox.getItems().addAll(Arrays.stream(MaintenanceStatus.values()).map(Enum::toString).toList());
        statusComboBox.setPromptText("Selecteer status");

        if (machineDTO != null) {
            machineComboBox.getItems().add(machineDTO);
            machineComboBox.setValue(machineDTO);
            machineComboBox.setDisable(true);
        } else {
            machineComboBox.setPromptText("Selecteer machine");
            List<MachineDTO> machines = mc.getMachineList();
            machineComboBox.getItems().addAll(machines);
        }
        machineComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(MachineDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Machine " + item.id() + ", code: " + item.code());
            }
        });
        machineComboBox.setButtonCell(machineComboBox.getCellFactory().call(null));

        leftColumn.getChildren().addAll(
                createFormField("Datum uitgevoerd:", executionDatePicker),
                executionDateErrorLabel,
                createFormField("Starttijdstip", startTimeField),
                startDateErrorLabel,
                createFormField("Eindtijdstip", endTimeField),
                endDateErrorLabel,
                createFormField("Naam technieker:", technicianComboBox),
                technicianErrorLabel,
                createFormField("Reden:", reasonField),
                reasonErrorLabel
        );

        rightColumn.getChildren().addAll(
                createFormField("Opmerkingen:", commentsField),
                createFormField("Status:", statusComboBox),
                statusErrorLabel,
                createFormField("Machine:", machineComboBox),
                machineErrorLabel
        );
        
        saveButton.getStyleClass().add("create-report-button");
        saveButton.setOnAction(e -> {
            try {
                resetErrorLabels();

                LocalDate execDate = executionDatePicker.getValue();
                LocalTime sTime = startTimeField.getValue();
                LocalTime eTime = endTimeField.getValue();

                LocalDateTime startDateTime = (execDate != null && sTime != null) ? LocalDateTime.of(execDate, sTime) : null;
                LocalDateTime endDateTime = (execDate != null && eTime != null) ? LocalDateTime.of(execDate, eTime) : null;

                Machine machine = (machineComboBox.getValue() != null)
                        ? mc.convertDTOToMachine(machineComboBox.getValue())
                        : null;

                MaintenanceBuilder builder = new MaintenanceBuilder();
                builder.createMaintenance();
                builder.buildExecutionDate(execDate);
                builder.buildStartDate(startDateTime);
                builder.buildEndDate(endDateTime);
                builder.buildTechnician(technicianComboBox.getValue());
                builder.buildReason(reasonField.getText());
                builder.buildComments(commentsField.getText());
                if (statusComboBox.getValue() != null)
                    builder.buildStatus(MaintenanceStatus.valueOf(statusComboBox.getValue()));
                builder.buildMachine(machine);

                mntcc.createMaintenance(builder.getMaintenance());
                goToMaintenanceList(machineDTO);

            } catch (InformationRequiredExceptionMaintenance ex) {
                handleInformationRequiredException(ex);
            } catch (Exception ex) {
                errorLabel.setText("Er is een fout opgetreden: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        this.getChildren().addAll(title, errorLabel, form);
    }

    private HBox createFormField(String labelText, Control inputControl) {
        Label label = new Label(labelText);
        label.setMinWidth(150);
        return new HBox(10, label, inputControl);
    }

    private void handleInformationRequiredException(InformationRequiredExceptionMaintenance e) {
        Map<String, Label> fieldToLabelMap = Map.of(
        		"executionDate", executionDateErrorLabel,
                "startDate", startDateErrorLabel,
                "endDate", endDateErrorLabel,
                "machine", machineErrorLabel,
                "status", statusErrorLabel,
                "reason", reasonErrorLabel,
                "technician", technicianErrorLabel
        );

        e.getInformationRequired().forEach((field, requiredElement) -> {
            String message = switch (requiredElement) {
                case EXECUTION_DATE_REQUIRED -> "Uitvoeringsdatum is verplicht";
                case START_DATE_REQUIRED -> "Starttijdstip is verplicht";
                case END_DATE_REQUIRED -> "Eindtijdstip is verplicht";
                case MACHINE_REQUIRED -> "Machine is verplicht";
                case MAINTENANCESTATUS_REQUIRED -> "Status is verplicht";
                case REASON_REQUIRED -> "Reden is verplicht";
                case TECHNICIAN_REQUIRED -> "Technieker is verplicht";
                case END_DATE_BEFORE_START -> "Eindtijd mag niet voor starttijd liggen";
                default -> "Verplicht veld";
            };

            Label label = fieldToLabelMap.get(field);
            if (label != null) {
                label.setText(message);
            } else {
                errorLabel.setText("Er is een fout opgetreden: " + message);
            }
        });
    }

    private void goToMaintenanceList(MachineDTO machineDTO) {
        mainLayout.showMaintenanceList(machineDTO);
    }

    private void resetErrorLabels() {
        errorLabel.setText("");
        startDateErrorLabel.setText("");
        endDateErrorLabel.setText("");
        technicianErrorLabel.setText("");
        reasonErrorLabel.setText("");
        machineErrorLabel.setText("");
        statusErrorLabel.setText("");
        executionDateErrorLabel.setText("");
    }

    private Label createErrorLabel() {
        Label error = new Label();
        error.getStyleClass().add("error-label");
        return error;
    }
}
