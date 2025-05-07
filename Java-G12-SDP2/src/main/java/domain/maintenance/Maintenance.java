package domain.maintenance;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import domain.report.Report;
import domain.machine.Machine;
import domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.MaintenanceStatus;

@Entity
@Table(name = "maintenances")
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance implements Serializable
{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private int id;

	@Getter
	@Setter
	private LocalDate executionDate;
	@Getter
	@Setter
	private LocalDateTime startDate;
	@Getter
	@Setter
	private LocalDateTime endDate;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "technician_id")
	@Getter
	@Setter
	private User technician;
	
	@Getter
	@Setter
	private String reason;
	@Getter
	@Setter
	private String comments;
	
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private MaintenanceStatus status;

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "machine_id")
	@Getter
	@Setter
	private Machine machine;
	
    @PrePersist
    @PreUpdate
    void validateDates() {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalStateException("End date cannot be before start date.");
        }
    }
	
	public Maintenance(LocalDate executionDate, 
			LocalDateTime startDate, LocalDateTime endDate, 
			User technician, 
			String reason, 
			String comments,
			MaintenanceStatus status,
			Machine machine
			) {
		this.executionDate = executionDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.technician = technician;
		this.reason = reason;
		this.comments = comments;
		this.status = status;
		this.machine = machine;
	}
}
