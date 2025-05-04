package gui;

import domain.machine.MachineController;
import domain.maintenance.MaintenanceController;
import domain.site.Site;
import domain.site.SiteController;
import domain.user.User;
import lombok.Getter;
import repository.GenericDao;
import repository.GenericDaoJpa;
import repository.SiteRepository;
import repository.UserRepository;

@Getter
public class AppServices
{
	private final UserRepository userRepo;
	private final SiteRepository siteRepo;
	private final SiteController siteController;
	private final MachineController machineController;
	private final MaintenanceController maintenanceController;

	public AppServices()
	{
		GenericDao<User> userDao = new GenericDaoJpa<>(User.class);
		this.userRepo = new UserRepository(userDao);

		GenericDao<Site> siteDao = new GenericDaoJpa<>(Site.class);
		this.siteRepo = new SiteRepository(siteDao);

		this.siteController = new SiteController();
		this.machineController = new MachineController();
		this.maintenanceController = new MaintenanceController();
	}
}