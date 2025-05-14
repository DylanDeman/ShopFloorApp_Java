package domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

public class UserController implements Subject{
	private UserDao userRepo;
	
	private List<Observer> observers = new ArrayList<>();

	public UserController() {
		userRepo = new UserDaoJpa();
		addObserver(new NotificationObserver());

	}

	public void authenticate(String email, String password) throws InvalidInputException {
		AuthenticationUtil.authenticate(email, password, userRepo);
	}

	public void logout() {
		AuthenticationUtil.logout();
	}

	public List<UserDTO> getAllTechniekers() {
		List<User> techniekers = userRepo.getAllTechniekers();
		return DTOMapper.toUserDTOs(techniekers);
	}

	public List<UserDTO> getAllUsers() {
		List<User> users = userRepo.findAll();
		return DTOMapper.toUserDTOs(users);
	}

	public User convertToUser(UserDTO dto) {
		User existingUser = userRepo.getByEmail(dto.email());
		return DTOMapper.toUser(dto, existingUser);
	}

	public User getUserById(int id) {
		return userRepo.get(id);
	}

	public UserDTO getUserDTOById(int id) {
		User user = getUserById(id);
		return DTOMapper.toUserDTO(user);
	}

	public User getUserByEmail(String email) {
		return userRepo.getByEmail(email);
	}

	public UserDTO getUserDTOByEmail(String email) {
		User user = getUserByEmail(email);
		return DTOMapper.toUserDTO(user);
	}

	public List<UserDTO> getAllVerantwoordelijken() {
		return getAllUsers().stream().filter(user -> user.role().equals(Role.VERANTWOORDELIJKE))
				.collect(Collectors.toUnmodifiableList());
	}

	public UserDTO createUser(String firstName, String lastName, String email, String phoneNumber, LocalDate birthdate,
			String street, String houseNumber, String postalCode, String city, Role role)
			throws InformationRequiredException, NumberFormatException {

		int houseNumberInt = Integer.parseInt(houseNumber);
		int postalCodeInt = Integer.parseInt(postalCode);

		UserBuilder userBuilder = new UserBuilder();
		userBuilder.createUser();
		userBuilder.buildName(firstName, lastName);
		userBuilder.buildContactInfo(email, phoneNumber);
		userBuilder.buildBirthdate(birthdate);
		userBuilder.createAddress();
		userBuilder.buildStreet(street);
		userBuilder.buildNumber(houseNumberInt);
		userBuilder.buildPostalcode(postalCodeInt);
		userBuilder.buildCity(city);
		userBuilder.buildRoleAndStatus(role, Status.ACTIEF); // New users are active by default

		User newUser = userBuilder.getUser();

		userRepo.startTransaction();
		userRepo.insert(newUser);
		userRepo.commitTransaction();
		
		notifyObservers("Gebruiker bijgewerkt: " + newUser.getId() + " " + newUser.getFullName());


		return DTOMapper.toUserDTO(newUser);
	}

	public UserDTO updateUser(int userId, String firstName, String lastName, String email, String phoneNumber,
			LocalDate birthdate, String street, String houseNumber, String postalCode, String city, Role role,
			Status status) throws InformationRequiredException, NumberFormatException {

		User existingUser = userRepo.get(userId);
		if (existingUser == null) {
			throw new IllegalArgumentException("User with ID " + userId + " not found");
		}

		int houseNumberInt = Integer.parseInt(houseNumber);
		int postalCodeInt = Integer.parseInt(postalCode);

		UserBuilder userBuilder = new UserBuilder();
		userBuilder.createUser();
		userBuilder.buildName(firstName, lastName);
		userBuilder.buildContactInfo(email, phoneNumber);
		userBuilder.buildBirthdate(birthdate);
		userBuilder.createAddress();
		userBuilder.buildStreet(street);
		userBuilder.buildNumber(houseNumberInt);
		userBuilder.buildPostalcode(postalCodeInt);
		userBuilder.buildCity(city);
		userBuilder.buildRoleAndStatus(role, status);

		User updatedUser = userBuilder.getUser();
		updatedUser.setId(existingUser.getId());

		if (existingUser.getAddress() != null && updatedUser.getAddress() != null) {
			updatedUser.getAddress().setId(existingUser.getAddress().getId());
		}

		userRepo.startTransaction();
		userRepo.update(updatedUser);
		userRepo.commitTransaction();
		
		notifyObservers("Gebruiker bijgewerkt: " + updatedUser.getId() + " " + updatedUser.getFullName());

		return DTOMapper.toUserDTO(updatedUser);
	}

	public void delete(int id) {
		User user = userRepo.get(id);
		userRepo.delete(user);
	}

	@Override
	public void addObserver(Observer observer) {
		observers.add(observer);
		
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
		
	}

	@Override
	public void notifyObservers(String message) {
		for(Observer o : observers)
			o.update(message);
		
	}
}