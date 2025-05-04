package domain.machine;

import java.io.Serializable;
import java.time.LocalDate;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED) // ? Final weggedaan van attributen om noargsConstructor op te
													// lossen!
@Getter
@Setter
public class Machine implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
   
    
    @ManyToOne
    private Site site;
    
    @ManyToOne
    private User technician;
        
    private String code, status, productieStatus, location, productInfo;
    private LocalDateTime lastMaintenance, futureMaintenance;
    private int numberDaysSinceLastMaintenance;

    
    private Machine(Builder builder) {
    	site = builder.site;
    	technician = builder.technician;
    	code = builder.code;
    	status = builder.status;
    	productieStatus = builder.productieStatus;
    	location = builder.location;
    	productInfo = builder.productInfo;
    	lastMaintenance = builder.lastMaintenance;
    	futureMaintenance = builder.futureMaintenance;
    	numberDaysSinceLastMaintenance = (lastMaintenance != null)
    	        ? (int) java.time.Duration.between(lastMaintenance, LocalDateTime.now()).toDays()
    	        : 0;
    }
    public double getUpTimeInHours() {
        return (lastMaintenance != null)
            ? Duration.between(lastMaintenance, LocalDateTime.now()).toHours()
            : 0.0;
    }

    
    public static Builder builder() {
    	return new Builder();
    }

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
	private double upTimeInHours = 0.0;

}
