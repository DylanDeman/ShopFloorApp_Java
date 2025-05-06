package domain.machine;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import domain.site.Site;
import domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import util.MachineStatus;
import util.ProductionStatus;

@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Machine implements Serializable
{
	public Machine(Site site, User tecnician, String code, String location, String productInfo,
			MachineStatus machineStatus, ProductionStatus productionStatus, LocalDate futureMaintenance)
	{
		setSite(site);
		setTechnician(tecnician);
		setCode(code);
		setLocation(location);
		setProductInfo(productInfo);
		setMachineStatus(machineStatus);
		setProductionStatus(productionStatus);
		setFutureMaintenance(futureMaintenance);
		
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	private Site site;

	@ManyToOne
	private User technician;

	private String code, location, productInfo;

	private MachineStatus machineStatus;
	private ProductionStatus productionStatus;

	private LocalDate lastMaintenance, futureMaintenance;
	private int numberDaysSinceLastMaintenance;

	public double getUpTimeInHours() {
	    if (lastMaintenance == null) {
	        System.out.println("Last maintenance is null for machine: " + code);
	        return 0.0;
	    }
	    
	    LocalDateTime maintenanceDateTime = lastMaintenance.atStartOfDay();
	    double hours = Duration.between(maintenanceDateTime, LocalDateTime.now()).toHours();
	    System.out.println("Machine " + code + " last maintenance: " + lastMaintenance + 
	                       ", current uptime: " + hours + " hours");
	    return hours;
	}
}