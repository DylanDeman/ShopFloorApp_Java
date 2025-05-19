package domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

		User technician = getUserById(technicianId);
		Machine machine = getMachineById(machineId);

		Maintenance maintenance = new Maintenance.Builder().buildExecutionDate(executionDate).buildStartDate(startDate)
				.buildEndDate(endDate).buildTechnician(technician).buildReason(reason).buildComments(comments)
				.buildMaintenanceStatus(status).buildMachine(machine).build();

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

		User technician = getUserById(technicianId);
		Machine machine = getMachineById(machineId);

		Maintenance maintenance = new Maintenance.Builder().buildExecutionDate(executionDate).buildStartDate(startDate)
				.buildEndDate(endDate).buildTechnician(technician).buildReason(reason).buildComments(comments)
				.buildMaintenanceStatus(status).buildMachine(machine).build();

		maintenance.setId(existingMaintenance.getId());

		updateMaintenance(maintenance);

		if (status == MaintenanceStatus.VOLTOOID
				&& (machine.getLastMaintenance() == null || executionDate.isAfter(machine.getLastMaintenance())))
		{

			machine.setLastMaintenance(executionDate);
			updateMachine(machine);
		}

		return makeMaintenanceDTO(maintenance);
	}

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