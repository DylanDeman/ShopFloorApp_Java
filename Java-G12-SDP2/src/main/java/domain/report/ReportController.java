package domain.report;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import domain.maintenance.MaintenanceController;
import domain.maintenance.MaintenanceDTO;
import domain.site.Site;
import domain.site.SiteController;
import domain.user.User;
import exceptions.InvalidRapportException;
import repository.GenericDaoJpa;
import util.Role;

public class ReportController
{
	private GenericDaoJpa<User> userDao;
	private GenericDaoJpa<Report> reportDao;

	private MaintenanceController maintenanceController;
	private SiteController siteController;

	public ReportController()
	{
		this.userDao = new GenericDaoJpa<>(User.class);
		this.reportDao = new GenericDaoJpa<>(Report.class);
		this.maintenanceController = new MaintenanceController();
		this.siteController = new SiteController();
	}

	// Constructor for testing with mock DAOs
	public ReportController(GenericDaoJpa<Site> siteDao, GenericDaoJpa<User> userDao, GenericDaoJpa<Report> reportDao,
			MaintenanceController maintenanceController)
	{
		this.userDao = userDao;
		this.reportDao = reportDao;
		this.maintenanceController = maintenanceController;
		this.siteController = new SiteController();
	}

	public List<User> getTechnicians()
	{
		return userDao.findAll().stream().filter(user -> user.getRole() == Role.TECHNIEKER).toList();
	}

	public void createReport(Report report, MaintenanceDTO maintenanceDTO) throws InvalidRapportException
	{

		reportDao.startTransaction();
		try
		{
			String reportId = generateReportId();
			ReportBuilder builder = new ConcreteReportBuilder(reportId);

			Report newReport = builder.setSite(siteController.getSite(maintenanceDTO.machine().site().id()))
					.setTechnician(report.getTechnician()).setStartDate(report.getStartDate())
					.setStartTime(report.getStartTime()).setEndDate(report.getEndDate()).setEndTime(report.getEndTime())
					.setReason(report.getReason()).setRemarks(report.getRemarks())
					.setMaintenance(maintenanceController.getMaintenance(maintenanceDTO.id())).build();

			reportDao.insert(newReport);

			reportDao.commitTransaction();
		} catch (Exception e)
		{
			reportDao.rollbackTransaction();
			throw new InvalidRapportException("Er ging iets mis bij het aanmaken van een rapport: " + e.getMessage());
		}
	}

	private String generateReportId()
	{
		return "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
			throw new InvalidRapportException("Technieker cannot be null");
		}
	}

	private void validateSite(Site site)
	{
		if (site == null)
		{
			throw new InvalidRapportException("Site cannot be null");
		}
	}

	private void validateDateRange(LocalDate startDate, LocalDate endDate)
	{
		if (startDate == null || endDate == null)
		{
			throw new InvalidRapportException("Date range cannot be null");
		}

		if (endDate.isBefore(startDate))
		{
			throw new InvalidRapportException("End date cannot be before start date");
		}
	}
}
