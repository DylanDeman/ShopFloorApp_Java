package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dto.SiteDTOWithMachines;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionSite;
import gui.AppServices;
import interfaces.Observer;
import interfaces.Subject;
import repository.GenericDaoJpa;
import repository.UserDao;
import repository.UserDaoJpa;
import util.DTOMapper;
import util.Status;

/**
 * Controller class for managing site operations and business logic. Implements
 * the Subject interface for observer pattern functionality. Handles CRUD
 * operations for sites and provides various filtering capabilities.
 */
public class SiteController implements Subject
{
	private GenericDaoJpa<Site> siteRepo;
	private UserDao userDao;
	private List<Observer> observers = new ArrayList<>();

	/**
	 * Constructs a new SiteController with default repository implementations.
	 * Automatically adds a NotificationObserver to observe changes.
	 */
	public SiteController()
	{
		userDao = new UserDaoJpa();
		siteRepo = new GenericDaoJpa<Site>(Site.class);
		addObserver(new NotificationObserver());
	}

	/**
	 * Retrieves a site by its ID and converts it to DTO with machines.
	 * 
	 * @param id the ID of the site to retrieve
	 * @return SiteDTOWithMachines containing site details and its machines
	 */
	public SiteDTOWithMachines getSite(int id)
	{
		Site site = siteRepo.get(id);
		return DTOMapper.toSiteDTOWithMachines(site);
	}

	/**
	 * Retrieves all sites with their machines as DTOs.
	 * 
	 * @return List of SiteDTOWithMachines containing all sites
	 */
	public List<SiteDTOWithMachines> getSites()
	{
		List<Site> sites = siteRepo.findAll();
		return DTOMapper.toSiteDTOsWithMachines(sites);
	}

	/**
	 * Retrieves all site domain objects.
	 * 
	 * @return List of Site domain objects
	 */
	public List<Site> getSiteObjects()
	{
		return siteRepo.findAll();
	}

	/**
	 * Retrieves a site domain object by ID.
	 * 
	 * @param siteId the ID of the site to retrieve
	 * @return Site domain object
	 */
	public Site getSiteObject(int siteId)
	{
		return siteRepo.get(siteId);
	}

