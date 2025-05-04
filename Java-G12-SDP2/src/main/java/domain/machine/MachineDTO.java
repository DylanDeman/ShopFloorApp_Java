package domain.machine;

import java.time.LocalDate;
import java.util.Objects;

import domain.site.SiteDTO;
import domain.user.User;
import util.MachineStatus;
import util.ProductionStatus;

//TODO UserDTO
public record MachineDTO(int id, SiteDTO site, User technician, String code, MachineStatus machineStatus,
		ProductionStatus productionStatus, String location, String productInfo, LocalDate lastMaintenance,
		LocalDate futureMaintenance, int numberDaysSinceLastMaintenance, double upTimeInHours)
{
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof MachineDTO other))
			return false;
		return id == other.id && code.equals(other.code);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, code);
	}
}
