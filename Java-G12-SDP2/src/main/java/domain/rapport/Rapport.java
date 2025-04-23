package domain.rapport;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

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
@EqualsAndHashCode(of = "rapportId")
public class Rapport implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	private String rapportId;

	@ManyToOne
	@Getter
	private Site site;

	@Getter
	private String onderhoudsNr;

	@ManyToOne
	@Getter
	private User technieker;

	@Getter
	private LocalDate startDate;

	@Getter
	private LocalTime startTime;

	@Getter
	private LocalDate endDate;

	@Getter
	private LocalTime endTime;

	@Getter
	private String reden;

	@Getter
	private String opmerkingen;

	// Package-private constructor used by the builder
	Rapport(String rapportId, Site site, String onderhoudsNr, User technieker, LocalDate startDate, LocalTime startTime,
			LocalDate endDate, LocalTime endTime, String reden, String opmerkingen)
	{
		this.rapportId = rapportId;
		this.site = site;
		this.onderhoudsNr = onderhoudsNr;
		this.technieker = technieker;
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.reden = reden;
		this.opmerkingen = opmerkingen;
	}
}