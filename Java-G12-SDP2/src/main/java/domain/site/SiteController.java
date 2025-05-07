package domain.site;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import domain.machine.Machine;
import domain.machine.MachineDTO;

public class SiteController {
	private SiteDao siteRepo;

	public SiteController() {
		siteRepo = new SiteDaoJpa();
	}

	public SiteDTO getSite(int id) {
		Site site = siteRepo.get(id);
		return makeSiteDTO(site);
	}

	public List<SiteDTO> getSites() {
		List<Site> sites = siteRepo.findAll();
		if (sites == null) {
			return new ArrayList<>();
		}
		return makeSiteDTOs(sites);
	}

	public List<Site> getSiteObjects() {
		return siteRepo.findAll();
	}

	public List<SiteDTO> makeSiteDTOs(List<Site> sites) {
		return sites.stream().map(site -> {
			Set<MachineDTO> machineDTOs = toMachineDTOs(site.getMachines());
			return new SiteDTO(site.getId(), site.getSiteName(), site.getVerantwoordelijke(), machineDTOs,
					site.getStatus(), site.getAddress());
		}).collect(Collectors.toUnmodifiableList());
	}

	public SiteDTO makeSiteDTO(Site site) {
		Set<MachineDTO> machineDTOs = toMachineDTOs(site.getMachines());
		return new SiteDTO(site.getId(), site.getSiteName(), site.getVerantwoordelijke(), machineDTOs, site.getStatus(),
				site.getAddress());
	}

	public MachineDTO makeMachineDTO(Machine machine) {
		return new MachineDTO(machine.getId(), null, machine.getTechnician(), machine.getCode(),
				machine.getMachineStatus(), machine.getProductionStatus(), machine.getLocation(),
				machine.getProductInfo(), machine.getLastMaintenance(), machine.getFutureMaintenance(),
				machine.getNumberDaysSinceLastMaintenance(), machine.getUpTimeInHours());
	}

	private Set<MachineDTO> toMachineDTOs(Set<Machine> machines) {
		return machines.stream()
				.map(machine -> new MachineDTO(machine.getId(), null, machine.getTechnician(), machine.getCode(),
						machine.getMachineStatus(), machine.getProductionStatus(), machine.getLocation(),
						machine.getProductInfo(), machine.getLastMaintenance(), machine.getFutureMaintenance(),
						machine.getNumberDaysSinceLastMaintenance(), machine.getUpTimeInHours()))
				.collect(Collectors.toSet());
	}

	public Site getSiteObject(SiteDTO site) {
		return siteRepo.get(site.id());
	}

	public List<SiteDTO> getFilteredSites(String searchFilter, String statusFilter, String siteNameFilter, String verantwoordelijkeFilter,
	        Integer minMachinesFilter, Integer maxMachinesFilter) {

	    String lowerCaseSearchFilter = searchFilter == null ? "" : searchFilter.toLowerCase();

	    return getSites().stream()
	            .filter(site -> statusFilter == null || site.status().toString().equals(statusFilter))
	            .filter(site -> siteNameFilter == null || site.siteName().toLowerCase().contains(siteNameFilter.toLowerCase()))
	            .filter(site -> verantwoordelijkeFilter == null || site.verantwoordelijke().getFullName().equals(verantwoordelijkeFilter))
	            .filter(site -> site.machines().size() >= minMachinesFilter && site.machines().size() <= maxMachinesFilter)
	            .filter(site -> site.siteName().toLowerCase().contains(lowerCaseSearchFilter)
	                    || site.verantwoordelijke().getFirstName().toLowerCase().contains(lowerCaseSearchFilter)
	                    || site.verantwoordelijke().getLastName().toLowerCase().contains(lowerCaseSearchFilter)
	                    || site.status().toString().toLowerCase().contains(lowerCaseSearchFilter))
	            .collect(Collectors.toList());
	}


	public List<String> getAllStatusses() {
		List<SiteDTO> allSites = getSites();
		return allSites.stream().map(s -> s.status().toString()).distinct().sorted().collect(Collectors.toList());
	}

	public List<String> getAllSiteNames() {
		List<SiteDTO> allSites = getSites();
		return allSites.stream().map(SiteDTO::siteName).distinct().sorted().collect(Collectors.toList());
	}

	public List<String> getAllVerantwoordelijken() {
		List<SiteDTO> allSites = getSites();
		return allSites.stream().map(s -> s.verantwoordelijke().getFullName()).distinct().sorted()
				.collect(Collectors.toList());
	}
}