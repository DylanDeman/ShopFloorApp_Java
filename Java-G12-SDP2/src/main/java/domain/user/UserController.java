package domain.user;

import java.util.List;

import exceptions.InvalidInputException;
import util.AuthenticationUtil;

public class UserController
{
	private UserDao userRepo;

	public UserController()
	{
		userRepo = new UserDaoJpa();
	}

	public void authenticate(String email, String password) throws InvalidInputException
	{
		AuthenticationUtil.authenticate(email, password, userRepo);
	}

	public void logout()
	{
		AuthenticationUtil.logout();
	}

	public List<User> getAllTechniekers()
	{
		return userRepo.getAllTechniekers();
	}

}