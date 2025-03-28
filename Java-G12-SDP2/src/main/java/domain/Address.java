package domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String street;
	private int number;
	private int postalcode;
	private String city;

	@OneToMany(mappedBy = "address")
	private List<User> users;

	@Override
	public String toString()
	{
		return "%s, %d, %d, %s".formatted(street != null ? street : "N/A", number, postalcode,
				city != null ? city : "N/A");
	}

	public Address(String street, int number, int postalcode, String city)
	{
		super();
		this.street = street;
		this.number = number;
		this.postalcode = postalcode;
		this.city = city;
	}

}
