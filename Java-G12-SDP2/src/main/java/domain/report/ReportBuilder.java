package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.maintenance.MaintenanceDTO;
import domain.site.Site;
import domain.user.User;

/**
 * Builder interface for creating Rapport objects. Specifies all the steps
 * needed to build a Rapport.
 */
public interface ReportBuilder
{
	ReportBuilder setMaintenanceDTO(MaintenanceDTO maintenanceDTO);

	ReportBuilder setSite(Site site);

	ReportBuilder setTechnician(User Technician);

	ReportBuilder setStartDate(LocalDate startDate);

	ReportBuilder setStartTime(LocalTime startTime);

	ReportBuilder setEndDate(LocalDate endDate);

	ReportBuilder setEndTime(LocalTime endTime);

	ReportBuilder setReason(String reason);

	ReportBuilder setRemarks(String remarks);

	Report build();

}