package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.Site;
import domain.User;
import exceptions.InformationRequiredExceptionReport;
import util.AuthenticationUtil;
import util.Role;

// Een director is enkel nodig als je een vaste volgorde hebt,
// hierbij geen director nodig denk ik 
public class ReportDirector
{
	private ReportBuilder builder;

	public ReportDirector(ReportBuilder builder)
	{
		this.builder = builder;
	}

	public Report constructStandardMaintenanceRapport(String rapportId, Site site, String onderhoudsNr, User technician,
			LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime)
			throws InformationRequiredExceptionReport
	{
		boolean hasRole = AuthenticationUtil.hasRole(Role.ADMINISTRATOR);

		if (hasRole)
		{
			builder.createReport();
			builder.buildSite(site);
			builder.buildTechnician(technician);
			builder.buildStartDate(startDate);
			builder.buildStartTime(startTime);
			builder.buildEndDate(endDate);
			builder.buildEndTime(endTime);
			builder.buildReason("Regulier onderhoud");
			builder.buildRemarks("Standaard onderhoudsrapport");

			return builder.getReport();
		}

		return null;

	}
}