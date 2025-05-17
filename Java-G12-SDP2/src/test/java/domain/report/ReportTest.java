package domain.report;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.maintenance.Maintenance;
import domain.maintenance.MaintenanceController;
import domain.site.Site;
import domain.user.User;
import exceptions.InvalidReportException;
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

	@BeforeEach
	public void setUp()
	{
		mockSiteDao = mock(GenericDaoJpa.class);
		mockUserDao = mock(GenericDaoJpa.class);
		mockReportDao = mock(GenericDaoJpa.class);
		mockMaintenanceController = mock(MaintenanceController.class);

		reportController = new ReportController(mockSiteDao, mockUserDao, mockReportDao, mockMaintenanceController);

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
		assertDoesNotThrow(() ->
		{
			var dto = reportController.createReport(validSite, validMaintenance, validTechnician, validStartDate,
					validStartTime, validEndDate, validEndTime, validReason, validRemarks);
			assertNotNull(dto);
		});
	}

	@Test
	public void createReport_missingTechnician_throwsException()
	{
		InvalidReportException exception = assertThrows(InvalidReportException.class,
				() -> reportController.createReport(validSite, validMaintenance, null, validStartDate, validStartTime,
						validEndDate, validEndTime, validReason, validRemarks));
		assertTrue(exception.getMessage().contains("Technician"));
	}

	@Test
	public void createReport_missingSite_throwsException()
	{
		InvalidReportException exception = assertThrows(InvalidReportException.class,
				() -> reportController.createReport(null, validMaintenance, validTechnician, validStartDate,
						validStartTime, validEndDate, validEndTime, validReason, validRemarks));
		assertTrue(exception.getMessage().contains("Site"));
	}
}
