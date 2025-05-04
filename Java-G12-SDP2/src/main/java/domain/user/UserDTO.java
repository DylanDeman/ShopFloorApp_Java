package domain.user;

import util.Role;
import util.Status;

public record UserDTO(int id, String firstName, String lastName, String email, Role role, Status status)
{
}
