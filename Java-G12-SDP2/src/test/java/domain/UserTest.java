package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.user.User;
import exceptions.InformationRequiredException;
import interfaces.Observer;
import util.Role;
import util.Status;

class UserTest
{

	private User user;

	@Mock
	private Observer observer;

	@Mock
	private Address address;

	@BeforeEach
	void setUp()
	{
		MockitoAnnotations.openMocks(this);
		user = new User();
		user.addObserver(observer);

		when(address.getStreet()).thenReturn("Main Street");
		when(address.getNumber()).thenReturn(123);
		when(address.getPostalcode()).thenReturn(1000);
		when(address.getCity()).thenReturn("Metropolis");
	}

	@Test
	void testGetAge()
	{
		LocalDate birthdate = LocalDate.now().minusYears(25);
		user.setBirthdate(birthdate);

		assertEquals(25, user.getAge());
	}

	@Test
	void testGetFullName()
	{
		user.setFirstName("John");
		user.setLastName("Doe");

		assertEquals("John Doe", user.getFullName());
	}

	@Test
	void testObserverPattern()
	{
		String message = "Test message";
		user.notifyObservers(message);

		verify(observer).update(message);
	}

	@Test
	void testBuilderWithAllRequiredFields() throws InformationRequiredException
	{
		User builtUser = new User.Builder().withFirstName("John").withLastName("Doe").withEmail("john.doe@example.com")
				.withPhoneNumber("1234567890").withBirthdate(LocalDate.of(1990, 1, 1)).withAddress(address)
				.withRole(Role.VERANTWOORDELIJKE).withStatus(Status.ACTIEF).build();

		assertNotNull(builtUser);
		assertEquals("John", builtUser.getFirstName());
	}

	@Test
	void testToString()
	{
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("john@example.com");
		user.setPhoneNumber("123456789");
		user.setBirthdate(LocalDate.of(1990, 1, 1));
		user.setAddress(address);
		user.setStatus(Status.ACTIEF);
		user.setRole(Role.VERANTWOORDELIJKE);

		String result = user.toString();

		assertTrue(result.contains("John Doe"));
		assertTrue(result.contains("john@example.com"));
		assertTrue(result.contains("VERANTWOORDELIJKE"));
	}

	@Test
	void testConstructorWithAllParameters()
	{
		User user = new User("Jane", "Doe", "jane@example.com", "987654321", "hashedPassword",
				LocalDate.of(1985, 5, 15), address, Status.ACTIEF, Role.TECHNIEKER);

		assertEquals("Jane", user.getFirstName());
		assertEquals("Doe", user.getLastName());
		assertEquals("jane@example.com", user.getEmail());
		assertEquals("987654321", user.getPhoneNumber());
		assertEquals("hashedPassword", user.getPassword());
		assertEquals(LocalDate.of(1985, 5, 15), user.getBirthdate());
		assertEquals(address, user.getAddress());
		assertEquals(Status.ACTIEF, user.getStatus());
		assertEquals(Role.TECHNIEKER, user.getRole());
	}
}