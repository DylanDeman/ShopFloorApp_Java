package domain.machine;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import domain.site.Site;
import domain.user.User;
import exceptions.InformationRequiredExceptionMachine;
import util.MachineStatus;
import util.ProductionStatus;
import util.RequiredElementMachine;

public class MachineBuilder
{

	private Machine machine;
	private Site site;
	private User technician;

	private Map<String, RequiredElementMachine> requiredElements;

	public void createMachine()
	{
		machine = new Machine();
	}

	public void buildSite(Site site)
	{
		// TODO validatie nog toevoegen
		machine.setSite(site);
	}

	public void buildTechnician(User technician)
	{
		// TODO validatie nog toevoegen
		machine.setTechnician(technician);
	}

	public void buildCode(String code)
	{
		// TODO validatie nog toevoegen
		machine.setCode(code);
	}

	public void buildStatusses(MachineStatus machineStatus, ProductionStatus productionStatus)
	{
		machine.setMachineStatus(machineStatus);
		machine.setProductionStatus(productionStatus);
	}

	public void buildLocation(String location)
	{
		// TODO validatie nog toevoegen
		machine.setLocation(location);
	}

	public void buildProductInfo(String productInfo)
	{
		// TODO validatie nog toevoegen
		machine.setProductInfo(productInfo);
	}

	public void buildMaintenance(LocalDate futureMaintenance)
	{
		machine.setFutureMaintenance(futureMaintenance);
	}

	public Machine getMachine() throws InformationRequiredExceptionMachine
	{
		requiredElements = new HashMap<>();

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

		return this.machine;

	}

}