package domain.maintenance;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import domain.machine.Machine;
import domain.machine.MachineDTO;
import domain.site.Site;
import domain.site.SiteDTO;

public class MaintenanceController
{
	private MaintenanceDao maintenanceRepo;

	public MaintenanceController()
	{
		maintenanceRepo = new MaintenanceDaoJpa();
	}

	protected MaintenanceDao getMaintenanceDao() {
		return maintenanceRepo;
	}

	public List<MaintenanceDTO> getMaintenances()
	{
		List<Maintenance> sites = maintenanceRepo.findAll();
		return makeMaintenanceDTOs(sites);
	}

	public List<MaintenanceDTO> makeMaintenanceDTOs(List<Maintenance> maintenances)
	{
		return maintenances.stream().map(maintenance -> {
			return new MaintenanceDTO(maintenance.getId(), maintenance.getExecutionDate(), maintenance.getStartDate(),
					maintenance.getEndDate(), maintenance.getTechnician(), maintenance.getReason(),
					maintenance.getComments(), maintenance.getStatus(), convertToMachineDTO(maintenance.getMachine()));
		}).collect(Collectors.toUnmodifiableList());

	}

	public Maintenance getMaintenance(int id)
	{
		return maintenanceRepo.get(id);
	}

	public MachineDTO convertToMachineDTO(Machine machine)
	{
		SiteDTO siteDTO = convertToSiteDTO(machine.getSite()); // Convert the Site object to a SiteDTO
		return new MachineDTO(machine.getId(), siteDTO, machine.getTechnician(), machine.getCode(),
				machine.getMachineStatus(), machine.getProductionStatus(), machine.getLocation(),
				machine.getProductInfo(), machine.getLastMaintenance(), machine.getFutureMaintenance(),
				machine.getNumberDaysSinceLastMaintenance(), machine.getUpTimeInHours());
	}

	private SiteDTO convertToSiteDTO(Site site)
	{
		return new SiteDTO(site.getId(), site.getSiteName(), site.getVerantwoordelijke(), // Assuming this is a User
																							// object
				convertMachinesToMachineDTOs(site.getMachines()), // If Site has a set of Machines, convert them to
																	// MachineDTOs
				site.getStatus(), site.getAddress());
	}

	private Set<MachineDTO> convertMachinesToMachineDTOs(Set<Machine> machines)
	{
		return machines.stream().map(this::convertToMachineDTO).collect(Collectors.toSet());
	}

}
