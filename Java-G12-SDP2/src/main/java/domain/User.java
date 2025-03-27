package domain;

import java.time.LocalDate;

import utils.Role;
import utils.Status;

public class User
{
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phoneNumber;

	private LocalDate birhtdate;

	private Adress adress;

	private Status status;
	private Role role;

}
