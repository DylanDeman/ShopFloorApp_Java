package domain.machine;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import domain.site.Site;
import domain.site.SiteDTO;

public class MachineController
{

	private MachineDao machineRepo;
	private List<Machine> machineList;

	public MachineController()
	{
		machineRepo = new MachineDaoJpa();
	}

	public List<MachineDTO> getMachineList()
	{
		List<Machine> machines = machineRepo.findAll();
		if (machines == null)
		{
			return List.of();
		}
		return makeMachineDTOs(machines);
	}

	public List<MachineDTO> makeMachineDTOs(List<Machine> machines)
	{
		return machines.stream().map(this::convertToMachineDTO) // Use a helper method to convert Machine to MachineDTO
				.collect(Collectors.toUnmodifiableList()); // Collect into a list
	}

	// Convert a Machine to MachineDTO
	private MachineDTO convertToMachineDTO(Machine machine)
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

	public void addNewMachine(Machine machine)
	{
		machineRepo.startTransaction();
		machineRepo.insert(machine);
		machineRepo.commitTransaction();
	}

	public void updateMachine(Machine machine) {
	    System.out.println("Updating machine with IDd: " + machine.getId());
	    machineRepo.startTransaction();
	    machineRepo.update(machine); // Assuming update is properly implemented
	    machineRepo.commitTransaction();
	    System.out.println("Machine update committed.");
	}

	public void addNewMachine(MachineDTO machineDTO)
	{
		Machine machine = convertDTOToMachine(machineDTO);
		addNewMachine(machine); 
	}

	public Machine convertDTOToMachine(MachineDTO dto)
	{
		Machine machine = new Machine();

		// Convert SiteDTO to Site before setting it
		machine.setId(dto.id()); // Make sure to set the ID
		Site site = convertDTOToSite(dto.site());
		machine.setSite(site);
		machine.setTechnician(dto.technician());
		machine.setProductInfo(dto.productInfo());
		machine.setLastMaintenance(dto.lastMaintenance());
		machine.setNumberDaysSinceLastMaintenance(dto.numberDaysSinceLastMaintenance());
		// machine.setUpTimeInHours(dto.upTimeInHours());
		machine.setCode(dto.code());
		machine.setLocation(dto.location());
		machine.setMachineStatus(dto.machineStatus());
		machine.setProductionStatus(dto.productionStatus());
		machine.setFutureMaintenance(dto.futureMaintenance());

		return machine;
	}

	public Site convertDTOToSite(SiteDTO dto)
	{
		Site site = new Site();

		site.setId(dto.id());
		site.setSiteName(dto.siteName());
		site.setVerantwoordelijke(dto.verantwoordelijke());
		dto.machines().forEach(machineDTO -> {
			Machine machine = convertDTOToMachine(machineDTO);
			site.addMachine(machine);
		});

		site.setStatus(dto.status());

		return site;
	}

}
