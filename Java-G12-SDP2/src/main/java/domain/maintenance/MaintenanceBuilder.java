package domain.maintenance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import domain.machine.Machine;
import domain.user.User;
import exceptions.InformationRequiredExceptionMaintenance;
import util.MaintenanceStatus;
import util.RequiredElementMaintenance;
public class MaintenanceBuilder
{
	private Maintenance maintenance;
	private Map<String, RequiredElementMaintenance> requiredElements;
	public void createMaintenance()
	{
		maintenance = new Maintenance();
		requiredElements = new HashMap<>();
	}	

	public void buildExecutionDate(LocalDate executionDate)
	{
		maintenance.setExecutionDate(executionDate);
	}

	public void buildStartDate(LocalDateTime startDate)
	{
		maintenance.setStartDate(startDate);
	}

	public void buildEndDate(LocalDateTime endDate)
	{
		maintenance.setEndDate(endDate);
	}

	public void buildTechnician(User technician)
	{
		maintenance.setTechnician(technician);
	}

	public void buildReason(String reason)
	{
		maintenance.setReason(reason);
	}

	public void buildComments(String comments)
	{
		maintenance.setComments(comments);
	}

	public void buildStatus(MaintenanceStatus status)
	{
		maintenance.setStatus(status);
	}
	
	public void buildMachine(Machine machine) {
		maintenance.setMachine(machine);
	}

	public Maintenance getMaintenance() throws InformationRequiredExceptionMaintenance
	{
		// Verplichte velden controleren
		if (maintenance.getExecutionDate() == null)
		{
			requiredElements.put("executionDate", RequiredElementMaintenance.EXECUTION_DATE_REQUIRED);
		}
		if (maintenance.getStartDate() == null)
		{
			requiredElements.put("startDate", RequiredElementMaintenance.START_DATE_REQUIRED);
		}
		if (maintenance.getEndDate() == null)
		{
			System.out.println(maintenance.getEndDate());
			requiredElements.put("endDate", RequiredElementMaintenance.END_DATE_REQUIRED);
		}
		if (maintenance.getTechnician() == null)
		{
			requiredElements.put("technician", RequiredElementMaintenance.TECHNICIAN_REQUIRED);
		}
		if (maintenance.getReason() == null || maintenance.getReason().isBlank())
		{
			requiredElements.put("reason", RequiredElementMaintenance.REASON_REQUIRED);
		}
		if (maintenance.getStatus() == null)
		{
			requiredElements.put("status", RequiredElementMaintenance.MAINTENANCESTATUS_REQUIRED);
		}
		if (maintenance.getMachine() == null)
		{
			requiredElements.put("machine", RequiredElementMaintenance.MACHINE_REQUIRED);
		}

		// Datum/tijd validaties
		if (maintenance.getEndDate() != null && maintenance.getStartDate() != null)
		{
			if (maintenance.getEndDate().isBefore(maintenance.getStartDate()))
			{
				requiredElements.put("endDate", RequiredElementMaintenance.END_DATE_BEFORE_START);
			} else if (maintenance.getEndDate().equals(maintenance.getStartDate()) && maintenance.getEndDate() != null
					&& maintenance.getStartDate() != null && maintenance.getEndDate().isBefore(maintenance.getStartDate()))
			{
				requiredElements.put("endDate", RequiredElementMaintenance.END_DATE_BEFORE_START);
			}
		}

		if (!requiredElements.isEmpty())
		{
			throw new InformationRequiredExceptionMaintenance(requiredElements);
		}

		return maintenance;
	}
}