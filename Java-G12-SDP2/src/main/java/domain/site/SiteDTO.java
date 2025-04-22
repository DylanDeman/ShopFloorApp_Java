package domain.site;

import java.util.Set;

import domain.machine.Machine;
import domain.user.User;
import util.Status;


// Dit moet nog in een package DTO, maar nu even voor gemak hierin gezet!!!
// TODO van User nog een UserDTO maken!!!
// TODO van Set<Machine> Set<MachineDTO> maken!!!
public record SiteDTO(int id, String siteName, User verantwoordelijke, Set<Machine> machines, Status status) {
}