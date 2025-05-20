package domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an address entity used for users and sites.
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The unique identifier of the address (primary key).
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	/**
	 * The name of the street.
	 */
	private String street;

	/**
	 * The house number.
	 */
	private int number;

	/**
	 * The postal code.
	 */
	private int postalcode;

	/**
	 * The city name.
	 */
	private String city;

	/**
	 * The list of users associated with this address.
	 */
	@OneToMany(mappedBy = "address")
	private List<User> users;

	/**
	 * The list of sites associated with this address.
	 */
	@OneToMany(mappedBy = "address")
	private List<Site> sites;

	/**
	 * Constructs a new Address with the specified street, number, postal code, and
	 * city.
	 *
	 * @param street     the street name
	 * @param number     the house number
	 * @param postalcode the postal code
	 * @param city       the city name
	 */
	public Address(String street, int number, int postalcode, String city)
	{
		setStreet(street);
		setNumber(number);
		setPostalcode(postalcode);
		setCity(city);
	}

	/**
	 * Returns a string representation of the address.
	 *
	 * @return a formatted string of the address details
	 */
	@Override
	public String toString()
	{
		return "%s, %d, %d, %s".formatted(street != null ? street : "N/A", number, postalcode,
				city != null ? city : "N/A");
	}
}
