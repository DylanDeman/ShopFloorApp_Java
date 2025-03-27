package domain;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import utils.Role;
import utils.Status;

@Getter
@Setter
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

}
