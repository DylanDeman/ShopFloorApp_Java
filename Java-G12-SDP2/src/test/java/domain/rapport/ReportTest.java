package domain.rapport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.maintenance.MaintenanceDTO;
import domain.report.Report;
import domain.site.Site;
import domain.user.User;
import util.MaintenanceStatus;

@ExtendWith(MockitoExtension.class)
class ReportTest
{

	private static final String VALID_MAINTENANCE_NR = "OND456";
	private static final LocalDate VALID_START_DATE = LocalDate.of(2025, 4, 20);
	private static final LocalTime VALID_START_TIME = LocalTime.of(9, 0);
	private static final LocalDate VALID_END_DATE = LocalDate.of(2025, 4, 20);
	private static final LocalTime VALID_END_TIME = LocalTime.of(12, 0);
	private static final String VALID_REASON = "Regulier onderhoud";
	private static final String VALID_REMARKS = "Alles in orde";

	@Mock
	private Site mockSite;

	@Mock
	private User mockTechnician;

	private MaintenanceDTO mockMaintenanceDTO;
	private Report report;

	@BeforeEach
	void setUp()
	{
		mockMaintenanceDTO = new MaintenanceDTO(1, VALID_START_DATE, VALID_START_DATE.atTime(VALID_START_TIME),
				VALID_END_DATE.atTime(VALID_END_TIME), mockTechnician, VALID_REASON, VALID_REMARKS,
				MaintenanceStatus.COMPLETED, null);
	}

	@Test
	void constructor_validParameters_createsReport()
	{
		assertAll(() -> assertEquals(mockTechnician, report.getTechnician()),
				() -> assertEquals(VALID_START_DATE, report.getStartDate()),
				() -> assertEquals(VALID_START_TIME, report.getStartTime()),
				() -> assertEquals(VALID_END_DATE, report.getEndDate()),
				() -> assertEquals(VALID_END_TIME, report.getEndTime()),
				() -> assertEquals(VALID_REASON, report.getReason()),
				() -> assertEquals(VALID_REMARKS, report.getRemarks()));
	}

	@Test
	void equalsAndHashCode_sameId_areEqual()
	{
		Report anotherReport = new Report(mockMaintenanceDTO, mockTechnician, VALID_START_DATE, VALID_START_TIME,
				VALID_END_DATE, VALID_END_TIME, VALID_REASON, VALID_REMARKS);

		anotherReport = report;

		assertEquals(report, anotherReport);
		assertEquals(report.hashCode(), anotherReport.hashCode());
	}

	@Test
	void equalsAndHashCode_differentId_notEqual()
	{
		MaintenanceDTO differentMaintenanceDTO = new MaintenanceDTO(2, VALID_START_DATE,
				VALID_START_DATE.atTime(VALID_START_TIME), VALID_END_DATE.atTime(VALID_END_TIME), mockTechnician,
				VALID_REASON, VALID_REMARKS, MaintenanceStatus.COMPLETED, null);

		Report differentReport = new Report(differentMaintenanceDTO, mockTechnician, VALID_START_DATE, VALID_START_TIME,
				VALID_END_DATE, VALID_END_TIME, "Different Reason", "Different Remarks");

		assertNotEquals(report, differentReport);
		assertNotEquals(report.hashCode(), differentReport.hashCode());
	}

	@Test
	void getters_returnCorrectValues()
	{
		assertAll(() -> assertEquals(mockTechnician, report.getTechnician()),
				() -> assertEquals(VALID_START_DATE, report.getStartDate()),
				() -> assertEquals(VALID_START_TIME, report.getStartTime()),
				() -> assertEquals(VALID_END_DATE, report.getEndDate()),
				() -> assertEquals(VALID_END_TIME, report.getEndTime()),
				() -> assertEquals(VALID_REASON, report.getReason()),
				() -> assertEquals(VALID_REMARKS, report.getRemarks()));
	}
}
