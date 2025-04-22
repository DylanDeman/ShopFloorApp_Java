package domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import domain.site.Site;
import exceptions.InformationRequiredException;
import exceptions.InvalidMachineException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import util.RequiredElementMachine;

@Entity
@ToString
@Getter
public class Machine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	private final Site site;

	@ManyToOne
	private final User technician;

	private final String code, status, productieStatus, location, productInfo;
	private final LocalDateTime lastMaintenance, futureMaintenance;
	private final int numberDaysSinceLastMaintenance = 0;
	private final double upTimeInHours = 0;

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
	}
	
	private Machine() {
		this.site = new Site();
		this.technician = null;
		this.code = "";
		this.status = "";
		this.productieStatus = "";
		this.location = "";
		this.productInfo = "";
		this.lastMaintenance = null;
		this.futureMaintenance = null;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Site site;
		private User technician;
		private String code, status, productieStatus, location, productInfo;
		private LocalDateTime lastMaintenance, futureMaintenance;
		private Set<RequiredElementMachine> requiredElements;

		public Builder site(Site site) {
			this.site = site;
			return this;
		}

		public Builder technician(User technician) {
			this.technician = technician;
			return this;
		}

		public Builder code(String code) {
			this.code = code;
			return this;
		}

		public Builder status(String status) {
			this.status = status;
			return this;
		}

		public Builder productieStatus(String productieStatus) {
			this.productieStatus = productieStatus;
			return this;
		}

		public Builder location(String location) {
			this.location = location;
			return this;
		}

		public Builder productInfo(String productInfo) {
			this.productInfo = productInfo;
			return this;
		}

		public Builder lastMaintenance(LocalDateTime lastMaintenance) {
			this.lastMaintenance = lastMaintenance;
			return this;
		}

		public Builder futureMaintenance(LocalDateTime futureMaintenance) {
			this.futureMaintenance = futureMaintenance;
			return this;
		}

		public Machine build() throws InformationRequiredException {
			requiredElements = new HashSet<>();

			Machine machine = new Machine(this);

			if (machine.site == null) {
				requiredElements.add(RequiredElementMachine.SITE_REQUIRED);
			}

			if (machine.technician == null) {
				requiredElements.add(RequiredElementMachine.TECHNICIAN_REQUIRED);
			}

			if (machine.code == null) {
				requiredElements.add(RequiredElementMachine.CODE_REQUIRED);
			}

			if (machine.status == null) {
				requiredElements.add(RequiredElementMachine.STATUS_REQUIRED);
			}

			if (machine.productieStatus == null) {
				requiredElements.add(RequiredElementMachine.PRODUCTIE_STATUS_REQUIRED);
			}

			if (machine.location == null) {
				requiredElements.add(RequiredElementMachine.LOCATION_REQUIRED);
			}

			if (machine.productInfo == null) {
				requiredElements.add(RequiredElementMachine.PRODUCT_INFO_REQUIRED);
			}

			if (machine.lastMaintenance == null) {
				requiredElements.add(RequiredElementMachine.LAST_MAINTENANCE_REQUIRED);
			}

			if (machine.futureMaintenance == null) {
				requiredElements.add(RequiredElementMachine.FUTURE_MAINTENANCE_REQUIRED);
			}
			return machine;
		}
	}

	public void setSite(Site site2) {
		this.setSite(site2);
	}

}
