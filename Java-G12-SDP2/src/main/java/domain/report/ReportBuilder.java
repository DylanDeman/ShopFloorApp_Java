package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import domain.maintenance.Maintenance;
import domain.site.Site;
import domain.user.User;
import dto.SiteDTO;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionReport;
import util.DTOMapper;
import util.RequiredElementReport;

public class ReportBuilder {
	private Report report;
	private Map<String, RequiredElementReport> requiredElements;

	public void createReport() {
		report = new Report();
		requiredElements = new HashMap<>();
	}

	public void buildMaintenance(Maintenance maintenance) {
		report.setMaintenance(maintenance);
	}

	public void buildTechnician(UserDTO technician) {
		User user = DTOMapper.toUser(technician, null);
		report.setTechnician(user);
	}

	public void buildStartDate(LocalDate startDate) {
		report.setStartDate(startDate);
	}

	public void buildStartTime(LocalTime startTime) {
		report.setStartTime(startTime);
	}

	public void buildEndDate(LocalDate endDate) {
		report.setEndDate(endDate);
	}

	public void buildEndTime(LocalTime endTime) {
		report.setEndTime(endTime);
	}

	public void buildReason(String reason) {
		report.setReason(reason);
	}

	public void buildRemarks(String comments) {
		report.setRemarks(comments);
	}

	public void buildSite(SiteDTOWithoutMachines site) {
		Site site = DTOMapper.toSite(site, null);
		report.setSite(site);
	}

	public Report getReport() throws InformationRequiredExceptionReport {
		// Verplichte velden controleren
		if (report.getMaintenance() == null) {
			requiredElements.put("maintenance", RequiredElementReport.MAINTENANCE_REQUIRED);
		}
		if (report.getTechnician() == null) {
			requiredElements.put("technician", RequiredElementReport.TECHNICIAN_REQUIRED);
		}
		if (report.getStartDate() == null) {
			requiredElements.put("startDate", RequiredElementReport.STARTDATE_REQUIRED);
		}
		if (report.getStartTime() == null) {
			requiredElements.put("startTime", RequiredElementReport.STARTTIME_REQUIRED);
		}
		if (report.getEndDate() == null) {
			requiredElements.put("endDate", RequiredElementReport.ENDDATE_REQUIRED);
		}
		if (report.getEndTime() == null) {
			requiredElements.put("endTime", RequiredElementReport.ENDTIME_REQUIRED);
		}
		if (report.getReason() == null || report.getReason().isEmpty()) {
			requiredElements.put("reason", RequiredElementReport.REASON_REQUIRED);
		}
		if (report.getSite() == null) {
			requiredElements.put("site", RequiredElementReport.SITE_REQUIRED);
		}

		// Datum/tijd validaties
		if (report.getEndDate() != null && report.getStartDate() != null) {
			if (report.getEndDate().isBefore(report.getStartDate())) {
				requiredElements.put("endDate", RequiredElementReport.END_DATE_BEFORE_START);
			} else if (report.getEndDate().equals(report.getStartDate()) && report.getEndTime() != null
					&& report.getStartTime() != null && report.getEndTime().isBefore(report.getStartTime())) {
				requiredElements.put("endTime", RequiredElementReport.END_TIME_BEFORE_START);
			}
		}

		if (!requiredElements.isEmpty()) {
			throw new InformationRequiredExceptionReport(requiredElements);
		}

		return report;
	}
}