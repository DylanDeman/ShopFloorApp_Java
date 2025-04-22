package domain.user;

import repository.GenericDao;

public interface UserDao extends GenericDao<User> {
	public User getByEmail(String email);
}
