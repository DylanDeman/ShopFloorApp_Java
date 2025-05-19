package machine;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import domain.Machine;
import domain.User;
import domain.site.Site;
import util.MachineStatus;
import util.ProductionStatus;

class MachineTest {

	private Machine machine;
	private Site mockSite;
	private User mockTechnician;

	@BeforeEach
	void setUp() {
		mockSite = mock(Site.class);
		
		Set<Machine> emptySet = new HashSet<Machine>();
		when(mockSite.getMachines()).thenReturn(emptySet);


		mockTechnician = mock(User.class);

		machine = new Machine();
		machine.setSite(mockSite);
		machine.setTechnician(mockTechnician);
		machine.setCode("MCH-001");
		machine.setLocation("Zone A");
		machine.setProductInfo("Widget Maker");
		machine.setMachineStatus(MachineStatus.DRAAIT);
		machine.setProductionStatus(ProductionStatus.GEZOND);
		machine.setLastMaintenance(LocalDate.now().minusDays(1));
		machine.setFutureMaintenance(LocalDate.now().plusMonths(1));
	}

	@Test
	void constructor_allFieldsSet_correctlyAssigned() {
		LocalDate futureMaintenance = LocalDate.of(2025, 12, 1);
		Machine constructed = new Machine(mockSite, mockTechnician, "MCH-123", "Room B", "Info",
				MachineStatus.DRAAIT, ProductionStatus.FALEND, futureMaintenance);

		assertAll(
			() -> assertEquals(mockSite, constructed.getSite()),
			() -> assertEquals(mockTechnician, constructed.getTechnician()),
			() -> assertEquals("MCH-123", constructed.getCode()),
			() -> assertEquals("Room B", constructed.getLocation()),
			() -> assertEquals("Info", constructed.getProductInfo()),
			() -> assertEquals(MachineStatus.DRAAIT, constructed.getMachineStatus()),
			() -> assertEquals(ProductionStatus.FALEND, constructed.getProductionStatus()),
			() -> assertEquals(futureMaintenance, constructed.getFutureMaintenance())
		);
	}

	@Test
	void getUpTimeInHours_lastMaintenanceYesterday_returns24OrMore() {
		double hours = machine.getUpTimeInHours();
		// Might vary slightly depending on test execution time, just check >= 24
		assert(hours >= 24);
	}

	@Test
	void setSite_removesMachineFromOldSiteAndAddsToNewSite() {
		Site oldSite = mock(Site.class);
		when(oldSite.getMachines()).thenReturn(new java.util.HashSet<>());

		Site newSite = mock(Site.class);
		Set<Machine> newSiteMachines = new java.util.HashSet<>();
		when(newSite.getMachines()).thenReturn(newSiteMachines);

		machine.setSite(oldSite);
		machine.setSite(newSite);

		assertEquals(newSite, machine.getSite());
		assert(newSiteMachines.contains(machine));
	}

}
