package domain.user;

import java.util.List;

import repository.GenericDao;

public interface UserDao extends GenericDao<User> {
	public User getByEmail(String email);
	
	//nodig voor het maken van machines
	List<User> getAllTechniekers();
}
