package util;

public enum RequiredElementMaintenance implements RequiredElement {
    EXECUTION_DATE_REQUIRED("Execution date is required!"),
    START_DATE_REQUIRED("Start date is required!"),
    END_DATE_REQUIRED("End date is required!"),
    TECHNICIAN_REQUIRED("Technician is required!"),
    REASON_REQUIRED("Reason is required!"),
    MAINTENANCESTATUS_REQUIRED("Maintenance status is required!"),
    MACHINE_REQUIRED("Machine is required!"),
    END_DATE_BEFORE_START("End date cannot be before start date!");

    private final String message;

    RequiredElementMaintenance(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}