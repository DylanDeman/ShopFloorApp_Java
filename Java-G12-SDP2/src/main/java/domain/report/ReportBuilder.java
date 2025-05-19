package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import domain.User;
import domain.maintenance.Maintenance;
import domain.site.Site;
import exceptions.InformationRequiredExceptionReport;
import util.RequiredElementReport;

public class ReportBuilder
{
	private Report report;
	private Map<String, RequiredElementReport> requiredElements;

	public void createReport()
	{
		report = new Report();
		requiredElements = new HashMap<>();
	}

	public void buildMaintenance(Maintenance maintenance)
	{
		report.setMaintenance(maintenance);
	}

	public void buildTechnician(User technician)
	{
		report.setTechnician(technician);
	}

	public void buildStartDate(LocalDate startDate)
	{
		report.setStartDate(startDate);
	}

	public void buildStartTime(LocalTime startTime)
	{
		report.setStartTime(startTime);
	}

	public void buildEndDate(LocalDate endDate)
	{
		report.setEndDate(endDate);
	}

	public void buildEndTime(LocalTime endTime)
	{
		report.setEndTime(endTime);
	}

	public void buildReason(String reason)
	{
		report.setReason(reason);
	}

	public void buildRemarks(String comments)
	{
		report.setRemarks(comments);
	}

	public void buildSite(Site site)
	{
		report.setSite(site);
	}

	public Report getReport() throws InformationRequiredExceptionReport
	{
		// Create a map to store missing or invalid elements
		Map<String, RequiredElementReport> missingElements = new HashMap<>();

		// Validate all required fields
		validateRequiredField(missingElements, report.getMaintenance() == null, "maintenance",
				RequiredElementReport.MAINTENANCE_REQUIRED);
		validateRequiredField(missingElements, report.getTechnician() == null, "technician",
				RequiredElementReport.TECHNICIAN_REQUIRED);
		validateRequiredField(missingElements, report.getStartDate() == null, "startDate",
				RequiredElementReport.STARTDATE_REQUIRED);
		validateRequiredField(missingElements, report.getStartTime() == null, "startTime",
				RequiredElementReport.STARTTIME_REQUIRED);
		validateRequiredField(missingElements, report.getEndDate() == null, "endDate",
				RequiredElementReport.ENDDATE_REQUIRED);
		validateRequiredField(missingElements, report.getEndTime() == null, "endTime",
				RequiredElementReport.ENDTIME_REQUIRED);
		validateRequiredField(missingElements, report.getReason() == null || report.getReason().isEmpty(), "reason",
				RequiredElementReport.REASON_REQUIRED);
		validateRequiredField(missingElements, report.getSite() == null, "site", RequiredElementReport.SITE_REQUIRED);

		// Only validate date sequence if both dates and times are available
		if (report.getStartDate() != null && report.getEndDate() != null)
		{
			// Check if end date is before start date
			if (report.getEndDate().isBefore(report.getStartDate()))
			{
				missingElements.put("endDate", RequiredElementReport.END_DATE_BEFORE_START);
			}
			// If same day, check if end time is before start time
			else if (report.getEndDate().isEqual(report.getStartDate()) && report.getStartTime() != null
					&& report.getEndTime() != null && report.getEndTime().isBefore(report.getStartTime()))
			{
				missingElements.put("endTime", RequiredElementReport.END_TIME_BEFORE_START);
			}
		}

		// If any validation failed, throw exception with all missing/invalid elements
		if (!missingElements.isEmpty())
		{
			throw new InformationRequiredExceptionReport(missingElements);
		}

		return report;
	}

	private void validateRequiredField(Map<String, RequiredElementReport> missingElements, boolean isInvalid,
			String fieldName, RequiredElementReport errorType)
	{
		if (isInvalid)
		{
			missingElements.put(fieldName, errorType);
		}
	}
}