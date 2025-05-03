package domain.site;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import domain.machine.Machine;
import domain.machine.MachineDTO;
import util.AuthenticationUtil;
import util.Role;

public class SiteController
{
	// Misschien kan het aanmaken van de repo met een factory gedaan worden:
	// TODO feedback vragen hierover
	private SiteDao siteRepo;

	public SiteController()
	{
		siteRepo = new SiteDaoJpa();
	}

	public Site getSite(int id)
	{
		return siteRepo.get(id);
	}

	public List<SiteDTO> getSites()
	{
		// Kijken of ingelogde user de rol ADMIN bevat
		boolean hasRole = AuthenticationUtil.hasRole(Role.ADMIN);

		// Als hij deze rol bevat kan hij deze actie dus uitvoeren en krijgen we alle
		// sites terug
		if (hasRole)
		{
			List<Site> sites = siteRepo.findAll();
			return makeSiteDTOs(sites);
		}

		// Als hij deze rol niet bevat geven we een lege lijst terug
		return new ArrayList<>();
	}

	public void setSiteNaam(int id, String name)
	{
		boolean hasRole = AuthenticationUtil.hasRole(Role.ADMIN);
		if (hasRole)
		{
			Site site = siteRepo.get(id);
			if (site != null)
			{
				site.setSiteName(name);
				siteRepo.update(site);
			}
		}
		// TODO hier een exceptie voor verboden toegang gooien
	}

	/*
	 * public List<SiteDTO> makeSiteDTOs(List<Site> sites) { return
	 * sites.stream().map(site -> new SiteDTO(site.getId(), site.getSiteName(),
	 * site.getVerantwoordelijke(), site.getMachines(),
	 * site.getStatus())).collect(Collectors.toUnmodifiableList()); }
	 */

	public List<SiteDTO> makeSiteDTOs(List<Site> sites)
	{
		return sites.stream().map(site -> {
			Set<MachineDTO> machineDTOs = toMachineDTOs(site.getMachines());

			return new SiteDTO(site.getId(), site.getSiteName(), site.getVerantwoordelijke(), // later UserDTO
					machineDTOs, site.getStatus(), site.getAddress());
		}).collect(Collectors.toUnmodifiableList());
	}

	private Set<MachineDTO> toMachineDTOs(Set<Machine> machines)
	{
		return machines.stream().map(machine -> new MachineDTO(machine.getId(), null, // Or use a SiteDTO if available.
																						// Avoid circular references!
				machine.getTechnician(), // Later UserDTO
				machine.getCode(), machine.getStatus(), machine.getProductieStatus(), machine.getLocation(),
				machine.getProductInfo(), machine.getLastMaintenance(), machine.getFutureMaintenance(),
				machine.getNumberDaysSinceLastMaintenance(), machine.getUpTimeInHours())).collect(Collectors.toSet());
	}

}
