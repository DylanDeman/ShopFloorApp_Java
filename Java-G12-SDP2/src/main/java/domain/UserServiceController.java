package domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import util.Role;
import util.Status;

// Deze klasse is zoals DC, maar nu enkel voor User, 
// GUI communiceert enkel met de controllers = Facade dus
public class UserServiceController {
	private UserService userService;

	// Hier de currentUser meekrijgen is niet zo mooi, later aanpassen zodat hij
	// zelf weet wie currentUser is!
	public UserServiceController(UserService userService, User currentUser) {
		if (userService == null) {
			throw new IllegalArgumentException("User service cannot be null!");
		}

		if (currentUser == null) {
			throw new IllegalArgumentException("Current user cannot be null!");
		}
	}

	// Dit is voor registratie (altijd echte service gebruiken en geen
	// autorisatie/authenticatie nodig)
	public UserServiceController(UserService userService) {
		if (userService == null) {
			throw new IllegalArgumentException("User service cannot be null");
		}
		this.userService = userService;
	}

	public List<User> getAllUsers() {
		return Collections.unmodifiableList(userService.findAll());
	}

	public User getUserById(int id) {
		return userService.get(id);
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