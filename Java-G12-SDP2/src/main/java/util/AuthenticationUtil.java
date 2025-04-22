package util;

import domain.user.User;
import domain.user.UserDao;
import exceptions.InvalidInputException;

public class AuthenticationUtil {
	
	private static User authenticatedUser = null;
	
	public static boolean authenticate(String email, String inputPassword, UserDao userRepo) throws InvalidInputException {
		User user = userRepo.getByEmail(email);

		if (user == null || !PasswordHasher.verify(inputPassword, user.getPassword())) {
			authenticatedUser = null;
			throw new InvalidInputException("E-mailadres en wachtwoord komen niet overeen. Probeer het opnieuw.");
		}
		authenticatedUser = user;
		return true;
	}
	
	public static boolean isAuth() {
		return authenticatedUser != null;
	}
	
	public static User getAuthenticatedUser() {
		return authenticatedUser;
	}
	
	public static boolean hasRole(Role role) {
		if(isAuth()) {
			return authenticatedUser.getRole() == role;
		}
		return false;
	}
}
