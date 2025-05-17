package domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import domain.Address;
import domain.Observer;
import domain.Subject;
import domain.notifications.NotificationObserver;
import dto.UserDTO;
import exceptions.InformationRequiredException;
import exceptions.InvalidInputException;
import util.AuthenticationUtil;
import util.DTOMapper;
import util.Role;
import util.Status;

/**
 * Controller class for managing user-related operations in the system. This
 * class serves as the main interface between the presentation layer and the
 * data access layer for all user management functionality.
 */
public class UserController implements Subject
{
	private UserDao userRepo;

	private List<Observer> observers = new ArrayList<>();

	/**
	 * Constructs a new UserController with default dependencies. Initializes the
	 * UserDaoJpa implementation and adds a default NotificationObserver.
	 */
	public UserController()
	{
		userRepo = new UserDaoJpa();
		addObserver(new NotificationObserver());

	}

	/**
	 * Authenticates a user with the provided credentials.
	 * 
	 * @param email    The user's email address
	 * @param password The user's password
	 * @throws InvalidInputException If authentication fails due to invalid
	 *                               credentials
	 */
	public void authenticate(String email, String password) throws InvalidInputException
	{
		AuthenticationUtil.authenticate(email, password, userRepo);
	}

	/**
	 * Logs out the currently authenticated user.
	 */

	public void logout()
	{
		AuthenticationUtil.logout();
	}

	/**
	 * Retrieves all users with the 'Technieker' role.
	 * 
	 * @return List of UserDTO objects representing all techniekers
	 */
	public List<UserDTO> getAllTechniekers()
	{
		List<User> techniekers = userRepo.getAllTechniekers();
		return DTOMapper.toUserDTOs(techniekers);
	}

	/**
	 * Retrieves all users in the system.
	 * 
	 * @return List of UserDTO objects representing all users
	 */
	public List<UserDTO> getAllUsers()
	{
		List<User> users = userRepo.findAll();
		return DTOMapper.toUserDTOs(users);
	}

	/**
	 * Converts a UserDTO to a User domain object.
	 * 
	 * @param dto The UserDTO to convert
	 * @return The converted User object
	 */
	public User convertToUser(UserDTO dto)
	{
		User existingUser = userRepo.getByEmail(dto.email());
		return DTOMapper.toUser(dto, existingUser);
	}

	/**
	 * Retrieves a user by their ID.
	 * 
	 * @param id The ID of the user to retrieve
	 * @return The User object with the specified ID
	 */
	public User getUserById(int id)
	{
		return userRepo.get(id);
	}

	/**
	 * Retrieves a user DTO by their ID.
	 * 
	 * @param id The ID of the user to retrieve
	 * @return The UserDTO object with the specified ID
	 */
	public UserDTO getUserDTOById(int id)
	{
		User user = getUserById(id);
		return DTOMapper.toUserDTO(user);
	}

	/**
	 * Retrieves a user by their email address.
	 * 
	 * @param email The email address of the user to retrieve
	 * @return The User object with the specified email
	 */
	public User getUserByEmail(String email)
	{
		return userRepo.getByEmail(email);
	}

	/**
	 * Retrieves a user DTO by their email address.
	 * 
	 * @param email The email address of the user to retrieve
	 * @return The UserDTO object with the specified email
	 */
	public UserDTO getUserDTOByEmail(String email)
	{
		User user = getUserByEmail(email);
		return DTOMapper.toUserDTO(user);
	}

	/**
	 * Retrieves all users with the 'Verantwoordelijke' role.
	 * 
	 * @return List of UserDTO objects representing all verantwoordelijken
	 */
	public List<UserDTO> getAllVerantwoordelijken()
	{
		return getAllUsers().stream().filter(user -> user.role().equals(Role.VERANTWOORDELIJKE))
				.collect(Collectors.toUnmodifiableList());
	}

	/**
	 * Creates a new user with the provided information.
	 * 
	 * @param firstName   The user's first name
	 * @param lastName    The user's last name
	 * @param email       The user's email address
	 * @param phoneNumber The user's phone number
	 * @param birthdate   The user's birthdate
	 * @param street      The street name of the user's address
	 * @param houseNumber The house number of the user's address
	 * @param postalCode  The postal code of the user's address
	 * @param city        The city of the user's address
	 * @param role        The user's role
	 * @return The created user as a UserDTO
	 * @throws InformationRequiredException If required information is missing
	 * @throws NumberFormatException        If houseNumber or postalCode are not
	 *                                      valid numbers
	 */
	public UserDTO createUser(String firstName, String lastName, String email, String phoneNumber, LocalDate birthdate,
			String street, String houseNumber, String postalCode, String city, Role role)
			throws InformationRequiredException, NumberFormatException
	{

		int houseNumberInt = Integer.parseInt(houseNumber);
		int postalCodeInt = Integer.parseInt(postalCode);

		Address address = new Address();
		address.setStreet(street);
		address.setNumber(houseNumberInt);
		address.setPostalcode(postalCodeInt);
		address.setCity(city);

		User newUser = new User.Builder().withFirstName(firstName).withLastName(lastName).withEmail(email)
				.withPhoneNumber(phoneNumber).withBirthdate(birthdate).withAddress(address).withRole(role)
				.withStatus(Status.ACTIEF).build();

		userRepo.startTransaction();
		userRepo.insert(newUser);
		userRepo.commitTransaction();

		notifyObservers("Gebruiker bijgewerkt: " + newUser.getId() + " " + newUser.getFullName());

		return DTOMapper.toUserDTO(newUser);
	}

