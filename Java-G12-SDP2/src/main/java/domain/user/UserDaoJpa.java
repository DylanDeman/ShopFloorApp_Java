package domain.user;

import java.util.List;

import exceptions.UserNotFoundWithEmailException;
import repository.GenericDaoJpa;

public class UserDaoJpa extends GenericDaoJpa<User> implements UserDao
{

	public UserDaoJpa()
	{
		super(User.class);
	}

	@Override
	public User getByEmail(String email)
	{
		try
		{
			return em.createNamedQuery("User.getByEmail", User.class).setParameter("email", email).getSingleResult();
		} catch (Exception e)
		{
			throw new UserNotFoundWithEmailException(email);
		}
	}

	@Override
	public List<User> getAllTechniekers()
	{
		return em.createNamedQuery("User.getAllTechniekers", User.class).getResultList();
	}
}
