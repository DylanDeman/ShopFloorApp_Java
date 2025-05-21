package domain.needsRefactoring;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.Machine;
import domain.Site;
import domain.User;
import exceptions.InvalidInputException;
import util.Status;

@ExtendWith(MockitoExtension.class)
class SiteTest
{
	private static final String VALID_SITE_NAME = "Headquarters";
	private static final Status VALID_STATUS = Status.ACTIEF;

	@Mock
	private User mockUser;

	@Mock
	private Machine mockMachine;

	private Site site;

	@BeforeEach
	void setUp()
	{
		site = new Site();
	}

	@Test
	void constructor_validParameters_createsSite()
	{
		site = new Site(VALID_SITE_NAME, mockUser, VALID_STATUS);

		assertAll(() -> assertEquals(VALID_SITE_NAME, site.getSiteName()),
				() -> assertEquals(mockUser, site.getVerantwoordelijke()),
				() -> assertEquals(VALID_STATUS, site.getStatus()));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void setSiteName_invalidValues_throwsException(String invalidName)
	{
		assertThrows(InvalidInputException.class, () -> site.setSiteName(invalidName));
	}

	@Test
	void setSiteName_validName_setsNameAndTrims()
	{
		String nameWithSpaces = "  Site Name  ";
		site.setSiteName(nameWithSpaces);
		assertEquals("Site Name", site.getSiteName());
	}

	@Test
	void setVerantwoordelijke_null_throwsException()
	{
		assertThrows(InvalidInputException.class, () -> site.setVerantwoordelijke(null));
	}

	@Test
	void setVerantwoordelijke_validUser_setsUser()
	{
		site.setVerantwoordelijke(mockUser);
		assertEquals(mockUser, site.getVerantwoordelijke());
	}

	@Test
	void setStatus_null_throwsException()
	{
		assertThrows(InvalidInputException.class, () -> site.setStatus(null));
	}

	@Test
	void setStatus_validStatus_setsStatus()
	{
		site.setStatus(Status.INACTIEF);
		assertEquals(Status.INACTIEF, site.getStatus());
	}

	@Test
	void addMachine_validMachine_addsMachineToSet()
	{
		site = new Site(VALID_SITE_NAME, mockUser, VALID_STATUS);

		site.addMachine(mockMachine);

		assertTrue(site.getMachines().contains(mockMachine));
		verify(mockMachine).setSite(site);
	}

	@Test
	void getMachines_returnsUnmodifiableSet()
	{
		site = new Site(VALID_SITE_NAME, mockUser, VALID_STATUS);
		site.addMachine(mockMachine);

		Set<Machine> machines = site.getMachines();

		assertThrows(UnsupportedOperationException.class, () -> machines.add(mockMachine));
	}
}