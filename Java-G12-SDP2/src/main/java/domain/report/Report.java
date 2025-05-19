package domain.report;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import domain.User;
import domain.maintenance.Maintenance;
import domain.site.Site;
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
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "reportId")
@Getter
@Setter
public class Report implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reportId;

	@ManyToOne
	@JoinColumn(name = "site_id")
	private Site site;

	@ManyToOne
	@JoinColumn(name = "maintenance_id")
	private Maintenance maintenance;

	@ManyToOne
	private User technician;

	private LocalDate startDate;

	private LocalTime startTime;

	private LocalDate endDate;

	private LocalTime endTime;

	private String reason;

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

		this.technician = selectedTechnician;
		this.startDate = startDate;
		this.startTime = startTime;
		this.maintenance = selectedMaintenance;
		this.endDate = endDate;
		this.endTime = endTime;
		this.reason = reason;
		this.remarks = remarks;
	}
}