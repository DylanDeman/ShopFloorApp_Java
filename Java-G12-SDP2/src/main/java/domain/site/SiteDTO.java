package domain.site;

import java.util.Set;

import domain.*;
import util.Status;

// TODO van User nog een UserDTO maken!!!
// TODO van Set<Machine> Set<MachineDTO> maken!!!
public record SiteDTO(int id, String siteName, User verantwoordelijke, Set<Machine> machines, Status status) {
}