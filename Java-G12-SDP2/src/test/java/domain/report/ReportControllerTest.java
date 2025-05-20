package domain.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.Maintenance;
import domain.MaintenanceController;
import domain.Report;
import domain.ReportController;
import domain.Site;
import domain.User;
import dto.ReportDTO;
import exceptions.InvalidReportException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import repository.GenericDaoJpa;
import util.DTOMapper;
import util.Role;

class ReportControllerTest
{

	@Mock
	private GenericDaoJpa<User> userDao;

	@Mock
	private GenericDaoJpa<Report> reportDao;

	@Mock
	private GenericDaoJpa<Site> siteDao;

	@Mock
	private MaintenanceController maintenanceController;

	@Mock
	private EntityManager entityManager;

	@Mock
	private TypedQuery<Report> typedQuery;

	@InjectMocks
	private ReportController reportController;

	private User technician;
	private Site site;
	private Maintenance maintenance;

	@BeforeEach
	void setUp()
	{
		MockitoAnnotations.openMocks(this);
		reportController = new ReportController(siteDao, userDao, reportDao, maintenanceController);

		technician = mock(User.class);
		when(technician.getRole()).thenReturn(Role.TECHNIEKER);

		site = mock(Site.class);
		maintenance = mock(Maintenance.class);
	}

	@Test
	void getTechnicians_shouldReturnOnlyTechnicians()
	{
		User technician1 = mock(User.class);
		User technician2 = mock(User.class);
		User admin = mock(User.class);

		when(technician1.getRole()).thenReturn(Role.TECHNIEKER);
		when(technician2.getRole()).thenReturn(Role.TECHNIEKER);
		when(admin.getRole()).thenReturn(Role.ADMINISTRATOR);

		when(userDao.findAll()).thenReturn(Arrays.asList(technician1, technician2, admin));

		List<User> result = reportController.getTechnicians();

		assertEquals(2, result.size());
		assertTrue(result.contains(technician1));
		assertTrue(result.contains(technician2));
		assertFalse(result.contains(admin));
	}

	@Test
	void getTechnicians_shouldReturnEmptyList_whenNoTechnicians()
	{
		User admin = mock(User.class);
		when(admin.getRole()).thenReturn(Role.ADMINISTRATOR);

		when(userDao.findAll()).thenReturn(Collections.singletonList(admin));

		List<User> result = reportController.getTechnicians();

		assertTrue(result.isEmpty());
	}

	@Test
	void createReport_shouldCreateAndSaveReport() throws InvalidReportException
	{
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(9, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine maintenance";
		String remarks = "All systems checked";

		ReportDTO mockReportDTO = mock(ReportDTO.class);

		try (var mockedStatic = mockStatic(DTOMapper.class))
		{
			mockedStatic.when(() -> DTOMapper.toReportDTO(any(Report.class))).thenReturn(mockReportDTO);

			ReportDTO result = reportController.createReport(site, maintenance, technician, startDate, startTime,
					endDate, endTime, reason, remarks);

			verify(reportDao).startTransaction();
			verify(reportDao).insert(any(Report.class));
			verify(reportDao).commitTransaction();

			assertEquals(mockReportDTO, result);
		}
	}

	@ParameterizedTest
	@MethodSource("invalidReportParameters")
	void createReport_shouldThrowInvalidReportException_whenInvalidParameters(Site site, Maintenance maintenance,
			User technician)
	{

		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(9, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine maintenance";
		String remarks = "All systems checked";

		assertThrows(InvalidReportException.class, () -> reportController.createReport(site, maintenance, technician,
				startDate, startTime, endDate, endTime, reason, remarks));

		verify(reportDao).rollbackTransaction();
	}

	static Stream<Arguments> invalidReportParameters()
	{
		return Stream.of(Arguments.of(null, mock(Maintenance.class), mock(User.class)),
				Arguments.of(mock(Site.class), mock(Maintenance.class), null));
	}

	@Test
	void createReport_shouldRollbackTransaction_whenExceptionOccurs()
	{
		doThrow(new RuntimeException("Database error")).when(reportDao).insert(any(Report.class));

		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(9, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine maintenance";
		String remarks = "All systems checked";

		assertThrows(InvalidReportException.class, () -> reportController.createReport(site, maintenance, technician,
				startDate, startTime, endDate, endTime, reason, remarks));

		verify(reportDao).rollbackTransaction();
	}

	@Test
	void getReportsByTechnician_shouldReturnReportsForTechnician()
	{
		List<Report> expectedReports = Arrays.asList(mock(Report.class), mock(Report.class));

		var query = mock(jakarta.persistence.TypedQuery.class);
		when(query.setParameter("technieker", technician)).thenReturn(query);
		when(query.getResultList()).thenReturn(expectedReports);

		reportController = spy(reportController);
		doReturn(expectedReports).when(reportController).getReportsByTechnician(technician);

		List<Report> result = reportController.getReportsByTechnician(technician);

		assertEquals(expectedReports, result);
	}

	@Test
	void getReportsByTechnician_shouldThrowException_whenTechnicianIsNull()
	{
		assertThrows(InvalidReportException.class, () -> reportController.getReportsByTechnician(null));
	}

	@Test
	void getReportsBySite_shouldReturnReportsForSite()
	{
		List<Report> expectedReports = Arrays.asList(mock(Report.class), mock(Report.class));

		var query = mock(jakarta.persistence.TypedQuery.class);
		when(query.setParameter("site", site)).thenReturn(query);
		when(query.getResultList()).thenReturn(expectedReports);

		reportController = spy(reportController);
		doReturn(expectedReports).when(reportController).getReportsBySite(site);

		List<Report> result = reportController.getReportsBySite(site);

		assertEquals(expectedReports, result);
	}

	@Test
	void getReportsBySite_shouldThrowException_whenSiteIsNull()
	{
		assertThrows(InvalidReportException.class, () -> reportController.getReportsBySite(null));
	}
}