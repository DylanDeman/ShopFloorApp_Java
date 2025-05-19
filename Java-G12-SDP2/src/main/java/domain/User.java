package domain;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.InformationRequiredException;
import interfaces.Observer;
import interfaces.Subject;
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
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.PasswordHasher;
import util.RequiredElement;
import util.Role;
import util.Status;

/**
 * Represents a user entity in the system. This class models all user-related
 * data and implements the Subject interface for observer pattern support.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(name = "User.getAllWithAddress", query = "SELECT u FROM User u JOIN u.address a ORDER BY u.id"),
		@NamedQuery(name = "User.getAllTechniekers", query = "SELECT u FROM User u WHERE u.role = util.Role.TECHNIEKER"),
		@NamedQuery(name = "User.getByEmail", query = "SELECT u FROM User u WHERE u.email = :email ORDER BY u.id") })
public class User implements Serializable, Subject
{
	private static final long serialVersionUID = 1L;

	@Transient
	private List<Observer> observers = new ArrayList<>();

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

	/**
	 * Constructs a new User with all required fields.
	 * 
	 * @param firstName   The user's first name
	 * @param lastName    The user's last name
	 * @param email       The user's email address
	 * @param phoneNumber The user's phone number
	 * @param password    The user's hashed password
	 * @param birthdate   The user's birthdate
	 * @param address     The user's address
	 * @param status      The user's system status
	 * @param role        The user's system role
	 */
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

	/**
	 * Calculates the user's age based on birthdate.
	 * 
	 * @return The user's age in years
	 */
	public int getAge()
	{
		return Period.between(birthdate, LocalDate.now()).getYears();
	}

	/**
	 * Returns a string representation of the user.
	 * 
	 * @return Formatted string containing all user fields
	 */
	@Override
	public String toString()
	{
		return String.format("%s %s, %s, %s, %s, %s, %s, %s, %s", firstName != null ? firstName : "N/A",
				lastName != null ? lastName : "N/A", email != null ? email : "N/A",
				phoneNumber != null ? phoneNumber : "N/A", password != null ? password : "N/A",
				birthdate != null ? birthdate.toString() : "N/A", address != null ? address.toString() : "N/A",
				status != null ? status.toString() : "N/A", role != null ? role.toString() : "N/A");
	}

	/**
	 * Returns the user's full name.
	 * 
	 * @return Concatenation of first and last name
	 */
	public String getFullName()
	{
		return String.format("%s %s", firstName, lastName);
	}

	@Override
	public void addObserver(Observer o)
	{
		observers.add(o);
		notifyObservers("");
	}

	@Override
	public void removeObserver(Observer o)
	{
		observers.remove(o);
		notifyObservers("");
	}

	@Override
	public void notifyObservers(String message)
	{
		observers.forEach(o -> o.update(message));
	}

	/**
	 * Builder class for constructing User instances with validation. Implements a
	 * fluent interface for setting properties and includes validation of required
	 * fields before building the User object.
	 */
	public static class Builder
	{
		private String firstName;
		private String lastName;
		private String email;
		private String phoneNumber;
		private LocalDate birthdate;
		private Role role;
		private Status status;
		private Address address;

		public Builder()
		{

		}

		protected User user;

		/**
		 * Sets the user's first name.
		 * 
		 * @param firstName The first name to set
		 * @return The builder instance for method chaining
		 */
		public Builder withFirstName(String firstName)
		{
			this.firstName = firstName;
			return this;
		}

		/**
		 * Sets the user's last name.
		 * 
		 * @param lastName The last name to set
		 * @return The builder instance for method chaining
		 */
		public Builder withLastName(String lastName)
		{
			this.lastName = lastName;
			return this;
		}

		/**
		 * Sets the user's email address.
		 * 
		 * @param email The email to set
		 * @return The builder instance for method chaining
		 */
		public Builder withEmail(String email)
		{
			this.email = email;
			return this;
		}

		/**
		 * Sets the user's phone number.
		 * 
		 * @param phoneNumber The phone number to set
		 * @return The builder instance for method chaining
		 */
		public Builder withPhoneNumber(String phoneNumber)
		{
			this.phoneNumber = phoneNumber;
			return this;
		}

		/**
		 * Sets the user's birthdate.
		 * 
		 * @param birthdate The birthdate to set
		 * @return The builder instance for method chaining
		 */
		public Builder withBirthdate(LocalDate birthdate)
		{
			this.birthdate = birthdate;
			return this;
		}

		/**
		 * Sets the user's role.
		 * 
		 * @param role The role to set
		 * @return The builder instance for method chaining
		 */
		public Builder withRole(Role role)
		{
			this.role = role;
			return this;
		}

		/**
		 * Sets the user's status.
		 * 
		 * @param status The status to set
		 * @return The builder instance for method chaining
		 */
		public Builder withStatus(Status status)
		{
			this.status = status;
			return this;
		}

		/**
		 * Sets the user's address.
		 * 
		 * @param address The address to set
		 * @return The builder instance for method chaining
		 */
		public Builder withAddress(Address address)
		{
			this.address = address;
			return this;
		}

		/**
		 * Builds the User instance after validating all required fields. Automatically
		 * generates and hashes a secure password.
		 * 
		 * @return The constructed User instance
		 * @throws InformationRequiredException If any required fields are missing
		 */
		public User build() throws InformationRequiredException
		{
			validateRequiredFields();

			String password = generatePassword();
			user = new User();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setPhoneNumber(phoneNumber);
			user.setPassword(PasswordHasher.hash(password));
			user.setBirthdate(birthdate);
			user.setAddress(address);
			user.setRole(role);
			user.setStatus(status);

			System.out.println("Generated password: " + password);

			return user;
		}

		/**
		 * Validates that all required fields have been set.
		 * 
		 * @throws InformationRequiredException If any required fields are missing
		 */
		private void validateRequiredFields() throws InformationRequiredException
		{
			Map<String, RequiredElement> requiredElements = new HashMap<>();

			if (firstName == null || firstName.isEmpty())
			{
				requiredElements.put("firstName", RequiredElement.FIRST_NAME_REQUIRED);
			}

			if (lastName == null || lastName.isEmpty())
			{
				requiredElements.put("lastName", RequiredElement.LAST_NAME_REQUIRED);
			}

			if (email == null || email.isEmpty())
			{
				requiredElements.put("email", RequiredElement.EMAIL_REQUIRED);
			}

			if (birthdate == null)
			{
				requiredElements.put("birthDate", RequiredElement.BIRTH_DATE_REQUIRED);
			}

			if (address == null || address.getStreet() == null || address.getStreet().isEmpty())
			{
				requiredElements.put("street", RequiredElement.STREET_REQUIRED);
			}

			if (address == null || address.getNumber() == 0)
			{
				requiredElements.put("number", RequiredElement.NUMBER_REQUIRED);
			}

			if (address == null || address.getPostalcode() == 0)
			{
				requiredElements.put("postalCode", RequiredElement.POSTAL_CODE_REQUIRED);
			}

			if (address == null || address.getCity() == null || address.getCity().isEmpty())
			{
				requiredElements.put("city", RequiredElement.CITY_REQUIRED);
			}

			if (role == null)
			{
				requiredElements.put("role", RequiredElement.ROLE_REQUIRED);
			}

			if (status == null)
			{
				requiredElements.put("status", RequiredElement.STATUS_REQUIRED);
			}

			if (!requiredElements.isEmpty())
			{
				throw new InformationRequiredException(requiredElements);
			}
		}

		/**
		 * Generates a secure random password with a mix of character types. The
		 * password will contain at least one lowercase letter, one uppercase letter,
		 * one digit, and one special character. The total length of the password will
		 * be between 10 and 20 characters (inclusive).
		 * 
		 * The password is generated using cryptographically secure random numbers and
		 * includes characters from the following categories: - Lowercase letters (a-z)
		 * - Uppercase letters (A-Z) - Digits (0-9) - Special characters
		 * (!@#$%^&*()-_=+[]{}|;:'",.<>/?)
		 * 
		 * @return A randomly generated secure password as a String
		 * @see SecureRandom
		 */
		private static String generatePassword()
		{
			SecureRandom random = new SecureRandom();
			String lower = "abcdefghijklmnopqrstuvwxyz";
			String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			String digits = "0123456789";
			String special = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?";
			String allChars = lower + upper + digits + special;

			int length = 10 + random.nextInt(11);

			ArrayList<Character> password = new ArrayList<>();

			password.add(lower.charAt(random.nextInt(lower.length())));
			password.add(upper.charAt(random.nextInt(upper.length())));
			password.add(digits.charAt(random.nextInt(digits.length())));
			password.add(special.charAt(random.nextInt(special.length())));

			for (int i = 4; i < length; i++)
			{
				password.add(allChars.charAt(random.nextInt(allChars.length())));
			}

			Collections.shuffle(password, random);

			StringBuilder sb = new StringBuilder();
			for (char c : password)
			{
				sb.append(c);
			}

			return sb.toString();
		}
	}

}