package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.Role;
import util.Status;

// Dit is ons model: plaats voor observers toe te voegen:
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(name = "User.getAllWithAddress", query = "SELECT u FROM User u JOIN u.address a ORDER BY u.id") })
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final int MIN_AGE = 18;
	private static final int MIN_PASSWORD_LENGTH = 8;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String password;
	private LocalDate birthdate;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "address_id")
	private Address address;

	@Enumerated(EnumType.STRING)
	private Status status;

	@Enumerated(EnumType.STRING)
	private Role role;

	public User(String firstName, String lastName, String email, String phoneNumber, String password,
			LocalDate birthdate, Address address, Status status, Role role)
	{
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
		setPhoneNumber(phoneNumber);
		setPassword(password);
		setBirthdate(birthdate);
		setAddress(address);
		setStatus(status);
		setRole(role);
	}

	public int getAge()
	{
		return Period.between(birthdate, LocalDate.now()).getYears();
	}

	@Override
	public String toString()
	{
		return String.format("%s %s, %s, %s, %s, %s, %s, %s, %s", firstName != null ? firstName : "N/A",
				lastName != null ? lastName : "N/A", email != null ? email : "N/A",
				phoneNumber != null ? phoneNumber : "N/A", password != null ? password : "N/A",
				birthdate != null ? birthdate.toString() : "N/A", address != null ? address.toString() : "N/A",
				status != null ? status.toString() : "N/A", role != null ? role.toString() : "N/A");
	}

	public String getFullName()
	{
		return String.format("%s %s", firstName, lastName);
	}
}