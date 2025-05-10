package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import domain.maintenance.Maintenance;
import domain.maintenance.MaintenanceController;
import domain.site.Site;
import domain.site.SiteController;
import domain.user.User;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionReport;
import exceptions.InvalidRapportException;
import repository.GenericDaoJpa;
import util.DTOMapper;
import util.Role;

public class ReportController {
	private GenericDaoJpa<User> userDao;
	private GenericDaoJpa<Report> reportDao;

	private MaintenanceController maintenanceController;
	private SiteController siteController;

	public ReportController() {
		this.userDao = new GenericDaoJpa<>(User.class);
		this.reportDao = new GenericDaoJpa<>(Report.class);
		this.maintenanceController = new MaintenanceController();
		this.siteController = new SiteController();
	}

	// Constructor for testing with mock DAOs
	public ReportController(GenericDaoJpa<Site> siteDao, GenericDaoJpa<User> userDao, GenericDaoJpa<Report> reportDao,
			MaintenanceController maintenanceController) {
		this.userDao = userDao;
		this.reportDao = reportDao;
		this.maintenanceController = maintenanceController;
		this.siteController = new SiteController();
	}

	public List<User> getTechnicians() {
		return userDao.findAll().stream().filter(user -> user.getRole() == Role.TECHNIEKER).toList();
	}

	public void createReport(Report report) {
		try {
			reportDao.startTransaction();
			reportDao.insert(report);
			reportDao.commitTransaction();
		} catch (Exception e) {
			reportDao.rollbackTransaction();
			throw e;
		}
	}

	private String generateReportId() {
		return "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	public List<Report> getReportsByTechnician(User technician) {
		validateTechnician(technician);
		var query = GenericDaoJpa.em.createNamedQuery("Report.findByTechnieker", Report.class);
		query.setParameter("technieker", technician);
		return query.getResultList();
	}

	public List<Report> getReportsBySite(Site site) {
		validateSite(site);
		var query = GenericDaoJpa.em.createNamedQuery("Report.findBySite", Report.class);
		query.setParameter("site", site);
		return query.getResultList();
	}

	private void validateTechnician(User technician) {
		if (technician == null) {
			throw new InvalidRapportException("Technieker cannot be null");
		}
	}

	private void validateSite(Site site) {
		if (site == null) {
			throw new InvalidRapportException("Site cannot be null");
		}
	}

	private void validateDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			throw new InvalidRapportException("Date range cannot be null");
		}

		if (endDate.isBefore(startDate)) {
			throw new InvalidRapportException("End date cannot be before start date");
		}
	}

	public Report createReport(Maintenance maintenance, UserDTO technician, LocalDate startDate, LocalTime startTime,
			LocalDate endDate, LocalTime endTime, String reason, String remarks, SiteDTOWithoutMachines site)
			throws InformationRequiredExceptionReport {
		ReportBuilder builder = new ReportBuilder();

		User technicianObject = DTOMapper.toUser(technician, null);
		Site siteObject = DTOMapper.toSite(site, null);

		builder.createReport();
		builder.buildMaintenance(maintenance);
		builder.buildTechnician(technicianObject);
		builder.buildStartDate(startDate);
		builder.buildStartTime(startTime);
		builder.buildEndDate(endDate);
		builder.buildEndTime(endTime);
		builder.buildReason(reason);
		builder.buildRemarks(remarks);
		builder.buildSite(siteObject);

		Report report = builder.getReport();

		try {
			reportDao.startTransaction();
			reportDao.insert(report);
			reportDao.commitTransaction();
			return report;
		} catch (Exception e) {
			reportDao.rollbackTransaction();
			throw e;
		}
	}
}
