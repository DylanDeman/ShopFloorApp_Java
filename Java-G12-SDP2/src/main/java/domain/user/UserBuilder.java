package domain.user;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import domain.Address;
import exceptions.InformationRequiredException;
import util.RequiredElement;
import util.Role;
import util.Status;

public class UserBuilder
{
	private User user;
	private Address address;

	private Map<String, RequiredElement> requiredElements;

	public void createUser()
	{
		user = new User();
	}

	public void buildName(String firstName, String lastName)
	{
		// validatie nog toevoegen
		user.setFirstName(firstName);
		user.setLastName(lastName);
	}

	public void buildContactInfo(String email, String phoneNumber)
	{
		// validatie nog toevoegen
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
	}

	public void buildBirthdate(LocalDate birthdate)
	{
		// validatie nog toevoegen
		user.setBirthdate(birthdate);
	}

	public void buildRoleAndStatus(Role role, Status status)
	{
		// validatie nog toevoegen
		user.setRole(role);
		user.setStatus(status);
	}

	public void createAddress()
	{
		address = new Address();
	}

	public void buildStreet(String street)
	{
		// validatie nog toevoegen
		address.setStreet(street);
	}

	public void buildNumber(int number)
	{
		// validatie nog toevoegen
		address.setNumber(number);
	}

	public void buildPostalcode(int postalcode)
	{
		// validatie nog toevoegen
		address.setPostalcode(postalcode);
	}

	public void buildCity(String city)
	{
		// validatie nog toevoegen
		address.setCity(city);
	}

	public User getUser() throws InformationRequiredException
	{
		requiredElements = new HashMap<>();

		if (user.getFirstName().isEmpty())
		{
			requiredElements.put("firstName", RequiredElement.FIRST_NAME_REQUIRED);
		}

		if (user.getLastName().isEmpty())
		{
			requiredElements.put("lastName", RequiredElement.LAST_NAME_REQUIRED);
		}

		if (user.getEmail().isEmpty())
		{
			requiredElements.put("email", RequiredElement.EMAIL_REQUIRED);
		}

		if (user.getBirthdate() == null)
		{
			requiredElements.put("birthDate", RequiredElement.BIRTH_DATE_REQUIRED);
		}

		if (address.getStreet().isEmpty())
		{
			requiredElements.put("street", RequiredElement.STREET_REQUIRED);
		}

		if (address.getNumber() == 0)
		{
			requiredElements.put("number", RequiredElement.NUMBER_REQUIRED);
		}

		if (address.getPostalcode() == 0)
		{
			requiredElements.put("postalCode", RequiredElement.POSTAL_CODE_REQUIRED);
		}

		if (address.getCity().isEmpty())
		{
			requiredElements.put("city", RequiredElement.CITY_REQUIRED);
		}

		if (user.getRole() == null)
		{
			requiredElements.put("role", RequiredElement.ROLE_REQUIRED);
		}

		if (user.getStatus() == null)
		{
			requiredElements.put("status", RequiredElement.STATUS_REQUIRED);
		}

		if (!requiredElements.isEmpty())
			throw new InformationRequiredException(requiredElements);

		user.setAddress(address);

		return this.user;
	}
}
