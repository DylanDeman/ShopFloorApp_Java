package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.maintenance.Maintenance;
import domain.site.Site;
import domain.user.User;

/**
 * Builder interface for creating Report objects. Specifies all the steps needed
 * to build a Report.
 */
public interface ReportBuilder
{
	ReportBuilder setMaintenance(Maintenance maintenance);

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