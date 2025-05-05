package machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.machine.Machine;
import domain.site.Site;
import domain.user.User;
import util.MachineStatus;
import util.ProductionStatus;

@ExtendWith(MockitoExtension.class)
class MachineTest
{
	@Mock
	private User mockTechnician;

	@Mock
	private Site site;

	private Machine machine;

	@Test
	void constructor_validParameters_createMachine()
	{
		LocalDate futureMaintenance = LocalDate.now().plusDays(30);

		machine = new Machine(site, mockTechnician, "testCode", "gent", "testProduct", 
				MachineStatus.DRAAIT, ProductionStatus.GEZOND, futureMaintenance);

		assertNotNull(machine);
		assertEquals("testCode", machine.getCode());
		assertEquals(MachineStatus.DRAAIT, machine.getMachineStatus());
		assertEquals("gent", machine.getLocation());
		assertEquals("testProduct", machine.getProductInfo());
		assertEquals(futureMaintenance, machine.getFutureMaintenance());
		assertEquals(ProductionStatus.GEZOND, machine.getProductionStatus());
	}

	@Test
	void constructor_nullSite_throwsNullPointerException()
	{
		assertThrows(NullPointerException.class, () ->
		{
			new Machine(null, mockTechnician, "testCode", "gent", "testProduct", 
					MachineStatus.DRAAIT, ProductionStatus.GEZOND, LocalDate.now().plusDays(10));
		});
	}

	@Test
	void constructor_nullTechnician_throwsNullPointerException()
	{
		assertThrows(NullPointerException.class, () ->
		{
			new Machine(site, null, "testCode", "gent", "testProduct", 
					MachineStatus.DRAAIT, ProductionStatus.GEZOND, LocalDate.now().plusDays(10));
		});
	}

	@Test
	void constructor_nullCode_throwsNullPointerException()
	{
		assertThrows(NullPointerException.class, () ->
		{
			new Machine(site, mockTechnician, null, "gent", "testProduct", 
					MachineStatus.DRAAIT, ProductionStatus.GEZOND, LocalDate.now().plusDays(10));
		});
	}

	@Test
	void constructor_nullLocation_throwsNullPointerException()
	{
		assertThrows(NullPointerException.class, () ->
		{
			new Machine(site, mockTechnician, "testCode", null, "testProduct", 
					MachineStatus.DRAAIT, ProductionStatus.GEZOND, LocalDate.now().plusDays(10));
		});
	}

	@Test
	void constructor_nullProductInfo_throwsNullPointerException()
	{
		assertThrows(NullPointerException.class, () ->
		{
			new Machine(site, mockTechnician, "testCode", "gent", null, 
					MachineStatus.DRAAIT, ProductionStatus.GEZOND, LocalDate.now().plusDays(10));
		});
	}

	@Test
	void constructor_nullMachineStatus_throwsNullPointerException()
	{
		assertThrows(NullPointerException.class, () ->
		{
			new Machine(site, mockTechnician, "testCode", "gent", "testProduct", 
					null, ProductionStatus.GEZOND, LocalDate.now().plusDays(10));
		});
	}

	@Test
	void constructor_nullProductionStatus_throwsNullPointerException()
	{
		assertThrows(NullPointerException.class, () ->
		{
			new Machine(site, mockTechnician, "testCode", "gent", "testProduct", 
					MachineStatus.DRAAIT, null, LocalDate.now().plusDays(10));
		});
	}

	@Test
	void constructor_nullFutureMaintenance_throwsNullPointerException()
	{
		assertThrows(NullPointerException.class, () ->
		{
			new Machine(site, mockTechnician, "testCode", "gent", "testProduct", 
					MachineStatus.DRAAIT, ProductionStatus.GEZOND, null);
		});
	}
}
