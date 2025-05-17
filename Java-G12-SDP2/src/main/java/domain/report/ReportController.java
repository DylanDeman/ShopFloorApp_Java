package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import domain.maintenance.Maintenance;
import domain.maintenance.MaintenanceController;
import domain.site.Site;
import domain.user.User;
import dto.ReportDTO;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionReport;
import exceptions.InvalidReportException;
import repository.GenericDaoJpa;
import util.DTOMapper;
import util.Role;

public class ReportController
{
	private GenericDaoJpa<User> userDao;
	private GenericDaoJpa<Report> reportDao;
	private GenericDaoJpa<Site> siteDao;
	private MaintenanceController maintenanceController;

	public ReportController()
	{
		this.userDao = new GenericDaoJpa<>(User.class);
		this.reportDao = new GenericDaoJpa<>(Report.class);
		this.siteDao = new GenericDaoJpa<>(Site.class);
	}

	// Constructor for testing with mock DAOs
	public ReportController(GenericDaoJpa<Site> siteDao, GenericDaoJpa<User> userDao, GenericDaoJpa<Report> reportDao,
			MaintenanceController maintenanceController)
	{
		this.siteDao = siteDao;
		this.userDao = userDao;
		this.reportDao = reportDao;
		this.maintenanceController = maintenanceController;
	}

	public List<User> getTechnicians()
	{
		return userDao.findAll().stream().filter(user -> user.getRole() == Role.TECHNIEKER).toList();
	}

	public ReportDTO createReport(Site site, Maintenance maintenance, User technician, 
            LocalDate startDate, LocalTime startTime, LocalDate endDate, 
            LocalTime endTime, String reason, String remarks) 
throws InvalidReportException
{
try
{
// Create a report using the Builder pattern
Report newReport = new Report.Builder()
.withSite(site)
.withMaintenance(maintenance)
.withTechnician(technician)
.withStartDate(startDate)
.withStartTime(startTime)
.withEndDate(endDate)
.withEndTime(endTime)
.withReason(reason)
.withRemarks(remarks)
.build();

// Validate the report before saving
validateReport(newReport);

reportDao.startTransaction();
reportDao.insert(newReport);
reportDao.commitTransaction();


return DTOMapper.toReportDTO(newReport);
} 
catch (InvalidReportException e)
{
reportDao.rollbackTransaction();
throw e;
} 
catch (Exception e)
{
reportDao.rollbackTransaction();
throw new InvalidReportException("Failed to create report: " + e.getMessage());
}
}

	private void validateReport(Report report) throws InvalidReportException
	{
		if (report == null)
		{
			throw new InvalidReportException("Report cannot be null");
		}

		// Validation should already be done by ReportBuilder, but we double-check
		// critical fields
		if (report.getTechnician() == null)
		{
			throw new InvalidReportException("Technician cannot be null");
		}

		if (report.getSite() == null)
		{
			throw new InvalidReportException("Site cannot be null");
		}
	}

	public List<Report> getReportsByTechnician(User technician)
	{
		validateTechnician(technician);
		var query = GenericDaoJpa.em.createNamedQuery("Report.findByTechnieker", Report.class);
		query.setParameter("technieker", technician);
		return query.getResultList();
	}

	public List<Report> getReportsBySite(Site site)
	{
		validateSite(site);
		var query = GenericDaoJpa.em.createNamedQuery("Report.findBySite", Report.class);
		query.setParameter("site", site);
		return query.getResultList();
	}

	private void validateTechnician(User technician)
	{
		if (technician == null)
		{
			throw new InvalidReportException("Technician cannot be null");
		}
	}

	private void validateSite(Site site)
	{
		if (site == null)
		{
			throw new InvalidReportException("Site cannot be null");
		}
	}

	
	
}