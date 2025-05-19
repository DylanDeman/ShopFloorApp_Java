package domain;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.InformationRequiredExceptionMachine;
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
import util.RequiredElementMachine;

@Entity
@ToString
@NoArgsConstructor
@Getter
@Setter
@Table(name = "machines")
public class Machine implements Serializable, Subject
{
	public Machine(Site site, User technician, String code, String location, String productInfo,
			MachineStatus machineStatus, ProductionStatus productionStatus, LocalDate futureMaintenance)
	{
		setSite(site);
		setTechnician(technician);
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
	@Convert(converter = MachineStatusConverter.class)
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

	public static class Builder
	{
		private Site site;
		private User technician;
		private String code;
		private String location;
		private String productInfo;
		private MachineStatus machineStatus;
		private ProductionStatus productionStatus;
		private LocalDate futureMaintenance;

		protected Machine machine;

		public Builder()
		{

		}

		public Builder buildSite(Site site)
		{
			this.site = site;
			return this;
		}

		public Builder buildTechnician(User technician)
		{
			this.technician = technician;
			return this;
		}

		public Builder buildCode(String code)
		{
			this.code = code;
			return this;
		}

		public Builder buildLocation(String location)
		{
			this.location = location;
			return this;
		}

		public Builder buildProductInfo(String productInfo)
		{
			this.productInfo = productInfo;
			return this;
		}

		public Builder buildMachineStatus(MachineStatus machineStatus)
		{
			this.machineStatus = machineStatus;
			return this;
		}

		public Builder buildProductionStatus(ProductionStatus productionStatus)
		{
			this.productionStatus = productionStatus;
			return this;
		}

		public Builder buildFutureMaintenance(LocalDate futureMaintenance)
		{
			this.futureMaintenance = futureMaintenance;
			return this;
		}

		public Machine build() throws InformationRequiredExceptionMachine
		{
			validateRequiredFields();

			machine = new Machine();
			machine.setSite(site);
			machine.setTechnician(technician);
			machine.setCode(code);
			machine.setLocation(location);
			machine.setProductInfo(productInfo);
			machine.setMachineStatus(machineStatus);
			machine.setProductionStatus(productionStatus);
			machine.setFutureMaintenance(futureMaintenance);

			return machine;
		}

		private void validateRequiredFields() throws InformationRequiredExceptionMachine
		{
			Map<String, RequiredElementMachine> requiredElements = new HashMap<>();

			if (machine.getLastMaintenance() == null)
			{
				machine.setLastMaintenance(LocalDate.now());
			}

			if (machine.getSite() == null)
			{
				requiredElements.put("site", RequiredElementMachine.SITE_REQUIRED);
			}

			if (machine.getTechnician() == null)
			{
				requiredElements.put("technician", RequiredElementMachine.TECHNICIAN_REQUIRED);
			}

			if (machine.getCode().isEmpty())
			{
				requiredElements.put("code", RequiredElementMachine.CODE_REQUIRED);
			}

			if (machine.getMachineStatus() == null)
			{
				requiredElements.put("machineStatus", RequiredElementMachine.MACHINESTATUS_REQUIRED);
			}

			if (machine.getProductionStatus() == null)
			{
				requiredElements.put("productionStatus", RequiredElementMachine.PRODUCTIONSTATUS_REQUIRED);
			}

			if (machine.getLocation().isEmpty())
			{
				requiredElements.put("location", RequiredElementMachine.LOCATION_REQUIRED);
			}

			if (machine.getProductInfo().isEmpty())
			{
				requiredElements.put("productInfo", RequiredElementMachine.PRODUCTINFO_REQUIRED);
			}

			if (machine.getFutureMaintenance() == null)
			{
				requiredElements.put("futureMaintenance", RequiredElementMachine.FUTURE_MAINTENANCE_REQUIRED);
			}

			if (!requiredElements.isEmpty())
			{
				throw new InformationRequiredExceptionMachine(requiredElements);
			}

		}
	}
}