package domain.maintenance;

import java.time.LocalDate;
import java.time.LocalDateTime;

import domain.rapport.Rapport;
import domain.user.User;
import util.MaintenanceStatus;

public record MaintenanceDTO(int id, LocalDate executionDate, LocalDateTime startDate, LocalDateTime endDate,
		User technician, String reason, String comments, MaintenanceStatus status, Rapport report
		) {

}

