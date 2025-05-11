package domain.maintenance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.machine.Machine;
import domain.site.Site;
import domain.user.User;
import dto.MachineDTO;
import util.MaintenanceStatus;

@ExtendWith(MockitoExtension.class)
public class MaintenanceTest
{
	@Mock
	private MaintenanceDao maintenanceDao;
	
	@Mock
	private Machine machine;
	
	@Mock
	private User technician;
	
	@Mock
	private Site site;
	
	private MaintenanceController maintenanceController;
	private Maintenance maintenance;
	
	@BeforeEach
	void setUp()
	{
		maintenanceController = Mockito.spy(new MaintenanceController());
		Mockito.doReturn(maintenanceDao).when(maintenanceController).getMaintenanceDao();
		
		when(machine.getCode()).thenReturn("TEST-001");
		when(machine.getSite()).thenReturn(site);
		when(site.getSiteName()).thenReturn("Test Site");
		when(technician.getEmail()).thenReturn("test@example.com");
		when(technician.getFirstName()).thenReturn("Test");
		when(technician.getLastName()).thenReturn("User");
		
		LocalDateTime now = LocalDateTime.now();
		maintenance = new Maintenance(LocalDate.now(), now, now.plusDays(1).minusHours(2), 
				technician, "Test reason", "Test comments", MaintenanceStatus.IN_PROGRESS, machine);
	}
	
	@Test
	void getMaintenances_ShouldReturnListOfMaintenanceDTOs()
	{
		when(maintenanceDao.findAll()).thenReturn(List.of(maintenance));
		List<MaintenanceDTO> result = maintenanceController.getMaintenances();
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(maintenance.getId(), result.get(0).id());
	}
	
	@Test
	void getMaintenance_ShouldReturnMaintenance()
	{
		when(maintenanceDao.get(1)).thenReturn(maintenance);
		Maintenance result = maintenanceController.getMaintenance(1);
		assertNotNull(result);
		assertEquals(maintenance.getId(), result.getId());
	}
	
	@Test
	void convertToMachineDTO_ShouldConvertCorrectly()
	{
		MachineDTO result = maintenanceController.convertToMachineDTO(machine);
		assertNotNull(result);
		assertEquals("TEST-001", result.code());
		verify(machine).getCode();
		verify(machine).getSite();
	}
	
	@ParameterizedTest
	@CsvSource(
	{ "2025-05-01T10:00,2025-05-01T09:00", "2025-05-02T15:00,2025-05-01T15:00" })
	void setEndDate_wrongEndDates_throwsException(String start, String end)
	{
		Maintenance maintenance = new Maintenance(LocalDate.now(), LocalDateTime.parse(start),
				LocalDateTime.parse(end), technician, "Test reason", "Test comments", 
				MaintenanceStatus.IN_PROGRESS, machine);
		assertThrows(IllegalStateException.class, maintenance::validateDates);
	}
}
