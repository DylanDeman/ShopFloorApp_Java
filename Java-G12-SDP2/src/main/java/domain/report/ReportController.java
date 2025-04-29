package domain.report;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import domain.maintenance.Maintenance;
import domain.maintenance.MaintenanceController;
import domain.maintenance.MaintenanceDTO;
import domain.site.Site;
import domain.user.User;
import exceptions.InvalidRapportException;
import repository.GenericDaoJpa;
import util.Role;

public class ReportController
{
	private GenericDaoJpa<User> userDao;
	private GenericDaoJpa<Report> reportDao;

	private MaintenanceController maintenanceController; // Using the MaintenanceController for maintenance

	public ReportController()
	{
		this.userDao = new GenericDaoJpa<>(User.class);
		this.reportDao = new GenericDaoJpa<>(Report.class);
		this.maintenanceController = new MaintenanceController(); // Initialize the MaintenanceController
	}

	// Constructor for testing with mock DAOs
	public ReportController(GenericDaoJpa<Site> siteDao, GenericDaoJpa<User> userDao, GenericDaoJpa<Report> reportDao,
			MaintenanceController maintenanceController)
	{
		this.userDao = userDao;
		this.reportDao = reportDao;
		this.maintenanceController = maintenanceController;
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

			Report newReport = builder.setSite(report.getSite()).setTechnician(report.getTechnician())
					.setStartDate(report.getStartDate()).setStartTime(report.getStartTime())
					.setEndDate(report.getEndDate()).setEndTime(report.getEndTime()).setReason(report.getReason())
					.setRemarks(report.getRemarks()).build();

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

	public List<Report> getReportsByDateRange(LocalDate startDate, LocalDate endDate)
	{
		validateDateRange(startDate, endDate);
		var query = GenericDaoJpa.em.createNamedQuery("Report.findByDateRange", Report.class);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		return query.getResultList();
	}

	public List<MaintenanceDTO> getMaintenancesByTechnician(User technician)
	{
		validateTechnician(technician);
		var query = GenericDaoJpa.em.createQuery("SELECT m FROM Maintenance m WHERE m.technician = :technician",
				Maintenance.class);
		query.setParameter("technician", technician);
		List<Maintenance> maintenances = query.getResultList();

		return maintenanceController.makeMaintenanceDTOs(maintenances);
	}

	public List<MaintenanceDTO> getMaintenancesBySite(Site site)
	{
		validateSite(site);
		var query = GenericDaoJpa.em.createQuery("SELECT m FROM Maintenance m WHERE m.report.site = :site",
				Maintenance.class);
		query.setParameter("site", site);
		List<Maintenance> maintenances = query.getResultList();

		return maintenanceController.makeMaintenanceDTOs(maintenances);
	}

	public MaintenanceDTO getMaintenanceById(int id)
	{
		Maintenance maintenance = maintenanceController.getMaintenance(id);

		if (maintenance == null)
		{
			throw new InvalidRapportException("Maintenance with id " + id + " not found");
		}
		return maintenanceController.makeMaintenanceDTOs(List.of(maintenance)).get(0);
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
