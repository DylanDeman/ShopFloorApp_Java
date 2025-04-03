package domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.NONE)
@AllArgsConstructor
@EqualsAndHashCode(of= "siteNaam")
public class Site implements Serializable{
	

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	private String siteNaam;
	
	private User verantwoordelijke;
	
	@OneToMany(mappedBy = "site")
	private Set<Machine> machines = new HashSet<>();
	
	
	
		public Set<Machine> getMachines(){
		return Collections.unmodifiableSet(machines);
	}
		
		
		public void addMachines(Machine m) {
			machines.add(m);
		}
		
		

}
