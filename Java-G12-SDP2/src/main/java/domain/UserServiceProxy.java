package domain;

import java.util.List;

import interfaces.IUserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserServiceProxy implements IUserService {
	private UserService userService;
	private User user;

	@Override
	public List<User> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(User entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(User entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		
	}

}
