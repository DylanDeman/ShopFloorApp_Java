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
import lombok.Setter;

/**
 * Entity representing a maintenance report. Contains details about the
 * maintenance activity, technician, timing, and related site.
 * <p>
 * Uses JPA annotations for ORM mapping. Implements {@link Serializable} for
 * potential serialization.
 * </p>
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "reportId")
@Getter
@Setter
public class Report implements Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * Unique identifier for the report, auto-generated.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reportId;

	/**
	 * The site where the maintenance took place.
	 */
	@ManyToOne
	@JoinColumn(name = "site_id")
	private Site site;

	/**
	 * The maintenance activity associated with this report.
	 */
	@ManyToOne
	@JoinColumn(name = "maintenance_id")
	private Maintenance maintenance;

	/**
	 * The technician responsible for the maintenance.
	 */
	@ManyToOne
	private User technician;

	/**
	 * The start date of the maintenance.
	 */
	private LocalDate startDate;

	/**
	 * The start time of the maintenance.
	 */
	private LocalTime startTime;

	/**
	 * The end date of the maintenance.
	 */
	private LocalDate endDate;

	/**
	 * The end time of the maintenance.
	 */
	private LocalTime endTime;

	/**
	 * Reason for performing the maintenance.
	 */
	private String reason;

	/**
	 * Additional remarks or comments related to the maintenance.
	 */
	private String remarks;

	/**
	 * Constructs a new Report instance with the specified details.
	 * 
	 * @param selectedMaintenance the maintenance activity
	 * @param selectedTechnician  the technician performing the maintenance
	 * @param startDate           start date of maintenance
	 * @param startTime           start time of maintenance
	 * @param endDate             end date of maintenance
	 * @param endTime             end time of maintenance
	 * @param reason              reason for maintenance
	 * @param remarks             additional remarks
	 * @param site                site where maintenance took place
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
		this.site = site;
	}

	/**
	 * Builder for creating {@link Report} instances in a flexible and readable
	 * manner.
	 */
	public static class Builder
	{

		private Site site;
		private Maintenance maintenance;
		private User technician;
		private LocalDate startDate;
		private LocalTime startTime;
		private LocalDate endDate;
		private LocalTime endTime;
		private String reason;
		private String remarks;

		/**
		 * Creates a new Builder instance.
		 */
		public Builder()
		{
		}

		/**
		 * Sets the site of the report.
		 * 
		 * @param site the site
		 * @return the builder instance
		 */
		public Builder withSite(Site site)
		{
			this.site = site;
			return this;
		}

		/**
		 * Sets the maintenance activity for the report.
		 * 
		 * @param maintenance the maintenance
		 * @return the builder instance
		 */
		public Builder withMaintenance(Maintenance maintenance)
		{
			this.maintenance = maintenance;
			return this;
		}

		/**
		 * Sets the technician responsible for the report.
		 * 
		 * @param technician the technician
		 * @return the builder instance
		 */
		public Builder withTechnician(User technician)
		{
			this.technician = technician;
			return this;
		}

		/**
		 * Sets the start date of the maintenance.
		 * 
		 * @param startDate the start date
		 * @return the builder instance
		 */
		public Builder withStartDate(LocalDate startDate)
		{
			this.startDate = startDate;
			return this;
		}

		/**
		 * Sets the start time of the maintenance.
		 * 
		 * @param startTime the start time
		 * @return the builder instance
		 */
		public Builder withStartTime(LocalTime startTime)
		{
			this.startTime = startTime;
			return this;
		}

		/**
		 * Sets the end date of the maintenance.
		 * 
		 * @param endDate the end date
		 * @return the builder instance
		 */
		public Builder withEndDate(LocalDate endDate)
		{
			this.endDate = endDate;
			return this;
		}

		/**
		 * Sets the end time of the maintenance.
		 * 
		 * @param endTime the end time
		 * @return the builder instance
		 */
		public Builder withEndTime(LocalTime endTime)
		{
			this.endTime = endTime;
			return this;
		}

		/**
		 * Sets the reason for maintenance.
		 * 
		 * @param reason the reason
		 * @return the builder instance
		 */
		public Builder withReason(String reason)
		{
			this.reason = reason;
			return this;
		}

		/**
		 * Sets additional remarks.
		 * 
		 * @param remarks the remarks
		 * @return the builder instance
		 */
		public Builder withRemarks(String remarks)
		{
			this.remarks = remarks;
			return this;
		}

		/**
		 * Builds and returns a {@link Report} instance with the configured parameters.
		 * 
		 * @return a new {@link Report} instance
		 */
		public Report build()
		{
			Report report = new Report();
			report.site = this.site;
			report.maintenance = this.maintenance;
			report.technician = this.technician;
			report.startDate = this.startDate;
			report.startTime = this.startTime;
			report.endDate = this.endDate;
			report.endTime = this.endTime;
			report.reason = this.reason;
			report.remarks = this.remarks;
			return report;
		}
	}
}
