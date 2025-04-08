package domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import exceptions.InformationRequiredException;
import util.RequiredElement;
import util.Role;
import util.Status;

public class UserBuilder
{
	private User user;

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

	public void buildAddress(String street, int number, int postalCode, String city)
	{
		// validatie nog toevoegen
		user.setAddress(new Address(street, number, postalCode, city));
	}

	public void buildRoleAndStatus(Role role, Status status)
	{
		// validatie nog toevoegen
		user.setRole(role);
		user.setStatus(status);
	}

	public User getUser() throws InformationRequiredException
	{
		requiredElements = new HashMap<>();

		if (user.getFirstName() == null)
		{
			requiredElements.put("firstName", RequiredElement.FIRST_NAME_REQUIRED);
		}

		if (user.getLastName() == null)
		{
			requiredElements.put("lastName", RequiredElement.LAST_NAME_REQUIRED);
		}

		if (user.getEmail() == null)
		{
			requiredElements.put("email", RequiredElement.EMAIL_REQUIRED);
		}

		if (user.getBirthdate() == null)
		{
			requiredElements.put("firstName", RequiredElement.BIRTH_DATE_REQUIRED);
		}

		if (user.getAddress().getStreet() == null)
		{
			requiredElements.put("street", RequiredElement.STREET_REQUIRED);
		}

		if (user.getAddress().getNumber() == 0)
		{
			requiredElements.put("number", RequiredElement.NUMBER_REQUIRED);
		}

		if (user.getAddress().getPostalcode() == 0)
		{
			requiredElements.put("postalCode", RequiredElement.POSTAL_CODE_REQUIRED);
		}

		if (user.getAddress().getCity() == null)
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

		return this.user;
	}
}
