package domain.site;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import domain.Address;
import domain.machine.Machine;
import domain.user.User;
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
import util.Status;

@Getter
@Table(name = "sites")
@Entity
@NoArgsConstructor
public class Site implements Serializable, Subject {
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

	public Site(String siteName, User verantwoordelijke, Status status) {
		this.siteName = siteName;
		this.verantwoordelijke = verantwoordelijke;
		this.status = status;
	}

	public Site(String siteName, User verantwoordelijke, Status status, Address address) {
		this.siteName = siteName;
		this.verantwoordelijke = verantwoordelijke;
		this.status = status;
		this.address = address;
	}

	public Site(int id, String siteName, User verantwoordelijke, Status status, Address address) {
		this.id = id;
		this.siteName = siteName;
		this.verantwoordelijke = verantwoordelijke;
		this.status = status;
		this.address = address;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName.trim();
		notifyObservers();
	}

	public void setVerantwoordelijke(User verantwoordelijke) {
		this.verantwoordelijke = verantwoordelijke;
		notifyObservers();
	}

	public void setStatus(Status status) {
		this.status = status;
		notifyObservers();
	}
	
	public void addMachine(Machine machine) {
	    machines.add(machine);
	    if (machine.getSite() != this) {
	        machine.setSite(this);
	    }
	}

	@Override
	public void addObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers() {
		observers.forEach(observer -> {
			observer.update();
		});
	}
	
}