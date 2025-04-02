package domain;

import exceptions.InvalidMachineException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class MachineService {
    
    
    private EntityManagerFactory emf;

    public void create(Machine entity) {
        if (entity == null) {
            throw new InvalidMachineException("Machine cannot be null");
        }
        
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(entity);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new InvalidMachineException("Failed to create machine: " + e.getMessage());
            }
        }
    }
    
    public void update(Machine entity) {
        if (entity == null) {
            throw new InvalidMachineException("Machine cannot be null");
        }
        
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                
                //TODO getCode nog implementeren!!!!!
                Machine existingMachine = em.find(Machine.class, entity.getCode());
                if (existingMachine == null) {
                    throw new InvalidMachineException("Machine with ID " + entity.getCode() + " not found");
                }
                
                em.merge(entity);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new InvalidMachineException("Failed to update machine: " + e.getMessage());
            }
        }
    }
}
