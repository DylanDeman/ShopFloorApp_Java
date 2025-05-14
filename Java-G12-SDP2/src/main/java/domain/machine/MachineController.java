package domain.machine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import domain.Observer;
import domain.Subject;
import domain.notifications.NotificationObserver;
import domain.site.Site;
import domain.site.SiteController;
import domain.user.User;
import dto.MachineDTO;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;
import exceptions.InformationRequiredExceptionMachine;
import gui.AppServices;
import util.DTOMapper;
import util.MachineStatus;
import util.ProductionStatus;

public class MachineController implements Subject {
	private MachineDao machineRepo;
	private SiteController siteController;
	private List<Observer> observers = new ArrayList<>();

	public MachineController() {
		machineRepo = new MachineDaoJpa();
		siteController = new SiteController();
		addObserver(new NotificationObserver());
	}

	public List<MachineDTO> getMachineList() {
		List<Machine> machines = machineRepo.findAll();
		if (machines == null) {
			return List.of();
		}

		return machines.stream().map(machine -> {
			SiteDTOWithoutMachines siteDTO = DTOMapper.toSiteDTOWithoutMachines(machine.getSite());
			return DTOMapper.toMachineDTO(machine, siteDTO);
		}).collect(Collectors.toUnmodifiableList());
	}

	public void addNewMachine(Machine machine) {
		machineRepo.startTransaction();
		machineRepo.insert(machine);
		machineRepo.commitTransaction();

		notifyObservers("Nieuwe machine toegevoegd: " + machine.getCode());
	}

	public void updateMachine(Machine machine) {
		machineRepo.startTransaction();
		machineRepo.update(machine);
		machineRepo.commitTransaction();

		notifyObservers("Machine bijgewerkt: " + machine.getCode());
	}

	public void addNewMachine(MachineDTO machineDTO) {
		Machine machine = convertDTOToMachine(machineDTO);
		addNewMachine(machine);
	}

	public MachineDTO createMachine(SiteDTOWithoutMachines siteDTO, UserDTO technicianDTO, String code,
			MachineStatus machineStatus, ProductionStatus productionStatus, String location, String productInfo,
			LocalDate futureMaintenance) throws InformationRequiredExceptionMachine {
		
		Site site = DTOMapper.toSite(siteDTO, null);
		User technician = DTOMapper.toUser(technicianDTO, null);
		
		MachineBuilder builder = new MachineBuilder();
		builder.createMachine();
		builder.buildSite(site);
		builder.buildTechnician(technician);
		builder.buildCode(code);
		builder.buildStatusses(machineStatus, productionStatus);
		builder.buildLocation(location);
		builder.buildProductInfo(productInfo);
		builder.buildMaintenance(futureMaintenance);

		Machine machine = builder.getMachine();

		addNewMachine(machine);

		return convertToMachineDTO(machine);
	}

	public MachineDTO updateMachine(int id, SiteDTOWithoutMachines siteDTO, UserDTO technicianDTO, String code,
			MachineStatus machineStatus, ProductionStatus productionStatus, String location, String productInfo,
			LocalDate futureMaintenance) throws InformationRequiredExceptionMachine {

		Site site = DTOMapper.toSite(siteDTO, null);
		User technician = DTOMapper.toUser(technicianDTO, null);
		
		MachineBuilder builder = new MachineBuilder();
		builder.createMachine();
		builder.buildId(id);
		builder.buildSite(site);
		builder.buildTechnician(technician);
		builder.buildCode(code);
		builder.buildStatusses(machineStatus, productionStatus);
		builder.buildLocation(location);
		builder.buildProductInfo(productInfo);
		builder.buildMaintenance(futureMaintenance);

		Machine machine = builder.getMachine();

		updateMachine(machine);

		return convertToMachineDTO(machine);
	}

	@Override
	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	@Override
	public void notifyObservers(String message) {
		for (Observer observer : observers) {
			observer.update(message);
		}
	}

	public Machine getMachineById(int machineId) {
		return machineRepo.get(machineId);
	}

	public Machine convertDTOToMachine(MachineDTO dto) {
		Machine machine = machineRepo.get(dto.id());
		Site site = null;

		if (dto.site() != null) {
			site = siteController.getSiteObject(dto.site().id());
		}

		return DTOMapper.toMachine(dto, machine, site);
	}

	private MachineDTO convertToMachineDTO(Machine machine) {
		if (machine == null) {
			return null;
		}

		SiteDTOWithoutMachines siteDTO = DTOMapper.toSiteDTOWithoutMachines(machine.getSite());

		return DTOMapper.toMachineDTO(machine, siteDTO);
	}

}