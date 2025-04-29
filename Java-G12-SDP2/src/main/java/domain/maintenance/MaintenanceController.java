package domain.maintenance;

import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceController
{
	public MaintenanceDao maintenanceRepo;

	public MaintenanceController()
	{
		maintenanceRepo = new MaintenanceDaoJpa();
	}

	public List<MaintenanceDTO> getMaintenances()
	{
		List<Maintenance> sites = maintenanceRepo.findAll();
		return makeMaintenanceDTOs(sites);
	}

	public List<MaintenanceDTO> makeMaintenanceDTOs(List<Maintenance> maintenances)
	{
		return maintenances.stream().map(maintenance ->
		{
			return new MaintenanceDTO(maintenance.getId(), maintenance.getExecutionDate(), maintenance.getStartDate(),
					maintenance.getEndDate(), maintenance.getTechnician(), maintenance.getReason(),
					maintenance.getComments(), maintenance.getStatus(), maintenance.getReport());
		}).collect(Collectors.toUnmodifiableList());
	}

}
