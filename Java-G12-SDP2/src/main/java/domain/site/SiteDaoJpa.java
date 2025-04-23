package domain.site;

import repository.GenericDaoJpa;

// Mag later weg indien geen methodes bijkomen
// Dit heb ik even in deze folder gelaten voor gemak
public class SiteDaoJpa extends GenericDaoJpa<Site> implements SiteDao {
	public SiteDaoJpa() {
		super(Site.class);
	}
}
