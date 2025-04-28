package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import domain.machine.Machine;
import domain.report.ReportDTO;
import domain.site.Site;
import domain.user.User;
import exceptions.InvalidRapportException;
import repository.GenericDaoJpa;
import util.Role;

public class ReportController
{
	private GenericDaoJpa<Site> siteDao;
	private GenericDaoJpa<User> userDao;
	private GenericDaoJpa<Report> reportDao;
	private GenericDaoJpa<Machine> machineDao;

	public ReportController()
	{
		this.siteDao = new GenericDaoJpa<>(Site.class);
		this.userDao = new GenericDaoJpa<>(User.class);
		this.reportDao = new GenericDaoJpa<>(Report.class);
		this.machineDao = new GenericDaoJpa<>(Machine.class);
	}

	// Constructor for testing with mock DAOs
	public ReportController(GenericDaoJpa<Site> siteDao, GenericDaoJpa<User> userDao, GenericDaoJpa<Report> reportDao,
			GenericDaoJpa<Machine> machineDao)
	{
		this.siteDao = siteDao;
		this.userDao = userDao;
		this.reportDao = reportDao;
		this.machineDao = machineDao;
	}

	public List<User> getTechnicians()
	{
		return userDao.findAll().stream().filter(user -> user.getRole() == Role.TECHNIEKER).toList();
	}

	public String generateNextMaintenanceNumber(Site site)
	{
		// Query to count reports for this site
		var query = GenericDaoJpa.em.createQuery("SELECT COUNT(r) FROM Report r WHERE r.site.id = :siteId", Long.class);
		query.setParameter("siteId", site.getId());
		Long reportCount = query.getSingleResult();

		// Format: SITE-XXX where XXX is a sequential number
		String sitePrefix = site.getSiteName().substring(0, Math.min(site.getSiteName().length(), 4)).toUpperCase()
				.replaceAll("[^A-Z0-9]", "");

		// Increment by 1 for the new report
		return String.format("%s-%03d", sitePrefix, reportCount + 1);
	}

	public Report createReport(ReportDTO reportDTO) throws InvalidRapportException
	{
		// Validate DTO
		validateReportDTO(reportDTO);

		// Start transaction for database operations
		reportDao.startTransaction();

		try
		{
			// Generate unique ID for the new report
			String reportId = generateReportId();

			// Use the Builder pattern to create the report
			RapportBuilder builder = new ConcreteReportBuilder(reportId);

			// Either use director for standard flows or build directly for custom flows
			Report newReport;

			if (reportDTO.reason().equalsIgnoreCase("Regulier onderhoud"))
			{
				// Use Director for standard maintenance report
				ReportDirector director = new ReportDirector(builder);
				newReport = director.constructStandardMaintenanceRapport(reportId, reportDTO.site(),
						reportDTO.maintenanceNumber(), reportDTO.technician(), reportDTO.startDate(),
						reportDTO.startTime(), reportDTO.endDate(), reportDTO.endTime());
			} else
			{
				// Use builder directly for custom report
				newReport = builder.setSite(reportDTO.site()).setMaintenanceNr(reportDTO.maintenanceNumber())
						.setTechnician(reportDTO.technician()).setStartDate(reportDTO.startDate())
						.setStartTime(reportDTO.startTime()).setEndDate(reportDTO.endDate())
						.setEndTime(reportDTO.endTime()).setReason(reportDTO.reason()).setRemarks(reportDTO.comments())
						.build();
			}

			// Save the new report
			reportDao.insert(newReport);

			// Commit the transaction
			reportDao.commitTransaction();

			return newReport;
		} catch (Exception e)
		{
			// Rollback the transaction in case of any error
			reportDao.rollbackTransaction();
			throw new InvalidRapportException("Error creating report: " + e.getMessage());
		}
	}

	private void validateReportDTO(ReportDTO reportDTO) throws InvalidRapportException
	{
		if (reportDTO.technician() == null)
		{
			throw new InvalidRapportException("Technieker moet geselecteerd worden");
		}

		if (reportDTO.reason() == null || reportDTO.reason().isEmpty())
		{
			throw new InvalidRapportException("Reden mag niet leeg zijn");
		}

		if (reportDTO.startDate() == null || reportDTO.endDate() == null)
		{
			throw new InvalidRapportException("Start- en einddatum moeten ingevuld worden");
		}

		if (reportDTO.startTime() == null || reportDTO.endTime() == null)
		{
			throw new InvalidRapportException("Start- en eindtijd moeten ingevuld worden");
		}

		// Check if end date/time is after start date/time
		LocalDate startDate = reportDTO.startDate();
		LocalDate endDate = reportDTO.endDate();
		LocalTime startTime = reportDTO.startTime();
		LocalTime endTime = reportDTO.endTime();

		if (endDate.isBefore(startDate))
		{
			throw new InvalidRapportException("Einddatum kan niet vóór startdatum liggen");
		}

		if (endDate.isEqual(startDate) && endTime.isBefore(startTime))
		{
			throw new InvalidRapportException("Eindtijd kan niet vóór starttijd liggen op dezelfde dag");
		}
	}

	private String generateReportId()
	{
		// Simple UUID-based ID generation
		return "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	public List<Report> getReportsByTechnician(User technician)
	{
		if (technician == null)
		{
			throw new InvalidRapportException("Technieker cannot be null");
		}

		var query = GenericDaoJpa.em.createNamedQuery("Report.findByTechnieker", Report.class);
		query.setParameter("technieker", technician);
		return query.getResultList();
	}

	public List<Report> getReportsBySite(Site site)
	{
		if (site == null)
		{
			throw new InvalidRapportException("Site cannot be null");
		}

		var query = GenericDaoJpa.em.createNamedQuery("Report.findBySite", Report.class);
		query.setParameter("site", site);
		return query.getResultList();
	}

	public List<Report> getReportsByDateRange(LocalDate startDate, LocalDate endDate)
	{
		if (startDate == null || endDate == null)
		{
			throw new InvalidRapportException("Date range cannot be null");
		}

		if (endDate.isBefore(startDate))
		{
			throw new InvalidRapportException("End date cannot be before start date");
		}

		var query = GenericDaoJpa.em.createNamedQuery("Report.findByDateRange", Report.class);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		return query.getResultList();
	}
}