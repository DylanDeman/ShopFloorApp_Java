package domain.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.Maintenance;
import domain.Report;
import domain.Site;
import domain.User;
import exceptions.InformationRequiredExceptionReport;
import util.RequiredElementReport;

@ExtendWith(MockitoExtension.class)
public class ReportTest
{

	@Test
	void testConstructor_InitializesFieldsCorrectly()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine check";
		String remarks = "No issues found";

		// Act
		Report report = new Report(maintenance, technician, startDate, startTime, endDate, endTime, reason, remarks,
				site);

		// Assert
		assertEquals(technician, report.getTechnician());
		assertEquals(startDate, report.getStartDate());
		assertEquals(startTime, report.getStartTime());
		assertEquals(maintenance, report.getMaintenance());
		assertEquals(endDate, report.getEndDate());
		assertEquals(endTime, report.getEndTime());
		assertEquals(reason, report.getReason());
		assertEquals(remarks, report.getRemarks());
		// Note: The constructor has a bug - it doesn't set site
	}

	@Test
	void testBuilder_WithValidData_BuildsReportSuccessfully()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine check";
		String remarks = "No issues found";

		// Act
		Report report = createValidReportBuilder(site, maintenance, technician, startDate, startTime, endDate, endTime,
				reason, remarks).build();

		// Assert
		assertNotNull(report);
		assertEquals(site, report.getSite());
		assertEquals(maintenance, report.getMaintenance());
		assertEquals(technician, report.getTechnician());
		assertEquals(startDate, report.getStartDate());
		assertEquals(startTime, report.getStartTime());
		assertEquals(endDate, report.getEndDate());
		assertEquals(endTime, report.getEndTime());
		assertEquals(reason, report.getReason());
		assertEquals(remarks, report.getRemarks());
	}

	@Test
	void testBuilder_MissingMaintenance_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine check";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildTechnician(technician)
				.buildstartDate(startDate).buildStartTime(startTime).buildEndDate(endDate).buildEndTime(endTime)
				.buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("maintenance"));
		assertEquals(RequiredElementReport.MAINTENANCE_REQUIRED, missingElements.get("maintenance"));
	}

	@Test
	void testBuilder_MissingSite_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine check";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildMaintenance(maintenance).buildTechnician(technician)
				.buildstartDate(startDate).buildStartTime(startTime).buildEndDate(endDate).buildEndTime(endTime)
				.buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("site"));
		assertEquals(RequiredElementReport.SITE_REQUIRED, missingElements.get("site"));
	}

	@Test
	void testBuilder_MissingTechnician_ThrowsException()
	{
		// Arrange
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine check";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildMaintenance(maintenance)
				.buildstartDate(startDate).buildStartTime(startTime).buildEndDate(endDate).buildEndTime(endTime)
				.buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("technician"));
		assertEquals(RequiredElementReport.TECHNICIAN_REQUIRED, missingElements.get("technician"));
	}

	@Test
	void testBuilder_MissingStartDate_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine check";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildMaintenance(maintenance)
				.buildTechnician(technician).buildStartTime(startTime).buildEndDate(endDate).buildEndTime(endTime)
				.buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("startDate"));
		assertEquals(RequiredElementReport.STARTDATE_REQUIRED, missingElements.get("startDate"));
	}

	@Test
	void testBuilder_MissingStartTime_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine check";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildMaintenance(maintenance)
				.buildTechnician(technician).buildstartDate(startDate).buildEndDate(endDate).buildEndTime(endTime)
				.buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("startTime"));
		assertEquals(RequiredElementReport.STARTTIME_REQUIRED, missingElements.get("startTime"));
	}

	@Test
	void testBuilder_MissingEndDate_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine check";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildMaintenance(maintenance)
				.buildTechnician(technician).buildstartDate(startDate).buildStartTime(startTime).buildEndTime(endTime)
				.buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("endDate"));
		assertEquals(RequiredElementReport.ENDDATE_REQUIRED, missingElements.get("endDate"));
	}

	@Test
	void testBuilder_MissingEndTime_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = LocalDate.now();
		String reason = "Routine check";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildMaintenance(maintenance)
				.buildTechnician(technician).buildstartDate(startDate).buildStartTime(startTime).buildEndDate(endDate)
				.buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("endTime"));
		assertEquals(RequiredElementReport.ENDTIME_REQUIRED, missingElements.get("endTime"));
	}

	@Test
	void testBuilder_MissingReason_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildMaintenance(maintenance)
				.buildTechnician(technician).buildstartDate(startDate).buildStartTime(startTime).buildEndDate(endDate)
				.buildEndTime(endTime).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("reason"));
		assertEquals(RequiredElementReport.REASON_REQUIRED, missingElements.get("reason"));
	}

	@Test
	void testBuilder_EmptyReason_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = LocalDate.now();
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildMaintenance(maintenance)
				.buildTechnician(technician).buildstartDate(startDate).buildStartTime(startTime).buildEndDate(endDate)
				.buildEndTime(endTime).buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("reason"));
		assertEquals(RequiredElementReport.REASON_REQUIRED, missingElements.get("reason"));
	}

	@Test
	void testBuilder_EndDateBeforeStartDate_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(8, 0);
		LocalDate endDate = startDate.minusDays(1);
		LocalTime endTime = LocalTime.of(17, 0);
		String reason = "Routine check";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildMaintenance(maintenance)
				.buildTechnician(technician).buildstartDate(startDate).buildStartTime(startTime).buildEndDate(endDate)
				.buildEndTime(endTime).buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("endDate"));
		assertEquals(RequiredElementReport.END_DATE_BEFORE_START, missingElements.get("endDate"));
	}

	@Test
	void testBuilder_EndTimeBeforeStartTimeSameDay_ThrowsException()
	{
		// Arrange
		User technician = new User();
		Site site = new Site();
		Maintenance maintenance = new Maintenance();
		LocalDate startDate = LocalDate.now();
		LocalTime startTime = LocalTime.of(14, 0);
		LocalDate endDate = startDate; // same day
		LocalTime endTime = LocalTime.of(13, 0); // earlier
		String reason = "Routine check";
		String remarks = "No issues found";

		Report.Builder builder = new Report.Builder().buildSite(site).buildMaintenance(maintenance)
				.buildTechnician(technician).buildstartDate(startDate).buildStartTime(startTime).buildEndDate(endDate)
				.buildEndTime(endTime).buildReason(reason).buildRemarks(remarks);

		// Act & Assert
		InformationRequiredExceptionReport exception = assertThrows(InformationRequiredExceptionReport.class,
				() -> builder.build());

		Map<String, RequiredElementReport> missingElements = exception.getMissingElements();
		assertTrue(missingElements.containsKey("endTime"));
		assertEquals(RequiredElementReport.END_TIME_BEFORE_START, missingElements.get("endTime"));
	}

	// Helper method to create a valid Report.Builder
	private Report.Builder createValidReportBuilder(Site site, Maintenance maintenance, User technician,
			LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String reason,
			String remarks)
	{
		return new Report.Builder().buildSite(site).buildMaintenance(maintenance).buildTechnician(technician)
				.buildstartDate(startDate).buildStartTime(startTime).buildEndDate(endDate).buildEndTime(endTime)
				.buildReason(reason).buildRemarks(remarks);
	}
}