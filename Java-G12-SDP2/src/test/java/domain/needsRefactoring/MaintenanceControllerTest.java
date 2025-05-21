package domain.needsRefactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.Machine;
import domain.MachineController;
import domain.Maintenance;
import domain.MaintenanceController;
import domain.User;
import domain.UserController;
import dto.MaintenanceDTO;
import exceptions.InformationRequiredExceptionMaintenance;
import gui.AppServices;
import repository.GenericDaoJpa;
import util.MaintenanceStatus;

@ExtendWith(MockitoExtension.class)
class MaintenanceControllerTest
{

	@Mock
	GenericDaoJpa<Maintenance> maintenanceRepo;

	@Mock
	AppServices appServices;

	@Mock
	domain.UserController userController;

	@Mock
	domain.MachineController machineController;

	MaintenanceController maintenanceController;

	MockedStatic<AppServices> appServicesStaticMock;

	@BeforeEach
	void setUp()
	{
		maintenanceController = new MaintenanceController(maintenanceRepo);

		appServicesStaticMock = Mockito.mockStatic(AppServices.class);
		appServicesStaticMock.when(AppServices::getInstance).thenReturn(appServices);

	}

	@AfterEach
	void tearDown()
	{
		appServicesStaticMock.close();
	}

	@Test
	void testGetMaintenances_ReturnsDTOList()
	{
		Maintenance maintenance1 = mock(Maintenance.class);
		Maintenance maintenance2 = mock(Maintenance.class);
		when(maintenanceRepo.findAll()).thenReturn(List.of(maintenance1, maintenance2));

		List<MaintenanceDTO> dtos = maintenanceController.getMaintenances();

		assertNotNull(dtos);
		assertEquals(2, dtos.size());
		verify(maintenanceRepo).findAll();
	}

	@Test
	void testCreateMaintenance_WithParams_CreatesAndReturnsDTO() throws Exception
	{
		when(appServices.getUserController()).thenReturn(userController);
		when(userController.getUserById(anyInt())).thenReturn(mock(User.class));

		when(appServices.getMachineController()).thenReturn(machineController);
		when(machineController.getMachineById(anyInt())).thenReturn(mock(Machine.class));
		when(userController.getUserById(anyInt())).thenReturn(mock(User.class));
		int technicianId = 42;
		int machineId = 99;

		User technician = new User();
		Machine machine = mock(Machine.class);
		when(userController.getUserById(technicianId)).thenReturn(technician);
		when(machineController.getMachineById(machineId)).thenReturn(machine);

		when(machine.getLastMaintenance()).thenReturn(null);

		doNothing().when(maintenanceRepo).startTransaction();
		doNothing().when(maintenanceRepo).insert(any(Maintenance.class));
		doNothing().when(maintenanceRepo).commitTransaction();
		doNothing().when(machineController).updateMachine(machine);

		LocalDate execDate = LocalDate.now();
		LocalDateTime startDate = LocalDateTime.now();
		LocalDateTime endDate = startDate.plusHours(1);
		MaintenanceStatus status = MaintenanceStatus.VOLTOOID;

		MaintenanceDTO dto = maintenanceController.createMaintenance(execDate, startDate, endDate, technicianId,
				"Routine check", "No comments", status, machineId);

		assertNotNull(dto);
		verify(maintenanceRepo).startTransaction();
		verify(maintenanceRepo).insert(any(Maintenance.class));
		verify(maintenanceRepo).commitTransaction();

		verify(machineController).updateMachine(machine);
	}

	@Test
	void testCreateMaintenance_WithParams_StatusNotVoltooid_DoesNotUpdateMachine() throws Exception
	{
		when(appServices.getUserController()).thenReturn(userController);
		when(userController.getUserById(anyInt())).thenReturn(mock(User.class));

		when(appServices.getMachineController()).thenReturn(machineController);
		when(machineController.getMachineById(anyInt())).thenReturn(mock(Machine.class));
		int technicianId = 1;
		int machineId = 2;

		User technician = new User();
		Machine machine = mock(Machine.class);
		when(userController.getUserById(technicianId)).thenReturn(technician);
		when(machineController.getMachineById(machineId)).thenReturn(machine);

		when(machine.getLastMaintenance()).thenReturn(LocalDate.now().minusDays(10));

		doNothing().when(maintenanceRepo).startTransaction();
		doNothing().when(maintenanceRepo).insert(any(Maintenance.class));
		doNothing().when(maintenanceRepo).commitTransaction();

		LocalDate execDate = LocalDate.now();
		LocalDateTime startDate = LocalDateTime.now();
		LocalDateTime endDate = startDate.plusHours(2);
		MaintenanceStatus status = MaintenanceStatus.IN_UITVOERING;

		MaintenanceDTO dto = maintenanceController.createMaintenance(execDate, startDate, endDate, technicianId,
				"Reason", "Comments", status, machineId);

		assertNotNull(dto);

		verify(machineController, never()).updateMachine(any());
	}

