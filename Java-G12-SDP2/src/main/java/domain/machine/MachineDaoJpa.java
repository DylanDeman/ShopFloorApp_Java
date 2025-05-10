package domain.machine;

import repository.GenericDaoJpa;

public class MachineDaoJpa extends GenericDaoJpa<Machine> implements MachineDao {
	public MachineDaoJpa() {
		super(Machine.class);
	}
}
