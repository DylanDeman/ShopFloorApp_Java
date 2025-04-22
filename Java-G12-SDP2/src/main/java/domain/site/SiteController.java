package domain.site;

import java.util.List;
import java.util.stream.Collectors;

import util.AuthenticationUtil;
import util.Role;

public class SiteController {
	// Misschien kan het aanmaken van de repo met een factory gedaan worden:
	// TODO feedback vragen hierover
	private SiteDao siteRepo;

	public SiteController() {
		siteRepo = new SiteDaoJpa();
	}

	public List<SiteDTO> getSites() {
		List<Site> sites = siteRepo.findAll();
		return makeSiteDTOs(sites);
	}

	public void setSiteNaam(int id, String name) {
		boolean hasRole = AuthenticationUtil.hasRole(Role.ADMIN);
		if (hasRole) {
			Site site = siteRepo.get(id);
			if (site != null) {
				site.setSiteName(name);
				siteRepo.update(site);
			}
		}
		// TODO hier een exceptie voor verboden toegang gooien
	}

	public List<SiteDTO> makeSiteDTOs(List<Site> sites) {
		return sites.stream().map(site -> new SiteDTO(site.getId(), site.getSiteName(), site.getVerantwoordelijke(),
				site.getMachines(), site.getStatus())).collect(Collectors.toUnmodifiableList());
	}

}
