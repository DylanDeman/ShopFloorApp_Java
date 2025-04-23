package domain.machine;

import domain.site.Site;
import domain.site.SiteDao;
import repository.GenericDaoJpa;

public class MachineDaoJpa extends GenericDaoJpa<Machine> implements MachineDao {
	public MachineDaoJpa() {
		super(Machine.class);
	}
}
