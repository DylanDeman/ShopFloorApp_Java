package domain.report;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import domain.maintenance.MaintenanceDTO;
import domain.site.Site;
import domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "reportId")
public class Report implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	private String reportId;

	@ManyToOne
	@Getter
	private Site site;

	@Getter
	private String maintenanceNr;

	@ManyToOne
	@Getter
	private User Technician;

	@Getter
	private LocalDate startDate;

	@Getter
	private LocalTime startTime;

	@Getter
	private LocalDate endDate;

	@Getter
	private LocalTime endTime;

	@Getter
	private String reason;

	@Getter
	private String remarks;

	/*
	 * // Package-private constructor used by the builder public Report(String
	 * rapportId, Site site, String onderhoudsNr, User technieker, LocalDate
	 * startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String
	 * reden, String opmerkingen) { this.rapportId = rapportId; this.site = site;
	 * this.onderhoudsNr = onderhoudsNr; this.technieker = technieker;
	 * this.startDate = startDate; this.startTime = startTime; this.endDate =
	 * endDate; this.endTime = endTime; this.reden = reden; this.opmerkingen =
	 * opmerkingen; }
	 */
	public Report(MaintenanceDTO selectedMaintenanceDTO, User selectedTechnician, LocalDate startDate,
			LocalTime startTime, LocalDate endDate, LocalTime endTime, String reason, String remarks)
	{

		this.Technician = selectedTechnician;
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.reason = reason;
		this.remarks = remarks;
	}
}