package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import exceptions.InformationRequiredExceptionReport;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.RequiredElementReport;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "reportId")
@Getter
@Setter
public class Report implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reportId;

	@ManyToOne
	@JoinColumn(name = "site_id")
	private Site site;

	@ManyToOne
	@JoinColumn(name = "maintenance_id")
	private Maintenance maintenance;

	@ManyToOne
	private User technician;

	private LocalDate startDate;
	private LocalTime startTime;
	private LocalDate endDate;
	private LocalTime endTime;

	private String reason;
	private String remarks;

	public Report(Maintenance selectedMaintenance, User selectedTechnician, LocalDate startDate, LocalTime startTime,
			LocalDate endDate, LocalTime endTime, String reason, String remarks, Site site)
	{

		this.technician = selectedTechnician;
		this.startDate = startDate;
		this.startTime = startTime;
		this.maintenance = selectedMaintenance;
		this.endDate = endDate;
		this.endTime = endTime;
		this.reason = reason;
		this.remarks = remarks;
	}

	public static class Builder
	{
		private Site site;
		private User technician;
		private LocalDate startDate;
		private LocalTime startTime;
		private LocalDate endDate;
		private LocalTime endTime;
		private String reason;
		private String remarks;
		private Maintenance maintenance;

		protected Report report;

		public Builder()
		{
		}

		public Builder buildSite(Site site)
		{
			this.site = site;
			return this;
		}

		public Builder buildTechnician(User technician)
		{
			this.technician = technician;
			return this;
		}

		public Builder buildstartDate(LocalDate startDate)
		{
			this.startDate = startDate;
			return this;
		}

		public Builder buildStartTime(LocalTime startTime)
		{
			this.startTime = startTime;
			return this;
		}

		public Builder buildEndDate(LocalDate endDate)
		{
			this.endDate = endDate;
			return this;
		}

		public Builder buildEndTime(LocalTime endTime)
		{
			this.endTime = endTime;
			return this;
		}

		public Builder buildReason(String reason)
		{
			this.reason = reason;
			return this;
		}

		public Builder buildRemarks(String remarks)
		{
			this.remarks = remarks;
			return this;
		}

		public Builder buildMaintenance(Maintenance maintenance)
		{
			this.maintenance = maintenance;
			return this;
		}

		public Report build() throws InformationRequiredExceptionReport
		{
			validateRequiredFields();

			report = new Report();
			report.setSite(site);
			report.setTechnician(technician);
			report.setStartDate(startDate);
			report.setStartTime(startTime);
			report.setEndDate(endDate);
			report.setEndTime(endTime);
			report.setReason(reason);
			report.setRemarks(remarks);
			report.setMaintenance(maintenance);

			return report;
		}

		private void validateRequiredFields() throws InformationRequiredExceptionReport
		{
			Map<String, RequiredElementReport> requiredElements = new HashMap<>();

			if (report.getMaintenance() == null)
			{
				requiredElements.put("maintenance", RequiredElementReport.MAINTENANCE_REQUIRED);
			}

			if (report.getSite() == null)
			{
				requiredElements.put("site", RequiredElementReport.SITE_REQUIRED);
			}

			if (report.getTechnician() == null)
			{
				requiredElements.put("technician", RequiredElementReport.TECHNICIAN_REQUIRED);
			}

			if (report.getStartDate() == null)
			{
				requiredElements.put("startDate", RequiredElementReport.STARTDATE_REQUIRED);
			}

			if (report.getStartTime() == null)
			{
				requiredElements.put("startTime", RequiredElementReport.STARTTIME_REQUIRED);
			}

			if (report.getEndDate() == null)
			{
				requiredElements.put("endDate", RequiredElementReport.ENDDATE_REQUIRED);
			}

			if (report.getEndTime() == null)
			{
				requiredElements.put("endTime", RequiredElementReport.ENDTIME_REQUIRED);
			}

			if (report.getReason() == null || report.getReason().isEmpty())
			{
				requiredElements.put("reason", RequiredElementReport.REASON_REQUIRED);
			}

			if (report.getStartDate() != null && report.getEndDate() != null)
			{
				if (report.getEndDate().isBefore(report.getStartDate()))
				{
					requiredElements.put("endDate", RequiredElementReport.END_DATE_BEFORE_START);
				} else if (report.getEndDate().isEqual(report.getStartDate()) && report.getStartTime() != null
						&& report.getEndTime() != null && report.getEndTime().isBefore(report.getStartTime()))
				{
					requiredElements.put("endTime", RequiredElementReport.END_TIME_BEFORE_START);
				}
			}

			if (!requiredElements.isEmpty())
			{
				throw new InformationRequiredExceptionReport(requiredElements);
			}
		}
	}
}