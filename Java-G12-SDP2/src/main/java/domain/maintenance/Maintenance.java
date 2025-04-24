package domain.maintenance;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import domain.rapport.Rapport;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import util.MaintenanceStatus;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="maintenances")
public class Maintenance implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private int id;
	
	@Getter
	private LocalDate executionDate;
	@Getter
	private LocalDateTime startDate;
	@Getter
	private LocalDateTime endDate;
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "technician_id")
	@Getter
	private User technician;
	@Getter
	private String reason;
	@Getter
	private String comments;
	@Enumerated(EnumType.STRING)
	@Getter
	private MaintenanceStatus status;
	
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "report_id")
	@Getter
	private Rapport report;
	
	public Maintenance(LocalDate executionDate, 
			LocalDateTime startDate, LocalDateTime endDate, 
			User technician, 
			String reason, 
			String comments,
			MaintenanceStatus status,
			Rapport report
			) {
		this.executionDate = executionDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.technician = technician;
		this.reason = reason;
		this.comments = comments;
		this.status = status;
		this.report = report;
	}
	
}
