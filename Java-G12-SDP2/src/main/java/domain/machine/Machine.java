package domain.machine;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import domain.site.Site;
import domain.user.User;
import interfaces.Observer;
import interfaces.Subject;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import util.MachineStatus;
import util.MachineStatusConverter;
import util.ProductionStatus;

@Entity
@ToString
@NoArgsConstructor
@Getter
@Setter
@Table(name = "machines")
public class Machine implements Serializable, Subject
{
	public Machine(Site site, User tecnician, String code, String location, String productInfo,
			MachineStatus machineStatus, ProductionStatus productionStatus, LocalDate futureMaintenance)
	{
		setSite(site);
		setTechnician(tecnician);
		setCode(code);
		setLocation(location);
		setProductInfo(productInfo);
		setMachineStatus(machineStatus);
		setProductionStatus(productionStatus);
		setFutureMaintenance(futureMaintenance);
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "SITE_ID")
	private Site site;

	@ManyToOne
	private User technician;

	@Transient
	private List<Observer> observers;

	private String code, location, productInfo;

	@Column(name = "MACHINESTATUS")
	@Convert(converter = MachineStatusConverter.class) // Activeer de converter
	private MachineStatus machineStatus;
	private ProductionStatus productionStatus;

	private LocalDate lastMaintenance, futureMaintenance;
	private int numberDaysSinceLastMaintenance;

	public double getUpTimeInHours()
	{
		if (lastMaintenance == null)
		{
			return 0.0;
		}

		LocalDateTime maintenanceDateTime = lastMaintenance.atStartOfDay();
		double hours = Duration.between(maintenanceDateTime, LocalDateTime.now()).toHours();
		return hours;
	}

	public void setSite(Site site)
	{
		if (this.site != null)
		{
			this.site.getMachines().remove(this);
		}
		this.site = site;
		if (site != null)
		{
			site.getMachines().add(this);
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
		observers.stream().forEach((o) -> o.update(message));
	}
}