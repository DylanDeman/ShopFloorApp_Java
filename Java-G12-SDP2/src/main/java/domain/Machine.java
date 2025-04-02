package domain;

import java.io.Serializable;
import java.util.Date;

import exceptions.InvalidMachineException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Machine implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    private Site site;
    
    private String status, productieStatus, locatie, productInfo;
    private Date laatsteOnderhoud, toekomstigOnderhoud;
    private int aantalDagenSindsLaatsteOnderhoud;

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

    public void setLocatie(String locatie) {
        if (locatie == null || locatie.isBlank()) {
            throw new InvalidMachineException("Location cannot be null or empty");
        }
        this.locatie = locatie.trim();
    }

    public void setProductInfo(String productInfo) {
        if (productInfo == null || productInfo.isBlank()) {
            throw new InvalidMachineException("Product info cannot be null or empty");
        }
        this.productInfo = productInfo.trim();
    }

    public void setLaatsteOnderhoud(Date laatsteOnderhoud) {
        if (laatsteOnderhoud == null) {
            throw new InvalidMachineException("Last maintenance date cannot be null");
        }
        if (laatsteOnderhoud.after(new Date())) {
            throw new InvalidMachineException("Last maintenance date cannot be in the future");
        }
        this.laatsteOnderhoud = laatsteOnderhoud;
    }

    public void setToekomstigOnderhoud(Date toekomstigOnderhoud) {
        if (toekomstigOnderhoud == null) {
            throw new InvalidMachineException("Future maintenance date cannot be null");
        }
        if (toekomstigOnderhoud.before(new Date())) {
            throw new InvalidMachineException("Future maintenance date cannot be in the past");
        }
        this.toekomstigOnderhoud = toekomstigOnderhoud;
    }

    public void setAantalDagenSindsLaatsteOnderhoud(int aantalDagenSindsLaatsteOnderhoud) {
        if (aantalDagenSindsLaatsteOnderhoud < 0) {
            throw new InvalidMachineException("Days since last maintenance cannot be negative");
        }
        this.aantalDagenSindsLaatsteOnderhoud = aantalDagenSindsLaatsteOnderhoud;
    }
}


