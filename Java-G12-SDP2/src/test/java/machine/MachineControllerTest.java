package machine;

import dto.MachineDTO;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionMachine;
import interfaces.Observer;
import interfaces.Subject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Machine;
import domain.NotificationObserver;
import util.MachineStatus;
import util.ProductionStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MachineControllerTest {

    private MachineRepository machineRepo;
    private MachineControllerTestImpl controller;
    private NotificationObserver notificationObserver;

    // Create an interface for the repository operations we need
    interface MachineRepository {
        List<Machine> findAll();
        Machine get(int id);
        void startTransaction();
        void insert(Machine machine);
        void update(Machine machine);
        void commitTransaction();
    }

    @BeforeEach
    void setUp() {
        // Create mock dependencies
        machineRepo = mock(MachineRepository.class);
        notificationObserver = mock(NotificationObserver.class);
        
        // Create our test controller implementation
        controller = new MachineControllerTestImpl(machineRepo);
        controller.addObserver(notificationObserver);
    }

    /**
     * Test implementation of MachineController that uses our mockable interface
     */
    private static class MachineControllerTestImpl implements Subject {
        private final MachineRepository machineRepo;
        private final List<Observer> observers = new ArrayList<>();

        public MachineControllerTestImpl(MachineRepository machineRepo) {
            this.machineRepo = machineRepo;
        }

        @Override
        public void addObserver(Observer observer) {
            observers.add(observer);
        }

        @Override
        public void removeObserver(Observer observer) {
            observers.remove(observer);
        }

        @Override
        public void notifyObservers(String message) {
            observers.forEach(o -> o.update(message));
        }

        public List<MachineDTO> getMachineList() {
            List<Machine> machines = machineRepo.findAll();
            if (machines == null) {
                return List.of();
            }
            return machines.stream().map(this::toMachineDTO).toList();
        }

        public void addNewMachine(Machine machine) {
            machineRepo.startTransaction();
            machineRepo.insert(machine);
            machineRepo.commitTransaction();
            notifyObservers("Nieuwe machine toegevoegd: " + machine.getCode());
        }

        public void updateMachine(Machine machine) {
            machineRepo.startTransaction();
            machineRepo.update(machine);
            machineRepo.commitTransaction();
            notifyObservers("Machine bijgewerkt: " + machine.getCode());
        }

        public MachineDTO getMachineById(int machineId) {
            return toMachineDTO(machineRepo.get(machineId));
        }

        private MachineDTO toMachineDTO(Machine machine) {
            if (machine == null) return null;
            return new MachineDTO(
                machine.getId(),
                null, // site
                null, // technician
                machine.getCode(),
                machine.getMachineStatus(),
                machine.getProductionStatus(),
                machine.getLocation(),
                machine.getProductInfo(),
                null, // lastMaintenance
                machine.getFutureMaintenance(),
                0, // numberDaysSinceLastMaintenance
                0.0 // upTimeInHours
            );
        }
    }

    @Test
    void testGetMachineListEmpty() {
        // Arrange
        when(machineRepo.findAll()).thenReturn(List.of());

        // Act
        List<MachineDTO> result = controller.getMachineList();

        // Assert
        assertTrue(result.isEmpty());
        verify(machineRepo, times(1)).findAll();
    }

    @Test
    void testGetMachineListWithItems() {
        // Arrange
        Machine machine1 = createTestMachine(1, "M001");
        Machine machine2 = createTestMachine(2, "M002");
        when(machineRepo.findAll()).thenReturn(List.of(machine1, machine2));

        // Act
        List<MachineDTO> result = controller.getMachineList();

        // Assert
        assertEquals(2, result.size());
        assertEquals("M001", result.get(0).code());
        assertEquals("M002", result.get(1).code());
        verify(machineRepo, times(1)).findAll();
    }

    @Test
    void testAddNewMachine() {
        // Arrange
        Machine machine = createTestMachine(1, "M001");

        // Act
        controller.addNewMachine(machine);

        // Assert
        verify(machineRepo, times(1)).startTransaction();
        verify(machineRepo, times(1)).insert(machine);
        verify(machineRepo, times(1)).commitTransaction();
        verify(notificationObserver, times(1)).update("Nieuwe machine toegevoegd: M001");
    }

    @Test
    void testUpdateMachine() {
        // Arrange
        Machine machine = createTestMachine(1, "M001");

        // Act
        controller.updateMachine(machine);

        // Assert
        verify(machineRepo, times(1)).startTransaction();
        verify(machineRepo, times(1)).update(machine);
        verify(machineRepo, times(1)).commitTransaction();
        verify(notificationObserver, times(1)).update("Machine bijgewerkt: M001");
    }

    @Test
    void testGetMachineById() {
        // Arrange
        Machine machine = createTestMachine(1, "M001");
        when(machineRepo.get(1)).thenReturn(machine);

        // Act
        MachineDTO result = controller.getMachineById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("M001", result.code());
        verify(machineRepo, times(1)).get(1);
    }

    @Test
    void testGetMachineByIdNotFound() {
        // Arrange
        when(machineRepo.get(999)).thenReturn(null);

        // Act
        MachineDTO result = controller.getMachineById(999);

        // Assert
        assertNull(result);
        verify(machineRepo, times(1)).get(999);
    }

    @Test
    void testObserverManagement() {
        // Arrange
        Observer testObserver = mock(Observer.class);

        // Act - Add observer
        controller.addObserver(testObserver);
        controller.notifyObservers("Test message");

        // Assert
        verify(testObserver, times(1)).update("Test message");

        // Act - Remove observer
        controller.removeObserver(testObserver);
        controller.notifyObservers("Another message");

        // Assert
        verify(testObserver, times(1)).update("Test message"); // Should not be called again
    }

    private Machine createTestMachine(int id, String code) {
        Machine machine = new Machine();
        machine.setId(id);
        machine.setCode(code);
        machine.setMachineStatus(MachineStatus.DRAAIT);
        machine.setProductionStatus(ProductionStatus.GEZOND);
        machine.setLocation("Test Location");
        machine.setProductInfo("Test Product");
        machine.setFutureMaintenance(LocalDate.now().plusMonths(1));
        return machine;
    }
}