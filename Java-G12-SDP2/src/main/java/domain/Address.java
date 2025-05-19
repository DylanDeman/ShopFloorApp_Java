package domain;

import java.io.Serializable;
import java.util.List;

import domain.site.Site;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address implements Serializable {
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

	@OneToMany(mappedBy = "address")
	private List<Site> sites;

	public Address(String street, int number, int postalcode, String city) {
		setStreet(street);
		setNumber(number);
		setPostalcode(postalcode);
		setCity(city);
	}

	@Override
	public String toString() {
		return "%s, %d, %d, %s".formatted(street != null ? street : "N/A", number, postalcode,
				city != null ? city : "N/A");
	}
}