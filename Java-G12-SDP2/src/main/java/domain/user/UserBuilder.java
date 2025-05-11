package domain.user;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import domain.Address;
import exceptions.InformationRequiredException;
import util.PasswordHasher;
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
		// TODO validatie nog toevoegen
		user.setFirstName(firstName);
		user.setLastName(lastName);
	}

	public void buildContactInfo(String email, String phoneNumber)
	{
		// TODO validatie nog toevoegen
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
	}

	public void buildBirthdate(LocalDate birthdate)
	{
		// TODO validatie nog toevoegen
		user.setBirthdate(birthdate);
	}

	public void buildRoleAndStatus(Role role, Status status)
	{
		// TODO validatie nog toevoegen
		user.setRole(role);
		user.setStatus(status);
	}

	public void createAddress()
	{
		address = new Address();
	}

	public void buildStreet(String street)
	{
		// TODO validatie nog toevoegen
		address.setStreet(street);
	}

	public void buildNumber(int number)
	{
		// TODO validatie nog toevoegen
		address.setNumber(number);
	}

	public void buildPostalcode(int postalcode)
	{
		// TODO validatie nog toevoegen
		address.setPostalcode(postalcode);
	}

	public void buildCity(String city)
	{
		// TODO validatie nog toevoegen
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

		String password = generatePassword();

		System.out.print(password);

		user.setPassword(PasswordHasher.hash(password));

		return this.user;
	}

	public static String generatePassword()
	{
		SecureRandom random = new SecureRandom();
		String lower = "abcdefghijklmnopqrstuvwxyz";
		String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String digits = "0123456789";
		String special = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?";
		String allChars = lower + upper + digits + special;

		int length = 10 + random.nextInt(11);

		ArrayList<Character> password = new ArrayList<>();

		password.add(lower.charAt(random.nextInt(lower.length())));
		password.add(upper.charAt(random.nextInt(upper.length())));
		password.add(digits.charAt(random.nextInt(digits.length())));
		password.add(special.charAt(random.nextInt(special.length())));

		for (int i = 4; i < length; i++)
		{
			password.add(allChars.charAt(random.nextInt(allChars.length())));
		}

		Collections.shuffle(password, random);

		StringBuilder sb = new StringBuilder();
		for (char c : password)
		{
			sb.append(c);
		}

		return sb.toString();
	}
}
