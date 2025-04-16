package domain.Site;

import repository.GenericDaoJpa;

public class SiteDaoJpa extends GenericDaoJpa<Site> implements SiteDao {

	public SiteDaoJpa() {
		super(Site.class);
	}
	
}
