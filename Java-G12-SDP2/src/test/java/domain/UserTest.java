package domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import exceptions.InvalidUserException;
import util.Role;
import util.Status;

class UserTest
{
	private static final String VALID_FIRST_NAME = "Robert";
	private static final String VALID_LAST_NAME = "Devree";
	private static final String VALID_EMAIL = "robert.devree@example.com";
	private static final String VALID_PHONE = "1234567890";
	private static final String VALID_PASSWORD = "superveiligwachtwoord12345";
	private static final LocalDate VALID_BIRTHDATE = LocalDate.now().minusYears(20);
	private static final Address VALID_ADDRESS = new Address("Stationstraat", 10, 9000, "Gent");

	@Test
	void constructor_validParameters_createsUser()
	{
		User user = new User(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,
				VALID_BIRTHDATE, VALID_ADDRESS, Status.ACTIEF, Role.TECHNIEKER);

		assertAll(() -> assertEquals(VALID_FIRST_NAME, user.getFirstName()),
				() -> assertEquals(VALID_LAST_NAME, user.getLastName()),
				() -> assertEquals(VALID_EMAIL.toLowerCase(), user.getEmail()),
				() -> assertEquals(VALID_PHONE, user.getPhoneNumber()),
				() -> assertEquals(VALID_PASSWORD, user.getPassword()),
				() -> assertEquals(VALID_BIRTHDATE, user.getBirthdate()),
				() -> assertEquals(VALID_ADDRESS, user.getAddress()),
				() -> assertEquals(Status.ACTIEF, user.getStatus()),
				() -> assertEquals(Role.TECHNIEKER, user.getRole()));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void setFirstName_invalidValues_throwsException(String invalidName)
	{
		User user = new User();
		assertThrows(InvalidUserException.class, () -> user.setFirstName(invalidName));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void setEmail_invalidValues_throwsException(String invalidEmail)
	{
		User user = new User();
		assertThrows(InvalidUserException.class, () -> user.setEmail(invalidEmail));
	}

	@ParameterizedTest
	@ValueSource(strings = { "invalid", "missing@dot", "@domain.com", "noat.com" })
	void setEmail_invalidFormat_throwsException(String invalidEmail)
	{
		User user = new User();
		assertThrows(InvalidUserException.class, () -> user.setEmail(invalidEmail));
	}

	@Test
	void setPassword_tooShort_throwsException()
	{
		User user = new User();
		assertThrows(InvalidUserException.class, () -> user.setPassword("short"));
	}

	@Test
	void setBirthdate_tooYoung_throwsException()
	{
		User user = new User();
		LocalDate tooYoung = LocalDate.now().minusYears(15);
		assertThrows(InvalidUserException.class, () -> user.setBirthdate(tooYoung));
	}

	@Test
	void setBirthdate_futureDate_throwsException()
	{
		User user = new User();
		LocalDate futureDate = LocalDate.now().plusDays(1);
		assertThrows(InvalidUserException.class, () -> user.setBirthdate(futureDate));
	}

	@Test
	void getAge_calculatesCorrectAge()
	{
		User user = new User();
		LocalDate birthdate = LocalDate.now().minusYears(25).minusMonths(6);
		user.setBirthdate(birthdate);
		assertEquals(25, user.getAge());
	}

	@Test
	void getFullName_returnsCorrectFormat()
	{
		User user = new User();
		user.setFirstName("Robert");
		user.setLastName("Devree");
		assertEquals("Robert Devree", user.getFullName());
	}

}