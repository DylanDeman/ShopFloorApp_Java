package gui.maintenance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import util.MaintenanceStatus;
import util.RequiredElementMaintenance;

public class MaintenancePlanningForm extends VBox {

    private final MainLayout mainLayout;
    private final MachineDTO machineDTO;
    private final MachineController mc;
    private final MaintenanceController mntcc;
    private final UserController uc;

    private Label errorLabel;
    private Label startDateErrorLabel, startTimeErrorLabel, endDateErrorLabel, endTimeErrorLabel, 
    machineErrorLabel, statusErrorLabel;

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

        Label headerLabel = new Label("Onderhoud inplannen");
        headerLabel.getStyleClass().add("header-label");

        VBox leftColumn = new VBox(15);
        VBox rightColumn = new VBox(15);
        HBox formRowBox = new HBox(40, leftColumn, rightColumn);
        formRowBox.setAlignment(Pos.TOP_CENTER);

        DatePicker executionDatePicker = new DatePicker();
        DatePicker startDatePicker = new DatePicker();
        ComboBox<LocalTime> startTimeField = new ComboBox<>();
        populateTimePicker(startTimeField);
        DatePicker endDatePicker = new DatePicker();
        ComboBox<LocalTime> endTimeField = new ComboBox<>();
        populateTimePicker(endTimeField);

        startTimeField.setPromptText("Starttijd");
        endTimeField.setPromptText("Eindtijd");

        startDateErrorLabel = createErrorLabel();
        startTimeErrorLabel = createErrorLabel();
        endDateErrorLabel = createErrorLabel();
        endTimeErrorLabel = createErrorLabel();
        machineErrorLabel = createErrorLabel();
        statusErrorLabel = createErrorLabel();
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
            createFormField("Datum Uitgevoerd:", executionDatePicker),
            createFormField("Start datum", startDatePicker),
            startDateErrorLabel,
            createFormField("Starttijdstip", startTimeField),
            startTimeErrorLabel,
            createFormField("Naam Technieker:", technicianComboBox),
            createFormField("Reden:", reasonField)
        );

        rightColumn.getChildren().addAll(
            createFormField("Einddatum", endDatePicker),
            endDateErrorLabel,
            createFormField("Eindtijdstip", endTimeField),
            endTimeErrorLabel,
            createFormField("Opmerkingen:", commentsField),
            createFormField("Status:", statusComboBox),
            statusErrorLabel,
            createFormField("Machine:", machineComboBox),
            machineErrorLabel
        );

        CustomButton saveButton = new CustomButton("Opslaan");
        saveButton.getStyleClass().add("create-report-button");
        saveButton.setOnAction(e -> {
            try {
                resetErrorLabels();

                // --- PRE-VALIDATION: collect missing fields before combining date+time ---
                LocalDate execDate = executionDatePicker.getValue();
                LocalDate sDate   = startDatePicker.getValue();
                LocalTime sTime   = startTimeField.getValue();
                LocalDate eDate   = endDatePicker.getValue();
                LocalTime eTime   = endTimeField.getValue();

                Map<String, RequiredElementMaintenance> missing = new HashMap<>();
                if (sDate == null) missing.put("startDate", RequiredElementMaintenance.START_DATE_REQUIRED);
                if (sTime == null) missing.put("startTime", RequiredElementMaintenance.START_TIME_REQUIRED);
                if (eDate == null) missing.put("endDate",   RequiredElementMaintenance.END_DATE_REQUIRED);
                if (eTime == null) missing.put("endTime",   RequiredElementMaintenance.END_TIME_REQUIRED);
                if (machineComboBox.getValue() == null) missing.put("machine", RequiredElementMaintenance.MACHINE_REQUIRED);
                if (!(Arrays.stream(MaintenanceStatus.values()).map((s) -> s.toString()).toList().contains(statusComboBox.getValue()))) missing.put("status", RequiredElementMaintenance.MAINTENANCESTATUS_REQUIRED);
                if (!missing.isEmpty()) {
                    throw new InformationRequiredExceptionMaintenance(missing);
                }

                MachineDTO selectedMachineDTO = machineComboBox.getValue();
                Machine machine = mc.convertDTOToMachine(selectedMachineDTO);

                MaintenanceBuilder maintenanceBuilder = new MaintenanceBuilder();
                maintenanceBuilder.createMaintenance();

                maintenanceBuilder.buildExecutionDate(execDate);
                maintenanceBuilder.buildStartDate(LocalDateTime.of(sDate, sTime));
                maintenanceBuilder.buildEndDate(  LocalDateTime.of(eDate,   eTime));
                maintenanceBuilder.buildTechnician(technicianComboBox.getValue());
                maintenanceBuilder.buildReason(reasonField.getText().trim());
                maintenanceBuilder.buildComments(commentsField.getText().trim());
                maintenanceBuilder.buildStatus(MaintenanceStatus.valueOf(statusComboBox.getValue()));
                maintenanceBuilder.buildMachine(machine);

                mntcc.createMaintenance(maintenanceBuilder.getMaintenance());
                mainLayout.showMaintenanceList();

            } catch (InformationRequiredExceptionMaintenance ex) {
                handleInformationRequiredException(ex);
            } catch (Exception ex) {
                errorLabel.setText("Er is een fout opgetreden: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        CustomButton backButton = new CustomButton("Annuleren");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> mainLayout.showMaintenanceList());

        HBox buttonBox = new HBox(10, backButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        this.getChildren().addAll(headerLabel, errorLabel, formRowBox, buttonBox);
    }

    private HBox createFormField(String labelText, Control inputControl) {
        Label label = new Label(labelText);
        label.setMinWidth(150);
        return new HBox(10, label, inputControl);
    }

    private void handleInformationRequiredException(InformationRequiredExceptionMaintenance e) {
        Map<String, Label> fieldToLabelMap = Map.of(
            "startDate", startDateErrorLabel,
            "startTime", startTimeErrorLabel,
            "endDate",   endDateErrorLabel,
            "endTime",   endTimeErrorLabel,
            "machine", machineErrorLabel,
            "status", statusErrorLabel
        );

        e.getInformationRequired().forEach((field, requiredElement) -> {
            String message = switch (requiredElement) {
                case START_DATE_REQUIRED-> "Startdatum is verplicht";
                case START_TIME_REQUIRED-> "Starttijd is verplicht";
                case END_DATE_REQUIRED	-> "Einddatum is verplicht";
                case END_TIME_REQUIRED	-> "Eindtijd is verplicht";
                case MACHINE_REQUIRED	-> "Machine is verplicht";
                case MAINTENANCESTATUS_REQUIRED -> "Status is verplicht";
                default					-> "Verplicht veld";
            };

            Label label = fieldToLabelMap.get(field);
            if (label != null) {
                label.setText(message);
            } else {
                errorLabel.setText("Er is een fout opgetreden: " + message);
            }
        });
    }

    private void resetErrorLabels() {
        errorLabel.setText("");
        startDateErrorLabel.setText("");
        startTimeErrorLabel.setText("");
        endDateErrorLabel.setText("");
        endTimeErrorLabel.setText("");
    }

    private Label createErrorLabel() {
        Label error = new Label();
        error.getStyleClass().add("error-label");
        return error;
    }
}
