package dto;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.User;
import domain.site.Site;

public record ReportDTO(String maintenanceNumber, Site site, User technician, LocalDate startDate, LocalTime startTime,
		LocalDate endDate, LocalTime endTime, String reason, String comments) {
}