package domain;

import java.util.List;

import exceptions.InvalidUserException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import repository.GenericDaoJpa;

// In de service komt altijd de business-logica!
// Dus als er bepaalde berekeningen moeten gedaan worden met de data 
public class UserService extends GenericDaoJpa<User> {
    public UserService(Class<User> type) {
		super(type);
		this.emf = GenericDaoJpa.em.getEntityManagerFactory();
	}

	private final EntityManagerFactory emf;
    
    @Override
    public List<User> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<User> query = em.createNamedQuery("User.getAllWithAddress", User.class);
            return query.getResultList();
        }
    }
    
    @Override
    public <U> User get(U id) {
        try (EntityManager em = emf.createEntityManager()) {
            User user = em.find(User.class, id);
            if (user == null) {
                throw new InvalidUserException("User with ID " + id + " not found");
            }
            return user;
        }
    }
    
    @Override
    public void insert(User entity) {
        if (entity == null) {
            throw new InvalidUserException("User cannot be null");
        }
        
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(entity);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new InvalidUserException("Failed to create user: " + e.getMessage());
            }
        }
    }
    
    @Override
    public User update(User entity) {
        if (entity == null) {
            throw new InvalidUserException("User cannot be null");
        }
        
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                User existingUser = em.find(User.class, entity.getId());
                if (existingUser == null) {
                    throw new InvalidUserException("User with ID " + entity.getId() + " not found");
                }
                
                em.merge(entity);
                tx.commit();
                return existingUser;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new InvalidUserException("Failed to update user: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void delete(User user) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                User foundUser = em.find(User.class, user.getId());
                if (foundUser == null) {
                    throw new InvalidUserException("User with ID " + user.getId() + " not found");
                }
                
                em.remove(user);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new InvalidUserException("Failed to delete user: " + e.getMessage());
            }
        }
    }
    
    // TODO methoden voor authenticatie van gebruiker komen hier later:
    
}
