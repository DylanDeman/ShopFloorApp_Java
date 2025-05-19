package domain.maintenance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import domain.Machine;
import domain.MachineController;
import domain.User;
import domain.UserController;
import dto.MachineDTO;
import dto.MaintenanceDTO;
import dto.SiteDTOWithoutMachines;
import exceptions.InformationRequiredExceptionMaintenance;
import gui.AppServices;
import repository.GenericDaoJpa;
import util.DTOMapper;
import util.MaintenanceStatus;

public class MaintenanceController
{
	private GenericDaoJpa<Maintenance> maintenanceRepo;

	public MaintenanceController()
	{
		maintenanceRepo = new GenericDaoJpa<Maintenance>(Maintenance.class);
	}

	protected GenericDaoJpa<Maintenance> getMaintenanceDao()
	{
		return maintenanceRepo;
	}

	public List<MaintenanceDTO> getMaintenances()
	{
		List<Maintenance> maintenances = maintenanceRepo.findAll();
		return makeMaintenanceDTOs(maintenances);
	}

	public List<MaintenanceDTO> makeMaintenanceDTOs(List<Maintenance> maintenances)
	{
		if (maintenances == null)
		{
			return List.of();
		}

		return maintenances.stream().map(this::makeMaintenanceDTO).collect(Collectors.toUnmodifiableList());
	}

	public MaintenanceDTO makeMaintenanceDTO(Maintenance maintenance)
	{
		if (maintenance == null)
		{
			return null;
		}

		MachineDTO machineDTO = null;
		if (maintenance.getMachine() != null)
		{
			SiteDTOWithoutMachines siteDTO = DTOMapper.toSiteDTOWithoutMachines(maintenance.getMachine().getSite());
			machineDTO = DTOMapper.toMachineDTO(maintenance.getMachine(), siteDTO);
		}

		return new MaintenanceDTO(maintenance.getId(), maintenance.getExecutionDate(), maintenance.getStartDate(),
				maintenance.getEndDate(), DTOMapper.toUserDTO(maintenance.getTechnician()), maintenance.getReason(),
				maintenance.getComments(), maintenance.getStatus(), machineDTO);
	}

	public Maintenance getMaintenance(int id)
	{
		return maintenanceRepo.get(id);
	}

	public MaintenanceDTO getMaintenanceDTO(int id)
	{
		Maintenance maintenance = getMaintenance(id);
		return makeMaintenanceDTO(maintenance);
	}

	public void createMaintenance(Maintenance maintenance)
	{
		maintenanceRepo.startTransaction();
		maintenanceRepo.insert(maintenance);
		maintenanceRepo.commitTransaction();
	}

	public MaintenanceDTO createMaintenance(LocalDate executionDate, LocalDateTime startDate, LocalDateTime endDate,
			int technicianId, String reason, String comments, MaintenanceStatus status, int machineId)
			throws InformationRequiredExceptionMaintenance
	{

		MaintenanceBuilder builder = new MaintenanceBuilder();
		builder.createMaintenance();
		builder.buildExecutionDate(executionDate);
		builder.buildStartDate(startDate);
		builder.buildEndDate(endDate);

		User technician = getUserById(technicianId);
		Machine machine = getMachineById(machineId);

		builder.buildTechnician(technician);
		builder.buildReason(reason);
		builder.buildComments(comments);
		builder.buildStatus(status);
		builder.buildMachine(machine);

		Maintenance maintenance = builder.getMaintenance();

		createMaintenance(maintenance);

		if (status == MaintenanceStatus.VOLTOOID
				&& (machine.getLastMaintenance() == null || executionDate.isAfter(machine.getLastMaintenance())))
		{

			machine.setLastMaintenance(executionDate);
			updateMachine(machine);
		}

		return makeMaintenanceDTO(maintenance);
	}

	public void updateMaintenance(Maintenance maintenance)
	{
		maintenanceRepo.startTransaction();
		maintenanceRepo.update(maintenance);
		maintenanceRepo.commitTransaction();
	}

	public MaintenanceDTO updateMaintenance(int maintenanceId, LocalDate executionDate, LocalDateTime startDate,
			LocalDateTime endDate, int technicianId, String reason, String comments, MaintenanceStatus status,
			int machineId) throws InformationRequiredExceptionMaintenance
	{

		Maintenance existingMaintenance = getMaintenance(maintenanceId);
		if (existingMaintenance == null)
		{
			throw new IllegalArgumentException("Maintenance with ID " + maintenanceId + " not found");
		}

		MaintenanceBuilder builder = new MaintenanceBuilder();
		builder.createMaintenance();
		builder.buildExecutionDate(executionDate);
		builder.buildStartDate(startDate);
		builder.buildEndDate(endDate);

		User technician = getUserById(technicianId);
		Machine machine = getMachineById(machineId);

		builder.buildTechnician(technician);
		builder.buildReason(reason);
		builder.buildComments(comments);
		builder.buildStatus(status);
		builder.buildMachine(machine);

		Maintenance updatedMaintenance = builder.getMaintenance();
		updatedMaintenance.setId(existingMaintenance.getId());

		updateMaintenance(updatedMaintenance);

		if (status == MaintenanceStatus.VOLTOOID
				&& (machine.getLastMaintenance() == null || executionDate.isAfter(machine.getLastMaintenance())))
		{

			machine.setLastMaintenance(executionDate);
			updateMachine(machine);
		}

		return makeMaintenanceDTO(updatedMaintenance);
	}

//    public List<MaintenanceDTO> getMaintenancesForMachine(int machineId) {
//        List<Maintenance> maintenances = maintenanceRepo.findByMachine(machineId);
//        return makeMaintenanceDTOs(maintenances);
//    }
//    
//    public List<MaintenanceDTO> getMaintenancesForTechnician(int technicianId) {
//        List<Maintenance> maintenances = maintenanceRepo.findByTechnician(technicianId);
//        return makeMaintenanceDTOs(maintenances);
//    }
//    
//    public List<MaintenanceDTO> getMaintenancesByStatus(MaintenanceStatus status) {
//        List<Maintenance> maintenances = maintenanceRepo.findByStatus(status);
//        return makeMaintenanceDTOs(maintenances);
//    }

	private User getUserById(int userId)
	{
		UserController uc = AppServices.getInstance().getUserController();
		return uc.getUserById(userId);
	}

	private Machine getMachineById(int machineId)
	{
		MachineController mc = AppServices.getInstance().getMachineController();
		return mc.getMachineById(machineId);
	}

	private void updateMachine(Machine machine)
	{
		MachineController mc = AppServices.getInstance().getMachineController();
		mc.updateMachine(machine);
	}
}