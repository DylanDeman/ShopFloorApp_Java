package domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import exceptions.InformationRequiredExceptionSite;
import interfaces.Observer;
import interfaces.Subject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.RequiredElementSite;
import util.Status;

@Getter
@Table(name = "sites")
@Entity
@NoArgsConstructor
public class Site implements Serializable, Subject
{
	private static final long serialVersionUID = 1L;

	@Transient
	private Set<Observer> observers = new HashSet<>();

	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String siteName;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "VERANTWOORDELIJKE_ID")
	private User verantwoordelijke;

	@Setter
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "ADDRESS_ID")
	private Address address;

	@OneToMany(mappedBy = "site", fetch = FetchType.EAGER)
	private Set<Machine> machines = new HashSet<>();

	@Enumerated(EnumType.STRING)
	private Status status;

	public Site(String siteName, User verantwoordelijke, Status status)
	{
		this.siteName = siteName;
		this.verantwoordelijke = verantwoordelijke;
		this.status = status;
	}

	public Site(String siteName, User verantwoordelijke, Status status, Address address)
	{
		this.siteName = siteName;
		this.verantwoordelijke = verantwoordelijke;
		this.status = status;
		this.address = address;
	}

	public Site(int id, String siteName, User verantwoordelijke, Status status, Address address)
	{
		this.id = id;
		this.siteName = siteName;
		this.verantwoordelijke = verantwoordelijke;
		this.status = status;
		this.address = address;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName.trim();
		notifyObservers("");
	}

	public void setVerantwoordelijke(User verantwoordelijke)
	{
		this.verantwoordelijke = verantwoordelijke;
		notifyObservers("");
	}

	public void setStatus(Status status)
	{
		this.status = status;
		notifyObservers("");
	}

	public void addMachine(Machine machine)
	{
		machines.add(machine);
		if (machine.getSite() != this)
		{
			machine.setSite(this);
		}
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
	public void notifyObservers(String message)
	{
		observers.forEach(observer -> {
			observer.update(message);
		});
	}

	public static class Builder
	{
		private String siteName;
		private Address address;
		private User verantwoordelijke;
		private Status status;

		public Builder()
		{

		}

		protected Site site;

		public Builder buildSiteName(String siteName)
		{
			this.siteName = siteName;
			return this;
		}

		public Builder buildAddress(Address address)
		{
			this.address = address;
			return this;
		}

		public Builder buildVerantwoordelijke(User verantwoordelijke)
		{
			this.verantwoordelijke = verantwoordelijke;
			return this;
		}

		public Builder buildStatus(Status status)
		{
			this.status = status;
			return this;
		}

		public Site build() throws InformationRequiredExceptionSite
		{
			validateRequiredFields();

			site = new Site();
			site.setSiteName(siteName);
			site.setAddress(address);
			site.setVerantwoordelijke(verantwoordelijke);
			site.setStatus(status);

			return site;
		}

		private void validateRequiredFields() throws InformationRequiredExceptionSite
		{
			Map<String, RequiredElementSite> requiredElements = new HashMap<>();

			if (siteName.isEmpty())
			{
				requiredElements.put("siteName", RequiredElementSite.SITE_NAME_REQUIRED);
			}

			if (address == null || address.getStreet().isEmpty())
			{
				requiredElements.put("street", RequiredElementSite.STREET_REQUIRED);
			}

			if (address == null || address.getNumber() == 0)
			{
				requiredElements.put("number", RequiredElementSite.NUMBER_REQUIRED);
			}

			if (address == null || address.getPostalcode() == 0)
			{
				requiredElements.put("postalCode", RequiredElementSite.POSTAL_CODE_REQUIRED);
			}

			if (address == null || address.getCity().isEmpty())
			{
				requiredElements.put("city", RequiredElementSite.CITY_REQUIRED);
			}

			if (verantwoordelijke == null)
			{
				requiredElements.put("employee", RequiredElementSite.EMPLOYEE_REQUIRED);
			}

			if (status == null)
			{
				requiredElements.put("status", RequiredElementSite.STATUS_REQUIRED);
			}

			if (!requiredElements.isEmpty())
				throw new InformationRequiredExceptionSite(requiredElements);
		}
	}

}