	/**
	 * Updates an existing user with the provided information.
	 * 
	 * @param userId      The ID of the user to update
	 * @param firstName   The updated first name
	 * @param lastName    The updated last name
	 * @param email       The updated email address
	 * @param phoneNumber The updated phone number
	 * @param birthdate   The updated birthdate
	 * @param street      The updated street name
	 * @param houseNumber The updated house number
	 * @param postalCode  The updated postal code
	 * @param city        The updated city
	 * @param role        The updated role
	 * @param status      The updated status
	 * @return The updated user as a UserDTO
	 * @throws InformationRequiredException If required information is missing
	 * @throws NumberFormatException        If houseNumber or postalCode are not
	 *                                      valid numbers
	 * @throws IllegalArgumentException     If no user exists with the specified ID
	 */
	public UserDTO updateUser(int userId, String firstName, String lastName, String email, String phoneNumber,
			LocalDate birthdate, String street, String houseNumber, String postalCode, String city, Role role,
			Status status) throws InformationRequiredException, NumberFormatException
	{

		User existingUser = userRepo.get(userId);
		if (existingUser == null)
		{
			throw new IllegalArgumentException("User with ID " + userId + " not found");
		}

		int houseNumberInt = Integer.parseInt(houseNumber);
		int postalCodeInt = Integer.parseInt(postalCode);

		Address address = new Address();
		address.setStreet(street);
		address.setNumber(houseNumberInt);
		address.setPostalcode(postalCodeInt);
		address.setCity(city);

		if (existingUser.getAddress() != null)
		{
			address.setId(existingUser.getAddress().getId());
		}

		User updatedUser = new User.Builder().withFirstName(firstName).withLastName(lastName).withEmail(email)
				.withPhoneNumber(phoneNumber).withBirthdate(birthdate).withAddress(address).withRole(role)
				.withStatus(status).build();

		updatedUser.setId(existingUser.getId());

		if (existingUser.getAddress() != null && updatedUser.getAddress() != null)
		{
			updatedUser.getAddress().setId(existingUser.getAddress().getId());
		}

		userRepo.startTransaction();
		userRepo.update(updatedUser);
		userRepo.commitTransaction();

		notifyObservers("Gebruiker bijgewerkt: " + updatedUser.getId() + " " + updatedUser.getFullName());

		return DTOMapper.toUserDTO(updatedUser);
	}

	/**
	 * Deletes a user with the specified ID.
	 * 
	 * @param id The ID of the user to delete
	 */
	public void delete(int id)
	{
		User user = userRepo.get(id);
		userRepo.delete(user);
	}

	@Override
	public void addObserver(Observer observer)
	{
		observers.add(observer);

	}

	@Override
	public void removeObserver(Observer observer)
	{
		observers.remove(observer);

	}

	@Override
	public void notifyObservers(String message)
	{
		for (Observer o : observers)
			o.update(message);

	}

	/**
	 * Retrieves all distinct status values from users in the system.
	 * 
	 * @return List of all distinct status values as strings
	 */
	public List<String> getAllStatusses()
	{
		List<UserDTO> allUsers = getAllUsers();
		return allUsers.stream().map(u -> u.status().toString()).distinct().sorted().collect(Collectors.toList());
	}

	/**
	 * Retrieves all distinct role values from users in the system.
	 * 
	 * @return List of all distinct role values as strings
	 */
	public List<String> getAllRoles()
	{
		List<UserDTO> allUsers = getAllUsers();
		return allUsers.stream().map(u -> u.role().toString()).distinct().sorted().collect(Collectors.toList());
	}

	/**
	 * Retrieves users filtered by search criteria, status, and role.
	 * 
	 * @param searchFilter   The string to filter user names (case insensitive)
	 * @param selectedStatus The status to filter by (null for no status filter)
	 * @param selectedRole   The role to filter by (null for no role filter)
	 * @return List of UserDTO objects matching the filter criteria
	 */
	public List<UserDTO> getFilteredUsers(String searchFilter, String selectedStatus, String selectedRole)
	{
		String lowerCaseSearchFilter = searchFilter == null ? "" : searchFilter.toLowerCase();

		return getAllUsers().stream()
				.filter(user -> selectedStatus == null || user.status().toString().equals(selectedStatus))
				.filter(user -> selectedRole == null || user.role().toString().equals(selectedRole))
				.filter(user -> user.firstName().toLowerCase().contains(lowerCaseSearchFilter)
						|| user.lastName().toLowerCase().contains(lowerCaseSearchFilter))
				.collect(Collectors.toList());
	}

}