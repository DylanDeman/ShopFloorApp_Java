package domain;

import java.util.List;

import interfaces.IUserService;
import util.Role;

// TODO nu kan je alles doen zolang je admin bent -> veranderen naar juiste rollen
// TODO mooie en juiste fouten teruggeven
public class UserServiceProxy implements IUserService {

	private final IUserService realService;
	private final User currentUser;

	public UserServiceProxy(IUserService realService, User currentUser) {
		this.realService = realService;
		this.currentUser = currentUser;
	}

	
	@Override
	public List<User> getAll() {
		if (currentUser != null && currentUser.getRole() == Role.ADMIN) { 
			return realService.getAll();
		} else {
			throw new SecurityException("Deze actie mag niet!");
		}
	}

	@Override
	public User getById(int id) {
		if (currentUser != null && (currentUser.getId() == id || currentUser.getRole() == Role.ADMIN)) {
			return realService.getById(id);
		} else {
			throw new SecurityException("Deze actie mag niet!");
		}
	}

	@Override
	public void create(User entity) {
		if (currentUser == null || currentUser.getRole() == Role.ADMIN) {
			if (currentUser == null && entity.getRole() == Role.ADMIN) {
				throw new SecurityException("Deze actie mag niet!");
			}

			realService.create(entity);
		} else {
			throw new SecurityException("Deze actie mag niet!");
		}
	}

	@Override
	public void update(User entity) {
		if (currentUser != null && (currentUser.getId() == entity.getId() || currentUser.getRole() == Role.ADMIN)) {
			if (currentUser.getId() == entity.getId() && currentUser.getRole() != Role.ADMIN
					&& entity.getRole() == Role.ADMIN) {
				throw new SecurityException("Deze actie mag niet!");
			}
			realService.update(entity);
		} else {
			throw new SecurityException("Deze actie mag niet!");
		}
	}

	@Override
	public void delete(int id) {
		if (currentUser != null && (currentUser.getId() == id || currentUser.getRole() == Role.ADMIN)) {
			realService.delete(id);
		} else {
			throw new SecurityException("Deze actie mag niet!");
		}
	}

	public User getCurrentUser() {
		return currentUser;
	}
}