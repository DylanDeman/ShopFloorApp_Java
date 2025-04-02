package domain;

import java.time.LocalDate;
import java.util.List;
import interfaces.IUserService;
import util.Role;
import util.Status;

public class UserServiceController {
	private final IUserService userService;

	// Hier de currentUser meekrijgen is niet zo mooi, later aanpassen zodat hij
	// zelf weet wie currentUser is!
	public UserServiceController(IUserService userService, User currentUser) {
		if (userService == null) {
			throw new IllegalArgumentException("User service cannot be null!");
		}

		if (currentUser == null) {
			throw new IllegalArgumentException("Current user cannot be null!");
		}

		IUserService proxiedService = new UserServiceProxy(userService, currentUser);
		this.userService = proxiedService;
	}

	// Dit is voor registratie (altijd echte service gebruiken en geen
	// autorisatie/authenticatie nodig)
	public UserServiceController(IUserService userService) {
		if (userService == null) {
			throw new IllegalArgumentException("User service cannot be null");
		}
		this.userService = userService;
	}

	public List<User> getAllUsers() {
		return userService.getAll();
	}

	public User getUserById(int id) {
		return userService.getById(id);
	}

	public User createUser(String firstName, String lastName, String email, String phoneNumber, String password,
			LocalDate birthdate, Address address, Status status, Role role) {
		// TODO
		return null;
	}

	public User updateUser(int id, String firstName, String lastName, String email, String phoneNumber, String password,
			LocalDate birthdate, Address address, Status status, Role role) {
		// TODO
		return null;
	}

	public boolean deleteUser(int id) {
		// TODO
		return false;
	}

}