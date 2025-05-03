package domain.site;

import java.util.Set;

import domain.Address;
import domain.machine.MachineDTO;
import domain.user.User;
import util.Status;

// Dit moet nog in een package DTO, maar nu even voor gemak hierin gezet!!!
// TODO van User nog een UserDTO maken!!!
public record SiteDTO(int id, String siteName, User verantwoordelijke, Set<MachineDTO> machines, Status status,
		Address address)
{
}
