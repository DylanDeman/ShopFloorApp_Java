package domain.Site;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SiteController {
	private SiteDao siteRepo = new SiteDaoJpa();
	
	
}
