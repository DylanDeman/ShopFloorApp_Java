package domain.machine;

import java.time.LocalDateTime;

import domain.site.SiteDTO;
import domain.user.User;

//TODO UserDTO
public record MachineDTO(int id, SiteDTO site, User technician, String code, String status, String productieStatus, String location,
		String productInfo, LocalDateTime lastMaintenance, LocalDateTime futureMaintenance, int numberDaysSinceLastMaintenance,
		double upTimeInHours) {

}