	@Test
	void testGetMaintenanceDTO_ReturnsDTOOrNull()
	{
		Maintenance maintenance = mock(Maintenance.class);
		when(maintenanceRepo.get(1)).thenReturn(maintenance);

		MaintenanceDTO dto = maintenanceController.getMaintenanceDTO(1);
		assertNotNull(dto);

		when(maintenanceRepo.get(2)).thenReturn(null);
		MaintenanceDTO dtoNull = maintenanceController.getMaintenanceDTO(2);
		assertNull(dtoNull);
	}

	@Test
	void testUpdateMaintenance_Success_StatusVoltooid_UpdatesMachine() throws InformationRequiredExceptionMaintenance
	{
		int maintenanceId = 1;
		LocalDate executionDate = LocalDate.of(2025, 5, 19);
		LocalDateTime startDate = LocalDateTime.now().minusHours(2);
		LocalDateTime endDate = LocalDateTime.now();
		int technicianId = 10;
		int machineId = 20;

		User mockUser = mock(User.class);
		Machine mockMachine = mock(Machine.class);
		Maintenance existingMaintenance = mock(Maintenance.class);

		when(maintenanceRepo.get(maintenanceId)).thenReturn(existingMaintenance);
		when(existingMaintenance.getId()).thenReturn(maintenanceId);
		when(appServices.getUserController()).thenReturn(userController);
		when(userController.getUserById(technicianId)).thenReturn(mockUser);
		when(appServices.getMachineController()).thenReturn(machineController);
		when(machineController.getMachineById(machineId)).thenReturn(mockMachine);

		when(mockMachine.getLastMaintenance()).thenReturn(executionDate.minusDays(1));

		MaintenanceDTO result = maintenanceController.updateMaintenance(maintenanceId, executionDate, startDate,
				endDate, technicianId, "reason", "comments", MaintenanceStatus.VOLTOOID, machineId);

		assertNotNull(result);
		verify(maintenanceRepo).get(maintenanceId);
		verify(userController).getUserById(technicianId);
		verify(machineController).getMachineById(machineId);
		verify(maintenanceRepo).startTransaction();
		verify(maintenanceRepo).update(any(Maintenance.class));
		verify(maintenanceRepo).commitTransaction();
		verify(mockMachine).setLastMaintenance(executionDate);
		verify(machineController).updateMachine(mockMachine);
	}

	@Test
	void testUpdateMaintenance_Success_StatusNotVoltooid_DoesNotUpdateMachine()
			throws InformationRequiredExceptionMaintenance
	{
		int maintenanceId = 1;
		LocalDate executionDate = LocalDate.of(2025, 5, 19);
		LocalDateTime startDate = LocalDateTime.now().minusHours(2);
		LocalDateTime endDate = LocalDateTime.now();
		int technicianId = 10;
		int machineId = 20;

		User mockUser = mock(User.class);
		Machine mockMachine = mock(Machine.class);
		Maintenance existingMaintenance = mock(Maintenance.class);

		when(maintenanceRepo.get(maintenanceId)).thenReturn(existingMaintenance);
		when(existingMaintenance.getId()).thenReturn(maintenanceId);
		when(appServices.getUserController()).thenReturn(userController);
		when(userController.getUserById(technicianId)).thenReturn(mockUser);
		when(appServices.getMachineController()).thenReturn(machineController);
		when(machineController.getMachineById(machineId)).thenReturn(mockMachine);

		MaintenanceDTO result = maintenanceController.updateMaintenance(maintenanceId, executionDate, startDate,
				endDate, technicianId, "reason", "comments", MaintenanceStatus.IN_UITVOERING, machineId);

		assertNotNull(result);
		verify(maintenanceRepo).update(any(Maintenance.class));
		verify(mockMachine, never()).setLastMaintenance(any());
		verify(machineController, never()).updateMachine(any());
	}

	@Test
	void testUpdateMaintenance_ThrowsException_WhenMaintenanceNotFound()
	{
		int maintenanceId = 999;
		when(maintenanceRepo.get(maintenanceId)).thenReturn(null);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			maintenanceController.updateMaintenance(maintenanceId, LocalDate.now(), LocalDateTime.now(),
					LocalDateTime.now().plusHours(1), 1, "reason", "comments", MaintenanceStatus.VOLTOOID, 1);
		});

		assertTrue(thrown.getMessage().contains("Maintenance with ID " + maintenanceId + " not found"));
		verifyNoMoreInteractions(userController, machineController, maintenanceRepo);
	}

}
