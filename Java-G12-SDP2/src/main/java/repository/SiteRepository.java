package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import domain.machine.Machine;
import domain.machine.MachineDTO;
import domain.site.Site;
import domain.site.SiteDTO;
import domain.user.User;
import interfaces.Observer;
import interfaces.Subject;

public class SiteRepository implements Subject
{
	private final GenericDao<Site> siteDao;
	private final List<Observer> observers = new ArrayList<>();

	public SiteRepository(GenericDao<Site> siteDao)
	{
		this.siteDao = siteDao;
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

	public List<Site> getAllSites()
	{
		try
		{
			siteDao.startTransaction();
			List<Site> sites = siteDao.findAll();
			siteDao.commitTransaction();
			return sites;
		} catch (Exception e)
		{
			siteDao.rollbackTransaction();
			throw e;
		}
	}

	public void addSite(Site site)
	{
		try
		{
			siteDao.startTransaction();
			siteDao.insert(site);
			siteDao.commitTransaction();
			notifyObservers();
		} catch (Exception e)
		{
			siteDao.rollbackTransaction();
			throw e;
		}
	}

	public void updateSite(Site site)
	{
		try
		{
			siteDao.startTransaction();
			siteDao.update(site);
			siteDao.commitTransaction();
			notifyObservers();
		} catch (Exception e)
		{
			siteDao.rollbackTransaction();
			throw e;
		}
	}

	// TODO soft delete ipv hard delete
	public void deleteSite(Site site)
	{
		try
		{
			siteDao.startTransaction();
			siteDao.delete(site);
			siteDao.commitTransaction();
			notifyObservers();
		} catch (Exception e)
		{
			siteDao.rollbackTransaction();
			throw new RuntimeException("Kon site niet verwijderen: " + e.getMessage(), e);
		}
	}

	public List<User> getAllEmployees()
	{
		List<Site> sites = siteDao.findAll();

		return sites.stream().map(site -> site.getVerantwoordelijke()).collect(Collectors.toUnmodifiableList());
	}

	public List<SiteDTO> makeSiteDTOs(List<Site> sites)
	{
		return sites.stream().map(site -> toSiteDTO(site)).collect(Collectors.toUnmodifiableList());
	}

	private SiteDTO toSiteDTO(Site site)
	{
		return new SiteDTO(site.getId(), site.getSiteName(), site.getVerantwoordelijke(),
				makeMachineDTOs(site.getMachines()), site.getStatus(), site.getAddress());
	}

	private Set<MachineDTO> makeMachineDTOs(Set<Machine> machines)
	{
		return machines.stream()
				.map(machine -> new MachineDTO(machine.getId(), toSiteDTO(machine.getSite()), machine.getTechnician(),
						machine.getCode(), machine.getStatus(), machine.getProductieStatus(), machine.getLocation(),
						machine.getProductInfo(), machine.getLastMaintenance(), machine.getFutureMaintenance(),
						machine.getNumberDaysSinceLastMaintenance(), machine.getUpTimeInHours()))
				.collect(Collectors.toUnmodifiableSet());
	}

	public Site makeSiteObject(SiteDTO siteDTO)
	{
		return new Site(siteDTO.siteName(), siteDTO.verantwoordelijke(), siteDTO.status(), siteDTO.address());
	}

}
