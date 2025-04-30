package domain.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.maintenance.MaintenanceController;
import domain.maintenance.MaintenanceDTO;
import domain.site.Site;
import domain.site.SiteController;
import domain.user.User;
import exceptions.InvalidRapportException;
import repository.GenericDaoJpa;
import util.MaintenanceStatus;
import util.Role;

@ExtendWith(MockitoExtension.class)
class ReportTest
{

	private static final LocalDate VALID_START_DATE = LocalDate.of(2025, 4, 20);
	private static final LocalTime VALID_START_TIME = LocalTime.of(9, 0);
	private static final LocalDate VALID_END_DATE = LocalDate.of(2025, 4, 20);
	private static final LocalTime VALID_END_TIME = LocalTime.of(12, 0);
	private static final String VALID_REASON = "Routine maintenance";
	private static final String VALID_REMARKS = "Everything OK";

	@Mock
	private GenericDaoJpa<User> mockUserDao;

	@Mock
	private GenericDaoJpa<Report> mockReportDao;

	@Mock
	private MaintenanceController mockMaintenanceController;

	@Mock
	private SiteController mockSiteController;

	@Mock
	private User mockTechnician;

	@Mock
	private Report mockReport;

	@Mock
	private Site mockSite;

	private ReportController reportController;

	private MaintenanceDTO maintenanceDTO;

	@BeforeEach
	void setUp()
	{
		reportController = new ReportController(null, mockUserDao, mockReportDao, mockMaintenanceController);

		maintenanceDTO = new MaintenanceDTO(1, VALID_START_DATE, VALID_START_DATE.atTime(VALID_START_TIME),
				VALID_END_DATE.atTime(VALID_END_TIME), mockTechnician, VALID_REASON, VALID_REMARKS,
				MaintenanceStatus.COMPLETED, null);
	}

	@Test
	void getTechnicians_returnsOnlyTechnicians()
	{
		User technician = new User();
		technician.setRole(Role.TECHNIEKER);

		User nonTechnician = new User();
		nonTechnician.setRole(Role.ADMIN);

		when(mockUserDao.findAll()).thenReturn(List.of(technician, nonTechnician));

		List<User> technicians = reportController.getTechnicians();

		assertEquals(1, technicians.size());
		assertEquals(Role.TECHNIEKER, technicians.get(0).getRole());
	}

	@Test
	void getReportsByTechnician_nullTechnician_throwsException()
	{
		assertThrows(InvalidRapportException.class, () -> reportController.getReportsByTechnician(null));
	}

	@Test
	void getReportsBySite_nullSite_throwsException()
	{
		assertThrows(InvalidRapportException.class, () -> reportController.getReportsBySite(null));
	}
}
