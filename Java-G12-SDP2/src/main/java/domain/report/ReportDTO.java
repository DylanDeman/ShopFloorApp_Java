package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.site.Site;
import domain.user.User;

public record ReportDTO(String maintenanceNumber, Site site, User technician, LocalDate startDate, LocalTime startTime,
		LocalDate endDate, LocalTime endTime, String reason, String comments)
{
	// Record automatically provides constructor, getters, equals, hashCode, and
	// toString
}