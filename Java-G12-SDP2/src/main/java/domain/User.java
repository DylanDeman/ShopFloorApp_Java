package domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import utils.Role;
import utils.Status;

@Getter
@Setter
@AllArgsConstructor
public class User
{
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String password;

	private LocalDate birthdate;

	private Address address;

	private Status status;
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

}
