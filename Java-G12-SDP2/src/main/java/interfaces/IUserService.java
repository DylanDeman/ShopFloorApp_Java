package interfaces;

import java.util.List;

import domain.User;

// TODO
// Dit kan generic zijn ipv een IUserService, ... 
// zo hoeven we niet voor elke service klasse een interface te hebben:
// nadeel: minder flexibel -> nog bespreken
public interface IUserService {
	List<User> getAll();
	User getById(int id);
	void create(User entity);
	void update(User entity);
    void delete(int id);
}