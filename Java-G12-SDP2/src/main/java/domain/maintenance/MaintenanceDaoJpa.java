package domain.maintenance;

import repository.GenericDaoJpa;

public class MaintenanceDaoJpa extends GenericDaoJpa<Maintenance> implements MaintenanceDao {

	public MaintenanceDaoJpa() {
		super(Maintenance.class);
	}

}
