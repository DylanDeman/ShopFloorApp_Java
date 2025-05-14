package domain.site;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import domain.Observer;
import domain.Subject;
import domain.notifications.NotificationObserver;
import domain.user.User;
import dto.SiteDTOWithMachines;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionSite;
import gui.AppServices;
import util.DTOMapper;
import util.Status;

public class SiteController implements Subject{
	private SiteDao siteRepo;
	private List<Observer> observers = new ArrayList<>();

	public SiteController() {
		siteRepo = new SiteDaoJpa();
		addObserver(new NotificationObserver());
	}

	public SiteDTOWithMachines getSite(int id) {
		Site site = siteRepo.get(id);
		return DTOMapper.toSiteDTOWithMachines(site);
	}

	public List<SiteDTOWithMachines> getSites() {
		List<Site> sites = siteRepo.findAll();
		return DTOMapper.toSiteDTOsWithMachines(sites);
	}

	public List<Site> getSiteObjects() {
		return siteRepo.findAll();
	}

	public Site getSiteObject(int siteId) {
		return siteRepo.get(siteId);
	}

	public List<SiteDTOWithMachines> getFilteredSites(String searchFilter, String statusFilter, String siteNameFilter,
			String verantwoordelijkeFilter, Integer minMachinesFilter, Integer maxMachinesFilter) {

		String lowerCaseSearchFilter = searchFilter == null ? "" : searchFilter.toLowerCase();

		// Set default values for min and max filters if null
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

	public List<String> getAllStatusses() {
		List<SiteDTOWithMachines> allSites = getSites();
		return allSites.stream().map(s -> s.status().toString()).distinct().sorted().collect(Collectors.toList());
	}

	public List<String> getAllSiteNames() {
		List<SiteDTOWithMachines> allSites = getSites();
		return allSites.stream().map(SiteDTOWithMachines::siteName).distinct().sorted().collect(Collectors.toList());
	}

	public List<String> getAllVerantwoordelijken() {
		List<SiteDTOWithMachines> allSites = getSites();
		return allSites.stream().filter(s -> s.verantwoordelijke() != null)
				.map(s -> s.verantwoordelijke().firstName() + " " + s.verantwoordelijke().lastName()).distinct()
				.sorted().collect(Collectors.toList());
	}

	public List<SiteDTOWithoutMachines> getSitesWithoutMachines() {
		List<Site> sites = siteRepo.findAll();
		if (sites == null) {
			return new ArrayList<>();
		}
		return DTOMapper.toSiteDTOsWithoutMachines(sites);
	}

	public SiteDTOWithMachines createSite(String siteName, String street, String houseNumber, String postalCode,
			String city, String employeeFullName) throws InformationRequiredExceptionSite, NumberFormatException {

		UserDTO employee = AppServices.getInstance().getUserController().getAllVerantwoordelijken().stream()
				.filter(user -> (user.firstName() + " " + user.lastName()).equals(employeeFullName)).findFirst()
				.orElse(null);

		int houseNumberInt = Integer.parseInt(houseNumber);
		int postalCodeInt = Integer.parseInt(postalCode);
		
		User employeeObject = DTOMapper.toUser(employee, null);
		
		SiteBuilder siteBuilder = new SiteBuilder();
		siteBuilder.createSite();
		siteBuilder.buildName(siteName);
		siteBuilder.createAddress();
		siteBuilder.buildStreet(street);
		siteBuilder.buildNumber(houseNumberInt);
		siteBuilder.buildPostalcode(postalCodeInt);
		siteBuilder.buildCity(city);
		siteBuilder.buildEmployee(employeeObject);
		siteBuilder.buildStatus(Status.ACTIEF); // New sites are active by default

		Site newSite = siteBuilder.getSite();

		siteRepo.startTransaction();
		siteRepo.insert(newSite);
		siteRepo.commitTransaction();
		
		notifyObservers("Site aangemaakt " + newSite.getId() + " " + newSite.getSiteName());

		return DTOMapper.toSiteDTOWithMachines(newSite);
	}

	public SiteDTOWithMachines updateSite(int siteId, String siteName, String street, String houseNumber,
			String postalCode, String city, String employeeFullName, Status status)
			throws InformationRequiredExceptionSite, NumberFormatException {

		Site existingSite = siteRepo.get(siteId);
		if (existingSite == null) {
			throw new IllegalArgumentException("Site with ID " + siteId + " not found");
		}

		UserDTO employee = AppServices.getInstance().getUserController().getAllVerantwoordelijken().stream()
				.filter(user -> (user.firstName() + " " + user.lastName()).equals(employeeFullName)).findFirst()
				.orElse(null);

		int houseNumberInt = Integer.parseInt(houseNumber);
		int postalCodeInt = Integer.parseInt(postalCode);
		
		User employeeObject = DTOMapper.toUser(employee, null);
		
		
		SiteBuilder siteBuilder = new SiteBuilder();
		siteBuilder.createSite();
		siteBuilder.buildName(siteName);
		siteBuilder.createAddress();
		siteBuilder.buildStreet(street);
		siteBuilder.buildNumber(houseNumberInt);
		siteBuilder.buildPostalcode(postalCodeInt);
		siteBuilder.buildCity(city);
		siteBuilder.buildEmployee(employeeObject);
		siteBuilder.buildStatus(status);

		Site updatedSite = siteBuilder.getSite();
		updatedSite.setId(existingSite.getId());
		updatedSite.getAddress().setId(existingSite.getAddress().getId());

		siteRepo.startTransaction();
		siteRepo.update(updatedSite);
		siteRepo.commitTransaction();
		
		notifyObservers("Site bijgewerkt " + updatedSite.getId() + " " + updatedSite.getSiteName());
		

		return DTOMapper.toSiteDTOWithMachines(updatedSite);
	}

	@Override
	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
		
	}

	@Override
	public void notifyObservers(String message) {
		for (Observer o : observers)
			o.update(message);
		
	}
}