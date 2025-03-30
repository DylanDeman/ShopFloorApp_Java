package domain;

import java.io.Serializable;
import java.util.List;

import exceptions.InvalidAddressException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String street;
	private int number;
	private int postalcode;
	private String city;

	@Setter
	@OneToMany(mappedBy = "address")
	private List<User> users;

	public Address(String street, int number, int postalcode, String city)
	{
		setStreet(street);
		setNumber(number);
		setPostalcode(postalcode);
		setCity(city);
	}

	public void setStreet(String street)
	{
		if (street == null || street.isBlank())
		{
			throw new InvalidAddressException("Street cannot be null or empty");
		}
		this.street = street.trim();
	}

	public void setNumber(int number)
	{
		if (number <= 0)
		{
			throw new InvalidAddressException("House number must be positive");
		}
		this.number = number;
	}

	public void setPostalcode(int postalcode)
	{
		if (postalcode <= 0)
		{
			throw new InvalidAddressException("Postal code must be positive");
		}
		if (postalcode < 1000 || postalcode > 9999)
		{
			throw new InvalidAddressException("Postal code must be between 1000 and 9999");
		}
		this.postalcode = postalcode;
	}

	public void setCity(String city)
	{
		if (city == null || city.isBlank())
		{
			throw new InvalidAddressException("City cannot be null or empty");
		}
		this.city = city.trim();
	}

	@Override
	public String toString()
	{
		return "%s, %d, %d, %s".formatted(street != null ? street : "N/A", number, postalcode,
				city != null ? city : "N/A");
	}
}