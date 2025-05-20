package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dto.MachineDTO;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionMachine;
import interfaces.Observer;
import interfaces.Subject;
import repository.GenericDaoJpa;
import util.DTOMapper;
import util.MachineStatus;
import util.ProductionStatus;

/**
 * Controller class for managing machine operations. Implements the Subject
 * interface for observer pattern functionality. Handles CRUD operations for
 * machines and converts between DTOs and domain objects.
 */
public class MachineController implements Subject
{
	private GenericDaoJpa<Machine> machineRepo;
	private SiteController siteController;
	private List<Observer> observers = new ArrayList<>();

	/**
	 * Constructs a new MachineController and initializes dependencies.
	 * Automatically adds a NotificationObserver to observe changes.
	 */
	public MachineController()
	{
		machineRepo = new GenericDaoJpa<Machine>(Machine.class);
		siteController = new SiteController();
		addObserver(new NotificationObserver());
	}

	/**
	 * Retrieves all machines and converts them to DTOs.
	 * 
	 * @return an unmodifiable list of MachineDTO objects
	 */
	public List<MachineDTO> getMachineList()
	{
		List<Machine> machines = machineRepo.findAll();
		if (machines == null)
		{
			return List.of();
		}

		return machines.stream().map(machine -> {
			SiteDTOWithoutMachines siteDTO = DTOMapper.toSiteDTOWithoutMachines(machine.getSite());
			return DTOMapper.toMachineDTO(machine, siteDTO);
		}).collect(Collectors.toUnmodifiableList());
	}

	/**
	 * Adds a new machine to the system and notifies observers.
	 * 
	 * @param machine the machine to add
	 */
	public void addNewMachine(Machine machine)
	{
		machineRepo.startTransaction();
		machineRepo.insert(machine);
		machineRepo.commitTransaction();
		notifyObservers("Nieuwe machine toegevoegd: " + machine.getCode());
	}

	/**
	 * Updates an existing machine and notifies observers.
	 * 
	 * @param machine the machine to update
	 */
	public void updateMachine(Machine machine)
	{
		machineRepo.startTransaction();
		machineRepo.update(machine);
		machineRepo.commitTransaction();
		notifyObservers("Machine bijgewerkt: " + machine.getCode());
	}

	/**
	 * Adds a new machine using DTO input.
	 * 
	 * @param machineDTO the machine data transfer object
	 */
	public void addNewMachine(MachineDTO machineDTO)
	{
		Machine machine = convertDTOToMachine(machineDTO);
		addNewMachine(machine);
	}

	/**
	 * Creates and adds a new machine with full details.
	 * 
	 * @param siteDTO           the site where the machine is located
	 * @param technicianDTO     the technician responsible for the machine
	 * @param code              the machine code/identifier
	 * @param machineStatus     the operational status of the machine
	 * @param productionStatus  the production status of the machine
	 * @param location          the physical location of the machine
	 * @param productInfo       information about products the machine handles
	 * @param futureMaintenance scheduled maintenance date
	 * @return the created machine as DTO
	 * @throws InformationRequiredExceptionMachine if required fields are missing
	 */
	public MachineDTO createMachine(SiteDTOWithoutMachines siteDTO, UserDTO technicianDTO, String code,
			MachineStatus machineStatus, ProductionStatus productionStatus, String location, String productInfo,
			LocalDate futureMaintenance) throws InformationRequiredExceptionMachine
	{

		Site site = DTOMapper.toSite(siteDTO, null);
		User technician = DTOMapper.toUser(technicianDTO, null);

		Machine machine = new Machine.Builder().buildSite(site).buildTechnician(technician).buildCode(code)
				.buildMachineStatus(machineStatus).buildProductionStatus(productionStatus).buildLocation(location)
				.buildProductInfo(productInfo).buildFutureMaintenance(futureMaintenance).build();

		addNewMachine(machine);
		return convertToMachineDTO(machine);
	}

	/**
	 * Updates an existing machine with new details.
	 * 
	 * @param id                the ID of the machine to update
	 * @param siteDTO           the new site information
	 * @param technicianDTO     the new technician information
	 * @param code              the new machine code
	 * @param machineStatus     the new machine status
	 * @param productionStatus  the new production status
	 * @param location          the new location
	 * @param productInfo       the new product information
	 * @param futureMaintenance the new maintenance date
	 * @return the updated machine as DTO
	 * @throws InformationRequiredExceptionMachine if required fields are missing
	 */
	public MachineDTO updateMachine(int id, SiteDTOWithoutMachines siteDTO, UserDTO technicianDTO, String code,
			MachineStatus machineStatus, ProductionStatus productionStatus, String location, String productInfo,
			LocalDate futureMaintenance) throws InformationRequiredExceptionMachine
	{

		Machine existingMachine = machineRepo.get(id);
		Site site = DTOMapper.toSite(siteDTO, null);
		User technician = DTOMapper.toUser(technicianDTO, null);

		Machine machine = new Machine.Builder().buildSite(site).buildTechnician(technician).buildCode(code)
				.buildMachineStatus(machineStatus).buildProductionStatus(productionStatus).buildLocation(location)
				.buildProductInfo(productInfo).buildFutureMaintenance(futureMaintenance).build();

		machine.setId(existingMachine.getId());
		updateMachine(machine);
		return convertToMachineDTO(machine);
	}

	@Override
	public void addObserver(Observer observer)
	{
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer)
	{
		observers.remove(observer);
	}

	@Override
	public void notifyObservers(String message)
	{
		observers.forEach(o -> o.update(message));
	}

	/**
	 * Retrieves a machine by its ID.
	 * 
	 * @param machineId the ID of the machine to retrieve
	 * @return the Machine object, or null if not found
	 */
	public Machine getMachineById(int machineId)
	{
		return machineRepo.get(machineId);
	}

	/**
	 * Converts a MachineDTO to a Machine domain object.
	 * 
	 * @param dto the data transfer object to convert
	 * @return the converted Machine object
	 */
	public Machine convertDTOToMachine(MachineDTO dto)
	{
		Machine machine = machineRepo.get(dto.id());
		Site site = null;

		if (dto.site() != null)
		{
			site = siteController.getSiteObject(dto.site().id());
		}

		return DTOMapper.toMachine(dto, machine, site);
	}

	/**
	 * Converts a Machine domain object to a MachineDTO.
	 * 
	 * @param machine the domain object to convert
	 * @return the converted DTO, or null if input is null
	 */
	public MachineDTO convertToMachineDTO(Machine machine)
	{
		if (machine == null)
		{
			return null;
		}

		SiteDTOWithoutMachines siteDTO = DTOMapper.toSiteDTOWithoutMachines(machine.getSite());
		return DTOMapper.toMachineDTO(machine, siteDTO);
	}
}