package domain.user;

import exceptions.InvalidInputException;
import util.AuthenticationUtil;

public class UserController {
	private UserDao userRepo;

	public UserController() {
		userRepo = new UserDaoJpa();
	}

	public void authenticate(String email, String password) throws InvalidInputException {
		AuthenticationUtil.authenticate(email, password, userRepo);
	}
	
	public void logout() {
		AuthenticationUtil.logout();
	}
}