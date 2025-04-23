package domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.user.User;
import util.Role;
import util.Status;

@ExtendWith(MockitoExtension.class)
class UserTest
{

	private static final String VALID_FIRST_NAME = "Robert";
	private static final String VALID_LAST_NAME = "Devree";
	private static final String VALID_EMAIL = "robert.devree@example.com";
	private static final String VALID_PHONE = "1234567890";
	private static final String VALID_PASSWORD = "superveiligwachtwoord12345";
	private static final LocalDate CURRENT_DATE = LocalDate.now();
	private static final int AGE = 25;
	private static final LocalDate VALID_BIRTHDATE = CURRENT_DATE.minusYears(AGE);

	@Mock
	private Address mockAddress;

	private User user;

	@BeforeEach
	void setUp()
	{
		user = new User();
		user.setAddress(mockAddress);
	}

	@ParameterizedTest
	@CsvSource({ "27, 1998-01-01", "32, 1993-01-01", "20, 2005-01-01" })
	void getAge_correctValues_returnsAge(int expectedAge, LocalDate birthdate)
	{
		user.setBirthdate(birthdate);

		int actualAge = user.getAge();

		assertEquals(expectedAge, actualAge);
	}

	@ParameterizedTest
	@CsvSource({ "John, Doe, 'John Doe'", "Jane, Smith, 'Jane Smith'", "'', Doe, ' Doe'", "John, '', 'John '" })
	void getFullName_correctValues_returnsFullName(String firstName, String lastName, String expected)
	{
		user.setFirstName(firstName);
		user.setLastName(lastName);

		String fullName = user.getFullName();

		assertEquals(expected, fullName);
	}

	@Test
	void constructor_correctValues_makesUser()
	{
		User constructedUser = new User(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,
				VALID_BIRTHDATE, mockAddress, Status.ACTIEF, Role.ADMIN);

		assertAll(() -> assertEquals(VALID_FIRST_NAME, constructedUser.getFirstName()),
				() -> assertEquals(VALID_LAST_NAME, constructedUser.getLastName()),
				() -> assertEquals(VALID_EMAIL, constructedUser.getEmail()),
				() -> assertEquals(VALID_PHONE, constructedUser.getPhoneNumber()),
				() -> assertEquals(VALID_PASSWORD, constructedUser.getPassword()),
				() -> assertEquals(VALID_BIRTHDATE, constructedUser.getBirthdate()),
				() -> assertEquals(mockAddress, constructedUser.getAddress()),
				() -> assertEquals(Status.ACTIEF, constructedUser.getStatus()),
				() -> assertEquals(Role.ADMIN, constructedUser.getRole()));
	}

}