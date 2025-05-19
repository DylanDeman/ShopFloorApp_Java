package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import domain.User;
import domain.maintenance.Maintenance;
import domain.maintenance.MaintenanceController;
import domain.site.Site;
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

	public void createReport(Report report) throws InvalidReportException
	{
		try
		{
			// Validate the report before saving
			validateReport(report);

			reportDao.startTransaction();
			reportDao.insert(report);
			reportDao.commitTransaction();
		} catch (InvalidReportException e)
		{
			reportDao.rollbackTransaction();
			throw e;
		} catch (Exception e)
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

	public Report createReport(Maintenance maintenance, UserDTO technician, LocalDate startDate, LocalTime startTime,
			LocalDate endDate, LocalTime endTime, String reason, String remarks, SiteDTOWithoutMachines siteDTO)
			throws InformationRequiredExceptionReport
	{

		// Convert DTOs to entities
		User technicianEntity = DTOMapper.toUser(technician, null);
		Site siteEntity = DTOMapper.toSite(siteDTO, null);

		// Use the builder to create and validate the report
		ReportBuilder builder = new ReportBuilder();
		builder.createReport();
		builder.buildMaintenance(maintenance);
		builder.buildTechnician(technicianEntity);
		builder.buildStartDate(startDate);
		builder.buildStartTime(startTime);
		builder.buildEndDate(endDate);
		builder.buildEndTime(endTime);
		builder.buildReason(reason);
		builder.buildRemarks(remarks);
		builder.buildSite(siteEntity);

		// This will throw InformationRequiredExceptionReport if validation fails
		Report report = builder.getReport();

		try
		{
			reportDao.startTransaction();
			reportDao.insert(report);
			reportDao.commitTransaction();
			return report;
		} catch (Exception e)
		{
			reportDao.rollbackTransaction();
			throw new InvalidReportException("Failed to persist report: " + e.getMessage());
		}
	}
}