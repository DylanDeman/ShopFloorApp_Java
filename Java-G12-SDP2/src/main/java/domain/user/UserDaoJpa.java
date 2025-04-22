package domain.user;

import repository.GenericDaoJpa;

public class UserDaoJpa extends GenericDaoJpa<User> implements UserDao {

	public UserDaoJpa() {
		super(User.class);
	}

	@Override
	public User getByEmail(String email) {
	    try {
	        return em.createQuery(
	                "SELECT u FROM User u WHERE u.email = :email", User.class)
	                .setParameter("email", email)
	                .getSingleResult();
	    } catch (Exception e) {
	        return null; // TODO gooi een custom exception
	    }
	}
}
