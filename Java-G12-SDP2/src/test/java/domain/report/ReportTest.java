package domain.report;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.User;
import domain.maintenance.Maintenance;
import domain.maintenance.MaintenanceController;
import domain.site.Site;
import exceptions.InformationRequiredExceptionReport;
import repository.GenericDaoJpa;
import util.Role;

public class ReportTest
{

	private ReportController reportController;
	private GenericDaoJpa<Site> mockSiteDao;
	private GenericDaoJpa<User> mockUserDao;
	private GenericDaoJpa<Report> mockReportDao;
	private MaintenanceController mockMaintenanceController;

	private User validTechnician;
	private Site validSite;
	private Maintenance validMaintenance;
	private LocalDate validStartDate;
	private LocalTime validStartTime;
	private LocalDate validEndDate;
	private LocalTime validEndTime;
	private String validReason;
	private String validRemarks;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp()
	{
		// Setup mock DAOs
		mockSiteDao = mock(GenericDaoJpa.class);
		mockUserDao = mock(GenericDaoJpa.class);
		mockReportDao = mock(GenericDaoJpa.class);
		mockMaintenanceController = mock(MaintenanceController.class);

		// Initialize controller with mocks
		reportController = new ReportController(mockSiteDao, mockUserDao, mockReportDao, mockMaintenanceController);

		// Setup valid data for tests
		validTechnician = new User();
		validTechnician.setRole(Role.TECHNIEKER);

		validSite = new Site();
		validSite.setId(1);

		validMaintenance = new Maintenance();

		validStartDate = LocalDate.now();
		validStartTime = LocalTime.of(8, 0);
		validEndDate = LocalDate.now();
		validEndTime = LocalTime.of(17, 0);
		validReason = "Routine maintenance";
		validRemarks = "Completed successfully";
	}

	@Test
	public void createValidReport_success()
	{
		Report validReport = new Report(validMaintenance, validTechnician, validStartDate, validStartTime, validEndDate,
				validEndTime, validReason, validRemarks, validSite);

		ReportBuilder builder = new ReportBuilder();
		builder.createReport();
		builder.buildMaintenance(validMaintenance);
		builder.buildTechnician(validTechnician);
		builder.buildStartDate(validStartDate);
		builder.buildStartTime(validStartTime);
		builder.buildEndDate(validEndDate);
		builder.buildEndTime(validEndTime);
		builder.buildReason(validReason);
		builder.buildRemarks(validRemarks);
		builder.buildSite(validSite);

		assertDoesNotThrow(() ->
		{
			Report report = builder.getReport();
			assertNotNull(report);
			assertEquals(validMaintenance, report.getMaintenance());
			assertEquals(validTechnician, report.getTechnician());
			assertEquals(validStartDate, report.getStartDate());
			assertEquals(validStartTime, report.getStartTime());
			assertEquals(validEndDate, report.getEndDate());
			assertEquals(validEndTime, report.getEndTime());
			assertEquals(validReason, report.getReason());
			assertEquals(validRemarks, report.getRemarks());
			assertEquals(validSite, report.getSite());
		});
	}

	@Test
	public void createReport_missingRequiredFields_throwsExcept()
	{

		ReportBuilder builder = new ReportBuilder();
		builder.createReport();

		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class, () ->
		{
			builder.getReport();
		});

		// Verify that the exception contains information about all required fields
		assertFalse(exception.getMissingElements().isEmpty());
		assertTrue(exception.getMissingElements().containsKey("maintenance"));
		assertTrue(exception.getMissingElements().containsKey("technician"));
		assertTrue(exception.getMissingElements().containsKey("startDate"));
		assertTrue(exception.getMissingElements().containsKey("startTime"));
		assertTrue(exception.getMissingElements().containsKey("endDate"));
		assertTrue(exception.getMissingElements().containsKey("endTime"));
		assertTrue(exception.getMissingElements().containsKey("reason"));
		assertTrue(exception.getMissingElements().containsKey("site"));
	}

	@Test
	public void createReport_invalidFields_throwsException()
	{

		ReportBuilder builder = new ReportBuilder();
		builder.createReport();
		builder.buildMaintenance(validMaintenance);
		builder.buildTechnician(validTechnician);
		builder.buildStartDate(validStartDate);
		builder.buildStartTime(validStartTime);
		builder.buildEndDate(validEndDate);
		builder.buildEndTime(validEndTime);
		builder.buildReason(""); // Empty reason is invalid
		builder.buildRemarks(validRemarks);
		builder.buildSite(validSite);

		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class, () ->
		{
			builder.getReport();
		});

		assertFalse(exception.getMissingElements().isEmpty());
		assertTrue(exception.getMissingElements().containsKey("reason"));
	}

	@Test
	public void createReport_invalidDateTime_throwsExc_endDateBeforeStartDate()
	{
		ReportBuilder builder = new ReportBuilder();
		builder.createReport();
		builder.buildMaintenance(validMaintenance);
		builder.buildTechnician(validTechnician);
		builder.buildStartDate(LocalDate.now());
		builder.buildStartTime(LocalTime.of(9, 0));
		builder.buildEndDate(LocalDate.now().minusDays(1)); // End date before start date
		builder.buildEndTime(LocalTime.of(17, 0));
		builder.buildReason(validReason);
		builder.buildRemarks(validRemarks);
		builder.buildSite(validSite);

		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class, () ->
		{
			builder.getReport();
		});

		assertFalse(exception.getMissingElements().isEmpty());
		assertTrue(exception.getMissingElements().containsKey("endDate"));
	}

	@Test
	public void createReport_invalidDateTime_throwsExc_endTimeBeforeStartTime()
	{

		ReportBuilder builder = new ReportBuilder();
		builder.createReport();
		builder.buildMaintenance(validMaintenance);
		builder.buildTechnician(validTechnician);
		builder.buildStartDate(LocalDate.now());
		builder.buildStartTime(LocalTime.of(14, 0));
		builder.buildEndDate(LocalDate.now());
		builder.buildEndTime(LocalTime.of(10, 0)); // End time before start time on same day
		builder.buildReason(validReason);
		builder.buildRemarks(validRemarks);
		builder.buildSite(validSite);

		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class, () ->
		{
			builder.getReport();
		});

		assertFalse(exception.getMissingElements().isEmpty());
		assertTrue(exception.getMissingElements().containsKey("endTime"));
	}
}