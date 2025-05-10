package domain.site;

import java.util.HashMap;
import java.util.Map;

import domain.Address;
import domain.user.User;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionSite;
import util.DTOMapper;
import util.RequiredElementSite;
import util.Status;

public class SiteBuilder {
	private Site site;
	private Address address;

	private Map<String, RequiredElementSite> requiredElements;

	public void createSite() {
		site = new Site();
	}

	public void buildName(String siteName) {
		// validatie nog toevoegen
		site.setSiteName(siteName);
	}

	public void createAddress() {
		address = new Address();
	}

	public void buildStreet(String street) {
		// TODO validatie nog toevoegen
		address.setStreet(street);
	}

	public void buildNumber(int number) {
		// TODO validatie nog toevoegen
		address.setNumber(number);
	}

	public void buildPostalcode(int postalcode) {
		// TODO validatie nog toevoegen
		address.setPostalcode(postalcode);
	}

	public void buildCity(String city) {
		// TODO validatie nog toevoegen
		address.setCity(city);
	}

	public void buildEmployee(User employee) {
		site.setVerantwoordelijke(employee);
	}

	public void buildStatus(Status status) {
		site.setStatus(status);
	}

	public Site getSite() throws InformationRequiredExceptionSite {
		requiredElements = new HashMap<>();

		if (site.getSiteName().isEmpty()) {
			requiredElements.put("siteName", RequiredElementSite.SITE_NAME_REQUIRED);
		}

		if (address.getStreet().isEmpty()) {
			requiredElements.put("street", RequiredElementSite.STREET_REQUIRED);
		}

		if (address.getNumber() == 0) {
			requiredElements.put("number", RequiredElementSite.NUMBER_REQUIRED);
		}

		if (address.getPostalcode() == 0) {
			requiredElements.put("postalCode", RequiredElementSite.POSTAL_CODE_REQUIRED);
		}

		if (address.getCity().isEmpty()) {
			requiredElements.put("city", RequiredElementSite.CITY_REQUIRED);
		}

		if (site.getVerantwoordelijke() == null) {
			requiredElements.put("employee", RequiredElementSite.EMPLOYEE_REQUIRED);
		}

		if (site.getStatus() == null) {
			requiredElements.put("status", RequiredElementSite.STATUS_REQUIRED);
		}

		if (!requiredElements.isEmpty())
			throw new InformationRequiredExceptionSite(requiredElements);

		site.setAddress(address);

		return this.site;
	}
}
