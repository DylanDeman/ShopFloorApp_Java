package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import exceptions.InformationRequiredExceptionMaintenance;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.MaintenanceStatus;
import util.RequiredElementMaintenance;

@Entity
@Table(name = "maintenances")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Maintenance implements Serializable
{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private LocalDate executionDate;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "technician_id")
	private User technician;

	private String reason;
	private String comments;

	@Enumerated(EnumType.STRING)
	private MaintenanceStatus status;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "machine_id")
	private Machine machine;

	@PrePersist
	@PreUpdate
	void validateDates()
	{
		if (startDate != null && endDate != null && endDate.isBefore(startDate))
		{
			throw new IllegalStateException("End date cannot be before start date.");
		}
	}

	public Maintenance(LocalDate executionDate, LocalDateTime startDate, LocalDateTime endDate, User technician,
			String reason, String comments, MaintenanceStatus status, Machine machine)
	{
		this.executionDate = executionDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.technician = technician;
		this.reason = reason;
		this.comments = comments;
		this.status = status;
		this.machine = machine;
	}

	public static class Builder
	{
		private LocalDate executionDate;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private User technician;
		private String reason;
		private String comments;
		private MaintenanceStatus status;
		private Machine machine;

		protected Maintenance maintenance;

		public Builder()
		{

		}

		public Builder buildExecutionDate(LocalDate executionDate)
		{
			this.executionDate = executionDate;
			return this;
		}

		public Builder buildStartDate(LocalDateTime startDate)
		{
			this.startDate = startDate;
			return this;
		}

		public Builder buildEndDate(LocalDateTime endDate)
		{
			this.endDate = endDate;
			return this;
		}

		public Builder buildTechnician(User technician)
		{
			this.technician = technician;
			return this;
		}

		public Builder buildReason(String reason)
		{
			this.reason = reason;
			return this;
		}

		public Builder buildComments(String comments)
		{
			this.comments = comments;
			return this;
		}

		public Builder buildMaintenanceStatus(MaintenanceStatus status)
		{
			this.status = status;
			return this;
		}

		public Builder buildMachine(Machine machine)
		{
			this.machine = machine;
			return this;
		}

		public Maintenance build() throws InformationRequiredExceptionMaintenance
		{
			validateRequiredFields();

			maintenance = new Maintenance();
			maintenance.setExecutionDate(executionDate);
			maintenance.setStartDate(startDate);
			maintenance.setEndDate(endDate);
			maintenance.setTechnician(technician);
			maintenance.setReason(reason);
			maintenance.setComments(comments);
			maintenance.setStatus(status);
			maintenance.setMachine(machine);

			return maintenance;
		}

		private void validateRequiredFields() throws InformationRequiredExceptionMaintenance
		{
			Map<String, RequiredElementMaintenance> requiredElements = new HashMap<>();

			if (maintenance.getExecutionDate() == null)
			{
				requiredElements.put("executionDate", RequiredElementMaintenance.EXECUTION_DATE_REQUIRED);
			}

			if (maintenance.getStartDate() == null)
			{
				requiredElements.put("startDate", RequiredElementMaintenance.START_DATE_REQUIRED);
			}

			if (maintenance.getEndDate() == null)
			{
				System.out.println(maintenance.getEndDate());
				requiredElements.put("endDate", RequiredElementMaintenance.END_DATE_REQUIRED);
			}

			if (maintenance.getTechnician() == null)
			{
				requiredElements.put("technician", RequiredElementMaintenance.TECHNICIAN_REQUIRED);
			}

			if (maintenance.getReason() == null || maintenance.getReason().isBlank())
			{
				requiredElements.put("reason", RequiredElementMaintenance.REASON_REQUIRED);
			}

			if (maintenance.getStatus() == null)
			{
				requiredElements.put("status", RequiredElementMaintenance.MAINTENANCESTATUS_REQUIRED);
			}

			if (maintenance.getMachine() == null)
			{
				requiredElements.put("machine", RequiredElementMaintenance.MACHINE_REQUIRED);
			}

			if (maintenance.getEndDate() != null && maintenance.getStartDate() != null)
			{
				if (maintenance.getEndDate().isBefore(maintenance.getStartDate()))
				{
					requiredElements.put("endDate", RequiredElementMaintenance.END_DATE_BEFORE_START);
				} else if (maintenance.getEndDate().equals(maintenance.getStartDate())
						&& maintenance.getEndDate() != null && maintenance.getStartDate() != null
						&& maintenance.getEndDate().isBefore(maintenance.getStartDate()))
				{
					requiredElements.put("endDate", RequiredElementMaintenance.END_DATE_BEFORE_START);
				}
			}

			if (!requiredElements.isEmpty())
			{
				throw new InformationRequiredExceptionMaintenance(requiredElements);
			}
		}
	}
}
