package machine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.machine.Machine;
import domain.site.Site;
import domain.user.User;
import exceptions.InvalidMachineException;

@ExtendWith(MockitoExtension.class)
class MachineTest {

	
	@Mock
	private User mockTechnician;
	
	@Mock
	private Site site;
	
	private Machine machine;
	
	
	@Test
	void constructor_validParameters_createMachine() {
	    LocalDateTime lastMaintenance = LocalDateTime.now(); 
	    LocalDateTime futureMaintenance = LocalDateTime.now().plusDays(30);
	    
	    assertDoesNotThrow(() -> {
	        machine = Machine.builder()
	                .site(site)
	                .technician(mockTechnician)
	                .code("testCode")
	                .status("actief")
	                .productieStatus("actief")
	                .location("gent")
	                .productInfo("testProduct")
	                .lastMaintenance(lastMaintenance)
	                .futureMaintenance(futureMaintenance)
	                .build();
	    });
	    
	    assertNotNull(machine);
	    assertEquals("testCode", machine.getCode());
	    assertEquals("actief", machine.getStatus());
	    assertEquals("gent", machine.getLocation());
	    assertEquals("testProduct", machine.getProductInfo());
	    assertEquals(lastMaintenance, machine.getLastMaintenance());
	    assertEquals(futureMaintenance, machine.getFutureMaintenance());
	}
	
	@Test
	void builder_missingSite_throwsInvalidMachineException() {
	    assertThrows(InvalidMachineException.class, () -> {
	        validMachineBuilder()
	            .site(null)
	            .build();
	    });
	}

	@Test
	void builder_missingTechnician_throwsInvalidMachineException() {
	    assertThrows(InvalidMachineException.class, () -> {
	        validMachineBuilder()
	            .technician(null)
	            .build();
	    });
	}

	@Test
	void builder_missingCode_throwsInvalidMachineException() {
	    assertThrows(InvalidMachineException.class, () -> {
	        validMachineBuilder()
	            .code(null)
	            .build();
	    });
	}
	
	@Test
	void builder_missingStatus_throwsInvalidMachineException() {
	    assertThrows(InvalidMachineException.class, () -> {
	        validMachineBuilder()
	            .status(null)
	            .build();
	    });
	}
	
	@Test
	void builder_missingProductieStatus_throwsInvalidMachineException() {
	    assertThrows(InvalidMachineException.class, () -> {
	        validMachineBuilder()
	            .productieStatus(null)
	            .build();
	    });
	}
	
	@Test
	void builder_missingLocation_throwsInvalidMachineException() {
	    assertThrows(InvalidMachineException.class, () -> {
	        validMachineBuilder()
	            .location(null)
	            .build();
	    });
	}
	
	@Test
	void builder_missingProductInfo_throwsInvalidMachineException() {
	    assertThrows(InvalidMachineException.class, () -> {
	        validMachineBuilder()
	            .productInfo(null)
	            .build();
	    });
	}
	
	@Test
	void builder_missingFutureMaintenance_throwsInvalidMachineException() {
	    assertThrows(InvalidMachineException.class, () -> {
	        validMachineBuilder()
	            .futureMaintenance(null)
	            .build();
	    });
	}


	private Machine.Builder validMachineBuilder() {
	    return Machine.builder()
	        .site(mock(Site.class))
	        .technician(mock(User.class))
	        .code("testCode")
	        .status("actief")
	        .productieStatus("actief")
	        .location("Gent")
	        .productInfo("testInfo")
	        .futureMaintenance(LocalDateTime.now().plusDays(10));
	}

}