	/**
	 * Retrieves filtered sites based on multiple criteria.
	 * 
	 * @param searchFilter            general search term to filter by
	 * @param statusFilter            status to filter by
	 * @param siteNameFilter          site name to filter by
	 * @param verantwoordelijkeFilter responsible person to filter by
	 * @param minMachinesFilter       minimum number of machines required
	 * @param maxMachinesFilter       maximum number of machines allowed
	 * @return List of filtered SiteDTOWithMachines
	 */
	public List<SiteDTOWithMachines> getFilteredSites(String searchFilter, String statusFilter, String siteNameFilter,
			String verantwoordelijkeFilter, Integer minMachinesFilter, Integer maxMachinesFilter)
	{

		String lowerCaseSearchFilter = searchFilter == null ? "" : searchFilter.toLowerCase();

		int minMachines = minMachinesFilter != null ? minMachinesFilter : 0;
		int maxMachines = maxMachinesFilter != null ? maxMachinesFilter : Integer.MAX_VALUE;

		return getSites().stream().filter(site -> statusFilter == null || site.status().toString().equals(statusFilter))
				.filter(site -> siteNameFilter == null
						|| site.siteName().toLowerCase().contains(siteNameFilter.toLowerCase()))
				.filter(site -> verantwoordelijkeFilter == null || (site.verantwoordelijke() != null
						&& (site.verantwoordelijke().firstName() + " " + site.verantwoordelijke().lastName())
								.equals(verantwoordelijkeFilter)))
				.filter(site -> site.machines().size() >= minMachines && site.machines().size() <= maxMachines)
				.filter(site -> site.siteName().toLowerCase().contains(lowerCaseSearchFilter)
						|| (site.verantwoordelijke() != null && (site.verantwoordelijke().firstName().toLowerCase()
								.contains(lowerCaseSearchFilter)
								|| site.verantwoordelijke().lastName().toLowerCase().contains(lowerCaseSearchFilter)))
						|| site.status().toString().toLowerCase().contains(lowerCaseSearchFilter))
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves all distinct status values from sites.
	 * 
	 * @return List of unique status strings
	 */
	public List<String> getAllStatusses()
	{
		List<SiteDTOWithMachines> allSites = getSites();
		return allSites.stream().map(s -> s.status().toString()).distinct().sorted().collect(Collectors.toList());
	}

	/**
	 * Retrieves all distinct site names.
	 * 
	 * @return List of unique site names
	 */
	public List<String> getAllSiteNames()
	{
		List<SiteDTOWithMachines> allSites = getSites();
		return allSites.stream().map(SiteDTOWithMachines::siteName).distinct().sorted().collect(Collectors.toList());
	}

	/**
	 * Retrieves all distinct responsible persons from sites.
	 * 
	 * @return List of unique responsible person names in "firstName lastName"
	 *         format
	 */
	public List<String> getAllVerantwoordelijken()
	{
		List<SiteDTOWithMachines> allSites = getSites();
		return allSites.stream().filter(s -> s.verantwoordelijke() != null)
				.map(s -> s.verantwoordelijke().firstName() + " " + s.verantwoordelijke().lastName()).distinct()
				.sorted().collect(Collectors.toList());
	}

	/**
	 * Retrieves all sites without machine details as DTOs.
	 * 
	 * @return List of SiteDTOWithoutMachines
	 */
	public List<SiteDTOWithoutMachines> getSitesWithoutMachines()
	{
		List<Site> sites = siteRepo.findAll();
		if (sites == null)
		{
			return new ArrayList<>();
		}
		return DTOMapper.toSiteDTOsWithoutMachines(sites);
	}

	/**
	 * Creates a new site with the specified details.
	 * 
	 * @param siteName         the name of the new site
	 * @param street           the street of the site address
	 * @param houseNumber      the house number of the site address
	 * @param postalCode       the postal code of the site address
	 * @param city             the city of the site address
	 * @param employeeFullName the full name of the responsible employee
	 * @return SiteDTOWithMachines representing the created site
	 * @throws InformationRequiredExceptionSite if required fields are missing
	 * @throws NumberFormatException            if houseNumber or postalCode are not
	 *                                          valid numbers
	 */
	public SiteDTOWithMachines createSite(String siteName, String street, String houseNumber, String postalCode,
			String city, String employeeFullName) throws InformationRequiredExceptionSite, NumberFormatException
	{

		UserDTO employee = AppServices.getInstance().getUserController().getAllVerantwoordelijken().stream()
				.filter(user -> (user.firstName() + " " + user.lastName()).equals(employeeFullName)).findFirst()
				.orElse(null);

		int houseNumberInt = Integer.parseInt(houseNumber);
		int postalCodeInt = Integer.parseInt(postalCode);

		Address address = new Address();
		address.setStreet(street);
		address.setNumber(houseNumberInt);
		address.setPostalcode(postalCodeInt);
		address.setCity(city);

		User existingUser = userDao.getByEmail(employee.email());
		User employeeObject = DTOMapper.toUser(employee, existingUser);

		Site newSite = new Site.Builder().buildSiteName(siteName).buildAddress(address)
				.buildVerantwoordelijke(employeeObject).buildStatus(Status.ACTIEF).build();

		siteRepo.startTransaction();
		siteRepo.insert(newSite);
		siteRepo.commitTransaction();

		notifyObservers("Site aangemaakt " + newSite.getId() + " " + newSite.getSiteName());

		return DTOMapper.toSiteDTOWithMachines(newSite);
	}

	/**
	 * Updates an existing site with new details.
	 * 
	 * @param siteId           the ID of the site to update
	 * @param siteName         the new name for the site
	 * @param street           the new street for the address
	 * @param houseNumber      the new house number for the address
	 * @param postalCode       the new postal code for the address
	 * @param city             the new city for the address
	 * @param employeeFullName the new responsible employee
	 * @param status           the new status for the site
	 * @return SiteDTOWithMachines representing the updated site
	 * @throws InformationRequiredExceptionSite if required fields are missing
	 * @throws NumberFormatException            if houseNumber or postalCode are not
	 *                                          valid numbers
	 * @throws IllegalArgumentException         if site with given ID is not found
	 */
	public SiteDTOWithMachines updateSite(int siteId, String siteName, String street, String houseNumber,
			String postalCode, String city, String employeeFullName, Status status)
			throws InformationRequiredExceptionSite, NumberFormatException
	{

		Site existingSite = siteRepo.get(siteId);
		if (existingSite == null)
		{
			throw new IllegalArgumentException("Site with ID " + siteId + " not found");
		}

		UserDTO employee = AppServices.getInstance().getUserController().getAllVerantwoordelijken().stream()
				.filter(user -> (user.firstName() + " " + user.lastName()).equals(employeeFullName)).findFirst()
				.orElse(null);

		int houseNumberInt = Integer.parseInt(houseNumber);
		int postalCodeInt = Integer.parseInt(postalCode);

		Address address = new Address();
		address.setStreet(street);
		address.setNumber(houseNumberInt);
		address.setPostalcode(postalCodeInt);
		address.setCity(city);

		if (existingSite.getAddress() != null)
		{
			address.setId(existingSite.getAddress().getId());
		}

		User employeeObject = DTOMapper.toUser(employee, null);

		Site updatedSite = new Site.Builder().buildSiteName(siteName).buildAddress(address)
				.buildVerantwoordelijke(employeeObject).buildStatus(Status.ACTIEF).build();

		updatedSite.setId(existingSite.getId());
		updatedSite.getAddress().setId(existingSite.getAddress().getId());

		siteRepo.startTransaction();
		siteRepo.update(updatedSite);
		siteRepo.commitTransaction();

		notifyObservers("Site bijgewerkt " + updatedSite.getId() + " " + updatedSite.getSiteName());

		return DTOMapper.toSiteDTOWithMachines(updatedSite);
	}

	@Override
	public void addObserver(Observer observer)
	{
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer)
	{
		observers.remove(observer);
	}

	@Override
	public void notifyObservers(String message)
	{
		observers.forEach(o -> o.update(message));
	}
}