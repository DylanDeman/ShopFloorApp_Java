package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.site.Site;
import domain.user.User;
import util.AuthenticationUtil;
import util.Role;

public class ReportDirector
{
	private RapportBuilder builder;

	public ReportDirector(RapportBuilder builder)
	{
		this.builder = builder;
	}

	public Report constructStandardMaintenanceRapport(String rapportId, Site site, String onderhoudsNr, User technieker,
			LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime)
	{
		boolean hasRole = AuthenticationUtil.hasRole(Role.ADMIN);

		if (hasRole)
		{
			return builder.setSite(site).setMaintenanceNr(onderhoudsNr).setTechnician(technieker)
					.setStartDate(startDate).setStartTime(startTime).setEndDate(endDate).setEndTime(endTime)
					.setReason("Regulier onderhoud").setRemarks("Standaard onderhoudsrapport").build();
		}

		return null;

	}
}