package domain;

import java.util.List;

import repository.GenericDao;

public class MachineController {
	
	
	private GenericDao<Machine> machineRepo;
	private List<Machine> machineList;

	
	public MachineController(GenericDao<Machine> machineDao) {
		machineRepo = machineDao;
	}
	
	
	public List<Machine> getMachineList(){
		if(machineList == null) {
			machineList = machineRepo.findAll();
		}
		
		return machineList;
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
