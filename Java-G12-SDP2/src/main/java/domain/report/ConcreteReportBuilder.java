package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.maintenance.MaintenanceDTO; // Assuming MaintenanceDTO is in this package
import domain.site.Site;
import domain.user.User;

/**
 * Concrete implementation of the RapportBuilder interface. Builds and assembles
 * the parts of the Rapport object.
 */
public class ConcreteReportBuilder implements ReportBuilder
{
	private String reportId;
	private Site site;
	private String maintenanceNumber;
	private MaintenanceDTO selectedMaintenanceDTO; // Updated to use MaintenanceDTO
	private User technieker;
	private LocalDate startDate;
	private LocalTime startTime;
	private LocalDate endDate;
	private LocalTime endTime;
	private String reden;
	private String opmerkingen;

	public ConcreteReportBuilder(String reportId)
	{
		this.reportId = reportId;
	}

	@Override
	public ReportBuilder setSite(Site site)
	{
		this.site = site;
		return this;
	}

	@Override
	public ReportBuilder setMaintenanceDTO(MaintenanceDTO maintenanceDTO) // Method to accept MaintenanceDTO
	{
		this.selectedMaintenanceDTO = maintenanceDTO;
		return this;
	}

	@Override
	public ReportBuilder setTechnician(User technieker)
	{
		this.technieker = technieker;
		return this;
	}

	@Override
	public ReportBuilder setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
		return this;
	}

	@Override
	public ReportBuilder setStartTime(LocalTime startTime)
	{
		this.startTime = startTime;
		return this;
	}

	@Override
	public ReportBuilder setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
		return this;
	}

	@Override
	public ReportBuilder setEndTime(LocalTime endTime)
	{
		this.endTime = endTime;
		return this;
	}

	@Override
	public ReportBuilder setReason(String reden)
	{
		this.reden = reden;
		return this;
	}

	@Override
	public ReportBuilder setRemarks(String opmerkingen)
	{
		this.opmerkingen = opmerkingen;
		return this;
	}

	@Override
	public Report build()
	{
		validateFields();

		return new Report(selectedMaintenanceDTO, technieker, startDate, startTime, endDate, endTime, reden,
				opmerkingen);
	}

	private void validateFields()
	{
		if (site == null)
		{
			throw new IllegalStateException("Site cannot be null");
		}
		if (selectedMaintenanceDTO == null)
		{
			throw new IllegalStateException("MaintenanceDTO cannot be null");
		}
		if (technieker == null)
		{
			throw new IllegalStateException("Technieker cannot be null");
		}
		if (startDate == null)
		{
			throw new IllegalStateException("StartDate cannot be null");
		}
		if (startTime == null)
		{
			throw new IllegalStateException("StartTime cannot be null");
		}
		if (endDate == null)
		{
			throw new IllegalStateException("EndDate cannot be null");
		}
		if (endTime == null)
		{
			throw new IllegalStateException("EndTime cannot be null");
		}
		if (reden == null || reden.trim().isEmpty())
		{
			throw new IllegalStateException("Reden cannot be null or empty");
		}

		if (endDate.isBefore(startDate))
		{
			throw new IllegalStateException("End date cannot be before start date");
		}
		if (endDate.isEqual(startDate) && endTime.isBefore(startTime))
		{
			throw new IllegalStateException("End time cannot be before start time on the same day");
		}
	}

}
