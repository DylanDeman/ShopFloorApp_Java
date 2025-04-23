package domain.machine;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import domain.site.Site;
import domain.site.SiteDTO;
import exceptions.InvalidMachineException;
import domain.machine.MachineDTO;
import repository.GenericDao;

public class MachineController {

	private MachineDao machineRepo;
	private List<Machine> machineList;

	public MachineController() {
		machineRepo = new MachineDaoJpa();
	}

	public List<MachineDTO> getMachineList() {
	    List<Machine> machines = machineRepo.findAll();
	    if (machines == null) {
	        return List.of();
	    }
	    return makeMachineDTOs(machines);
	}


	public List<MachineDTO> makeMachineDTOs(List<Machine> machines) {
	    return machines.stream()
	            .map(this::convertToMachineDTO) // Use a helper method to convert Machine to MachineDTO
	            .collect(Collectors.toUnmodifiableList()); // Collect into a list
	}

	// Convert a Machine to MachineDTO
	private MachineDTO convertToMachineDTO(Machine machine) {
	    SiteDTO siteDTO = convertToSiteDTO(machine.getSite()); // Convert the Site object to a SiteDTO
	    return new MachineDTO(
	            machine.getId(),
	            siteDTO,
	            machine.getTechnician(),
	            machine.getCode(),
	            machine.getStatus(),
	            machine.getProductieStatus(),
	            machine.getLocation(),
	            machine.getProductInfo(),
	            machine.getLastMaintenance(),
	            machine.getFutureMaintenance(),
	            machine.getNumberDaysSinceLastMaintenance(),
	            machine.getUpTimeInHours()
	    );
	}

	private SiteDTO convertToSiteDTO(Site site) {
	    return new SiteDTO(
	            site.getId(),
	            site.getSiteName(),
	            site.getVerantwoordelijke(),  // Assuming this is a User object
	            convertMachinesToMachineDTOs(site.getMachines()), // If Site has a set of Machines, convert them to MachineDTOs
	            site.getStatus()
	    );
	}

	private Set<MachineDTO> convertMachinesToMachineDTOs(Set<Machine> machines) {
	    return machines.stream()
	            .map(this::convertToMachineDTO)
	            .collect(Collectors.toSet());
	}


	public void addNewMachine(Machine machine) {
		machineRepo.startTransaction();
		machineRepo.insert(machine);
		machineRepo.commitTransaction();
	}

	public void updateMachine(Machine machine) {
		machineRepo.startTransaction();
		machineRepo.update(machine);
		machineRepo.commitTransaction();
	}
	



	


}
