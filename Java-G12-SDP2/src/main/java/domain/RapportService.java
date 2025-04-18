package domain;

import java.time.LocalDate;
import java.util.List;

import exceptions.InvalidRapportException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import repository.GenericDaoJpa;

public class RapportService extends GenericDaoJpa<Rapport>
{

	private final EntityManagerFactory emf;

	public RapportService(Class<Rapport> type)
	{
		super(type);
		this.emf = GenericDaoJpa.em.getEntityManagerFactory();
	}

	@Override
	public List<Rapport> findAll()
	{
		try (EntityManager em = emf.createEntityManager())
		{
			TypedQuery<Rapport> query = em.createNamedQuery("Rapport.getAll", Rapport.class);
			return query.getResultList();
		}
	}

	@Override
	public <U> Rapport get(U id)
	{
		try (EntityManager em = emf.createEntityManager())
		{
			Rapport rapport = em.find(Rapport.class, id);
			if (rapport == null)
			{
				throw new InvalidRapportException("Rapport with ID " + id + " not found");
			}
			return rapport;
		}
	}

	@Override
	public void insert(Rapport entity)
	{
		if (entity == null || entity.getRapportId() == null)
		{
			throw new InvalidRapportException("Invalid rapport");
		}

		try (EntityManager em = emf.createEntityManager())
		{
			EntityTransaction tx = em.getTransaction();
			try
			{
				tx.begin();
				em.persist(entity);
				tx.commit();
			} catch (Exception e)
			{
				if (tx.isActive())
					tx.rollback();
				throw new InvalidRapportException("Failed to save rapport: " + e.getMessage());
			}
		}
	}

	@Override
	public Rapport update(Rapport entity)
	{
		if (entity == null || entity.getRapportId() == null)
		{
			throw new InvalidRapportException("Invalid rapport");
		}

		try (EntityManager em = emf.createEntityManager())
		{
			EntityTransaction tx = em.getTransaction();
			try
			{
				tx.begin();
				Rapport existingRapport = em.find(Rapport.class, entity.getRapportId());
				if (existingRapport == null)
				{
					throw new InvalidRapportException("Rapport with ID " + entity.getRapportId() + " not found");
				}
				Rapport merged = em.merge(entity);
				tx.commit();
				return merged;
			} catch (Exception e)
			{
				if (tx.isActive())
					tx.rollback();
				throw new InvalidRapportException("Failed to update rapport: " + e.getMessage());
			}
		}
	}

	@Override
	public void delete(Rapport entity)
	{
		try (EntityManager em = emf.createEntityManager())
		{
			EntityTransaction tx = em.getTransaction();
			try
			{
				tx.begin();
				Rapport existingRapport = em.find(Rapport.class, entity.getRapportId());
				if (existingRapport == null)
				{
					throw new InvalidRapportException("Rapport with ID " + entity.getRapportId() + " not found");
				}
				em.remove(existingRapport);
				tx.commit();
			} catch (Exception e)
			{
				if (tx.isActive())
					tx.rollback();
				throw new InvalidRapportException("Failed to delete rapport: " + e.getMessage());
			}
		}
	}

	public List<Rapport> getRapportenByTechnieker(User technieker)
	{
		if (technieker == null)
		{
			throw new InvalidRapportException("Technieker cannot be null");
		}

		try (EntityManager em = emf.createEntityManager())
		{
			TypedQuery<Rapport> query = em.createNamedQuery("Rapport.findByTechnieker", Rapport.class);
			query.setParameter("technieker", technieker);
			return query.getResultList();
		}
	}

	public List<Rapport> getRapportenBySite(Site site)
	{
		if (site == null)
		{
			throw new InvalidRapportException("Site cannot be null");
		}

		try (EntityManager em = emf.createEntityManager())
		{
			TypedQuery<Rapport> query = em.createNamedQuery("Rapport.findBySite", Rapport.class);
			query.setParameter("site", site);
			return query.getResultList();
		}
	}

	public List<Rapport> getRapportenByDateRange(LocalDate startDate, LocalDate endDate)
	{
		if (startDate == null || endDate == null)
		{
			throw new InvalidRapportException("Date range cannot be null");
		}

		if (endDate.isBefore(startDate))
		{
			throw new InvalidRapportException("End date cannot be before start date");
		}

		try (EntityManager em = emf.createEntityManager())
		{
			TypedQuery<Rapport> query = em.createNamedQuery("Rapport.findByDateRange", Rapport.class);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			return query.getResultList();
		}
	}
}
