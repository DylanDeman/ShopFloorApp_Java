package domain.machine;

import java.time.LocalDateTime;
import java.util.Objects;

import domain.site.SiteDTO;
import domain.user.User;

//TODO UserDTO
public record MachineDTO(int id, SiteDTO site, User technician, String code, String status, String productieStatus, String location,
		String productInfo, LocalDateTime lastMaintenance, LocalDateTime futureMaintenance, int numberDaysSinceLastMaintenance,
		double upTimeInHours) {
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof MachineDTO other)) return false;
	    return id == other.id && code.equals(other.code);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(id, code);
	}
}
