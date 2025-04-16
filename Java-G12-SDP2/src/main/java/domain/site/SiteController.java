package domain.site;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SiteController {
	// misschien kan het aanmaken van de repo met een factory gedaan worden:
	// feedback vragen hierover
	private SiteDao siteRepo = new SiteDaoJpa();
	
	public void getSites() {
		siteRepo.findAll();
	}
	
	public void setSiteNaam(int id, String name) {
		Site site = siteRepo.get(id);
		if(site != null) {
			site.setSiteName(name);
			siteRepo.update(site);
		}
	}
}
