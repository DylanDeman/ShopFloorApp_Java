package domain.maintenance;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.machine.Machine;
import domain.user.User;
import util.MaintenanceStatus;

@ExtendWith(MockitoExtension.class)
public class MaintenanceTest {
	
	private Maintenance maintenance;
	
	@Mock
	private Machine machine;
	
	@Mock
	private User technician;

	@BeforeEach
	void setUp() {
		maintenance = new Maintenance();
		maintenance.setStartDate(LocalDateTime.now());
		maintenance.setEndDate(LocalDateTime.now().plusDays(1).minusHours(2));
		maintenance.setMachine(machine);
		maintenance.setStatus(MaintenanceStatus.IN_PROGRESS);
		maintenance.setTechnician(technician);
	}
	
	@ParameterizedTest
    @CsvSource({
        "2025-05-01T10:00,2025-05-01T09:00", // endDate before startDate
        "2025-05-02T15:00,2025-05-01T15:00"  // endDate before startDate
    })
    void setEndDate_wrongEndDates_throwsException(String start, String end) {
        Maintenance maintenance = new Maintenance();
        maintenance.setStartDate(LocalDateTime.parse(start));
        maintenance.setEndDate(LocalDateTime.parse(end));

        assertThrows(IllegalStateException.class, maintenance::validateDates);
    }
	
}
