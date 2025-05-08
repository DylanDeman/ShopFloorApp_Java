package util;

public enum MaintenanceStatus {
	COMPLETED("Voltooid"), IN_PROGRESS("In uitvoering"), PLANNED("Ingepland");
	
	private final String label;
	
	private MaintenanceStatus(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
