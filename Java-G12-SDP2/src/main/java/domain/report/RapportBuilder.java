package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.site.Site;
import domain.user.User;

/**
 * Builder interface for creating Rapport objects. Specifies all the steps
 * needed to build a Rapport.
 */
public interface RapportBuilder
{
	RapportBuilder setSite(Site site);

	RapportBuilder setMaintenanceNr(String onderhoudsNr);

	RapportBuilder setTechnician(User technieker);

	RapportBuilder setStartDate(LocalDate startDate);

	RapportBuilder setStartTime(LocalTime startTime);

	RapportBuilder setEndDate(LocalDate endDate);

	RapportBuilder setEndTime(LocalTime endTime);

	RapportBuilder setReason(String reden);

	RapportBuilder setRemarks(String opmerkingen);

	Report build();
}