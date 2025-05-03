package repository;

import java.util.ArrayList;
import java.util.List;

import domain.user.User;
import interfaces.Observer;
import interfaces.Subject;

public class UserRepository implements Subject
{
	private final GenericDao<User> userDao;
	private final List<Observer> observers = new ArrayList<>();

	public UserRepository(GenericDao<User> userDao)
	{
		this.userDao = userDao;
	}

	@Override
	public void addObserver(Observer o)
	{
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o)
	{
		observers.remove(o);
	}

	@Override
	public void notifyObservers()
	{
		observers.forEach(o -> o.update());
	}

	public List<User> getAllUsers()
	{
		try
		{
			userDao.startTransaction();
			List<User> users = userDao.findAll();
			userDao.commitTransaction();
			return users;
		} catch (Exception e)
		{
			userDao.rollbackTransaction();
			throw e;
		}
	}

	public void addUser(User user)
	{
		try
		{
			userDao.startTransaction();
			userDao.insert(user);
			userDao.commitTransaction();
			notifyObservers();
		} catch (Exception e)
		{
			userDao.rollbackTransaction();
			throw e;
		}
	}

	public void updateUser(User user)
	{
		try
		{
			userDao.startTransaction();
			userDao.update(user);
			userDao.commitTransaction();
			notifyObservers();
		} catch (Exception e)
		{
			userDao.rollbackTransaction();
			throw e;
		}
	}

	// TODO soft delete ipv hard delete
	public void deleteUser(User user)
	{
		try
		{
			userDao.startTransaction();
			userDao.delete(user);
			userDao.commitTransaction();
			notifyObservers();
		} catch (Exception e)
		{
			userDao.rollbackTransaction();
			throw new RuntimeException("Kon gebruiker niet verwijderen: " + e.getMessage(), e);
		}
	}

}