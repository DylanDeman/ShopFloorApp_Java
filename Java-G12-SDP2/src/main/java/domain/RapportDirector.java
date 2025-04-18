package domain;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Director class that knows the process of building different types of Rapport
 * objects. Controls the building sequence.
 */
public class RapportDirector
{

	private RapportBuilder builder;

	public RapportDirector(RapportBuilder builder)
	{
		this.builder = builder;
	}

	/**
	 * Creates a standard maintenance rapport
	 */
	public Rapport constructStandardMaintenanceRapport(String rapportId, Site site, String onderhoudsNr,
			User technieker, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime)
	{

		return builder.setSite(site).setOnderhoudsNr(onderhoudsNr).setTechnieker(technieker).setStartDate(startDate)
				.setStartTime(startTime).setEndDate(endDate).setEndTime(endTime).setReden("Regulier onderhoud").build();
	}

	/**
	 * Creates an emergency repair rapport
	 */
	public Rapport constructEmergencyRepairRapport(String rapportId, Site site, String onderhoudsNr, User technieker,
			LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String reden,
			String opmerkingen)
	{

		return builder.setSite(site).setOnderhoudsNr(onderhoudsNr).setTechnieker(technieker).setStartDate(startDate)
				.setStartTime(startTime).setEndDate(endDate).setEndTime(endTime).setReden(reden)
				.setOpmerkingen(opmerkingen).build();
	}
}