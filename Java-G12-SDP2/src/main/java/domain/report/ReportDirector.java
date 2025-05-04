package domain.report;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.site.Site;
import domain.user.User;
import exceptions.InformationRequiredExceptionReport;
import util.AuthenticationUtil;
import util.Role;

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
		boolean hasRole = AuthenticationUtil.hasRole(Role.ADMIN);

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