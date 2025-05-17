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

public class UserController implements Subject
{
	private UserDao userRepo;

	private List<Observer> observers = new ArrayList<>();

	public UserController()
	{
		userRepo = new UserDaoJpa();
		addObserver(new NotificationObserver());

	}

	public void authenticate(String email, String password) throws InvalidInputException
	{
		AuthenticationUtil.authenticate(email, password, userRepo);
	}

	public void logout()
	{
		AuthenticationUtil.logout();
	}

	public List<UserDTO> getAllTechniekers()
	{
		List<User> techniekers = userRepo.getAllTechniekers();
		return DTOMapper.toUserDTOs(techniekers);
	}

	public List<UserDTO> getAllUsers()
	{
		List<User> users = userRepo.findAll();
		return DTOMapper.toUserDTOs(users);
	}

	public User convertToUser(UserDTO dto)
	{
		User existingUser = userRepo.getByEmail(dto.email());
		return DTOMapper.toUser(dto, existingUser);
	}

	public User getUserById(int id)
	{
		return userRepo.get(id);
	}

	public UserDTO getUserDTOById(int id)
	{
		User user = getUserById(id);
		return DTOMapper.toUserDTO(user);
	}

	public User getUserByEmail(String email)
	{
		return userRepo.getByEmail(email);
	}

	public UserDTO getUserDTOByEmail(String email)
	{
		User user = getUserByEmail(email);
		return DTOMapper.toUserDTO(user);
	}

	public List<UserDTO> getAllVerantwoordelijken()
	{
		return getAllUsers().stream().filter(user -> user.role().equals(Role.VERANTWOORDELIJKE))
				.collect(Collectors.toUnmodifiableList());
	}

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

	public List<String> getAllStatusses()
	{
		List<UserDTO> allUsers = getAllUsers();
		return allUsers.stream().map(u -> u.status().toString()).distinct().sorted().collect(Collectors.toList());
	}

	public List<String> getAllRoles()
	{
		List<UserDTO> allUsers = getAllUsers();
		return allUsers.stream().map(u -> u.role().toString()).distinct().sorted().collect(Collectors.toList());
	}

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