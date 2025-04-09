package domain;

import java.util.HashMap;
import java.util.Map;

import exceptions.InformationRequiredException;
import util.RequiredElement;

public class AddressBuilder
{
	private Address address;

	private Map<String, RequiredElement> requiredElements;

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

	public Address getAddress() throws InformationRequiredException
	{
		requiredElements = new HashMap<>();

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

		if (!requiredElements.isEmpty())
			throw new InformationRequiredException(requiredElements);

		return this.address;
	}
}
