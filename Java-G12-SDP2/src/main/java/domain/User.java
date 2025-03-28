package domain;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.Role;
import util.Status;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;

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
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.birthdate = birthdate;
		this.address = address;
		this.status = status;
		this.role = role;
	}

	@Override
	public String toString()
	{
		return "%s %s, %s, %s, %s, %s, %s, %s, %s".formatted(firstName != null ? firstName : "N/A",
				lastName != null ? lastName : "N/A", email != null ? email : "N/A",
				phoneNumber != null ? phoneNumber : "N/A", password != null ? password : "N/A",
				birthdate != null ? birthdate.toString() : "N/A", address != null ? address.toString() : "N/A",
				status != null ? status.toString() : "N/A", role != null ? role.toString() : "N/A");
	}

}
