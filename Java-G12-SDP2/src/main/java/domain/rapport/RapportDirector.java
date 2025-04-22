package domain.rapport;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.User;
import domain.site.Site;

public class RapportDirector
{
	private RapportBuilder builder;

	public RapportDirector(RapportBuilder builder)
	{
		this.builder = builder;
	}

	public Rapport constructStandardMaintenanceRapport(String rapportId, Site site, String onderhoudsNr,
			User technieker, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime)
	{

		return builder.setSite(site).setOnderhoudsNr(onderhoudsNr).setTechnieker(technieker).setStartDate(startDate)
				.setStartTime(startTime).setEndDate(endDate).setEndTime(endTime).setReden("Regulier onderhoud")
				.setOpmerkingen("Standaard onderhoudsrapport").build();
	}
}