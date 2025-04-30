package domain.report;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import domain.maintenance.Maintenance;
import domain.site.Site;
import domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reportId;

	@ManyToOne
	@Getter
	@JoinColumn(name = "site_id")
	private Site site;

	@Getter
	@ManyToOne
	@JoinColumn(name = "maintenance_id")
	private Maintenance maintenance;

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
	public Report(Maintenance selectedMaintenance, User selectedTechnician, LocalDate startDate, LocalTime startTime,
			LocalDate endDate, LocalTime endTime, String reason, String remarks, Site site)
	{

		this.Technician = selectedTechnician;
		this.startDate = startDate;
		this.startTime = startTime;
		this.maintenance = selectedMaintenance;
		this.endDate = endDate;
		this.endTime = endTime;
		this.reason = reason;
		this.remarks = remarks;
	}
}