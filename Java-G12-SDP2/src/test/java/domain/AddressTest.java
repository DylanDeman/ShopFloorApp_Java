package domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import exceptions.InvalidAddressException;

class AddressTest
{
	private static final String VALID_STREET = "Stationstraat";
	private static final String VALID_CITY = "Gent";
	private static final int VALID_NUMBER = 10;
	private static final int VALID_POSTALCODE = 9000;
	private static final String N_A = "N/A";

	@Test
	void constructor_validParameters_createsValidObject()
	{
		Address address = new Address(VALID_STREET, VALID_NUMBER, VALID_POSTALCODE, VALID_CITY);
		assertAll(() -> assertEquals(VALID_STREET, address.getStreet()),
				() -> assertEquals(VALID_NUMBER, address.getNumber()),
				() -> assertEquals(VALID_POSTALCODE, address.getPostalcode()),
				() -> assertEquals(VALID_CITY, address.getCity()));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1, 999, 10000 })
	void constructor_invalidPostalcode_throwsException(int invalidPostalcode)
	{
		assertThrows(InvalidAddressException.class, () -> new Address(VALID_STREET, 1, invalidPostalcode, VALID_CITY));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void setStreet_invalidValues_throwsException(String invalidStreet)
	{
		Address address = new Address();
		assertThrows(InvalidAddressException.class, () -> address.setStreet(invalidStreet));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1, -100 })
	void setNumber_invalidValues_throwsException(int invalidNumber)
	{
		Address address = new Address();
		assertThrows(InvalidAddressException.class, () -> address.setNumber(invalidNumber));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1, 346, 999, 10000, 12430 })
	void setPostalcode_invalidValues_throwsException(int invalidPostalcode)
	{
		Address address = new Address();
		assertThrows(InvalidAddressException.class, () -> address.setPostalcode(invalidPostalcode));
	}

	@ParameterizedTest
	@ValueSource(ints = { 1000, 2000, 9999 })
	void setPostalcode_validValues_setsValue(int validPostalcode)
	{
		Address address = new Address();
		assertDoesNotThrow(() -> address.setPostalcode(validPostalcode));
		assertEquals(validPostalcode, address.getPostalcode());
	}

	@ParameterizedTest
	@NullAndEmptySource
	void setCity_invalidValues_throwsException(String invalidCity)
	{
		Address address = new Address();
		assertThrows(InvalidAddressException.class, () -> address.setCity(invalidCity));
	}

	@Test
	void toString_completeAddress_returnsFormattedString()
	{
		Address address = new Address(VALID_STREET, VALID_NUMBER, VALID_POSTALCODE, VALID_CITY);
		assertEquals(String.format("%s, %d, %d, %s", VALID_STREET, VALID_NUMBER, VALID_POSTALCODE, VALID_CITY),
				address.toString());
	}

	@Test
	void toString_partialAddress_usesPlaceholders()
	{
		Address address = new Address();
		address.setNumber(VALID_NUMBER);
		assertEquals(String.format("%s, %d, 0, %s", N_A, VALID_NUMBER, N_A), address.toString());
	}
}