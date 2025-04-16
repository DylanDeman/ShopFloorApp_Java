package domain;

import java.io.Serializable;
import java.util.Date;

import domain.Site.Site;
import exceptions.InvalidMachineException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
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
    private Date lastMaintenance, futureMaintenance;
    private int numberDaysSinceLastMaintenance;
    private double upTimeInHours;

    public void setId(int id) {
        if (id < 0) {
            throw new InvalidMachineException("ID cannot be negative");
        }
        this.id = id;
    }

    public void setSite(Site site) {
        if (site == null) {
            throw new InvalidMachineException("Site cannot be null");
        }
        this.site = site;
    }

    public void setStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new InvalidMachineException("Status cannot be null or empty");
        }
        this.status = status.trim();
    }

    public void setProductieStatus(String productieStatus) {
        if (productieStatus == null || productieStatus.isBlank()) {
            throw new InvalidMachineException("Production status cannot be null or empty");
        }
        this.productieStatus = productieStatus.trim();
    }

    public void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new InvalidMachineException("Location cannot be null or empty");
        }
        this.location = location.trim();
    }

    public void setProductInfo(String productInfo) {
        if (productInfo == null || productInfo.isBlank()) {
            throw new InvalidMachineException("Product info cannot be null or empty");
        }
        this.productInfo = productInfo.trim();
    }

    public void setLastMaintenance(Date lastMaintenance) {
        if (lastMaintenance == null) {
            throw new InvalidMachineException("Last maintenance date cannot be null");
        }
        if (lastMaintenance.after(new Date())) {
            throw new InvalidMachineException("Last maintenance date cannot be in the future");
        }
        this.lastMaintenance = lastMaintenance;
    }

    public void setFutureMaintenance(Date futureMaintenance) {
        if (futureMaintenance == null) {
            throw new InvalidMachineException("Future maintenance date cannot be null");
        }
        if (futureMaintenance.before(new Date())) {
            throw new InvalidMachineException("Future maintenance date cannot be in the past");
        }
        this.futureMaintenance = futureMaintenance;
    }

    public void setNumberDaysSinceLastMaintenance(int numberDaysSinceLastMaintenance) {
        if (numberDaysSinceLastMaintenance < 0) {
            throw new InvalidMachineException("Days since last maintenance cannot be negative");
        }
        this.numberDaysSinceLastMaintenance = numberDaysSinceLastMaintenance;
    }
    
    public void setTechnician(User technician) {
        if (technician == null) {
            throw new InvalidMachineException("Technician cannot be null");
        }
        if (!"TECHNIEKER".equals(technician.getRole())) {
            throw new InvalidMachineException("User is not a technician");
        }
        this.technician = technician;
    }

}


