package util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import domain.Address;
import domain.machine.Machine;
import domain.site.Site;
import domain.user.User;
import dto.AddressDTO;
import dto.MachineDTO;
import dto.SiteDTO;
import dto.SiteDTOWithMachines;
import dto.SiteDTOWithoutMachines;
import dto.UserDTO;

public class DTOMapper {

	public static AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        
        return new AddressDTO(
            address.getId(),
            address.getStreet(),
            address.getNumber(),
            address.getPostalcode(),
            address.getCity()
        );
    }
    
    public static Address toAddress(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Address address = new Address();
        address.setId(dto.id());
        address.setStreet(dto.street());
        address.setNumber(dto.number());
        address.setPostalcode(dto.postalcode());
        address.setCity(dto.city());
        
        return address;
    }
    
    public static UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getBirthdate(),
            toAddressDTO(user.getAddress()),
            user.getRole(),
            user.getStatus(), user.getPassword()
        );
    }
    
    public static List<UserDTO> toUserDTOs(List<User> users) {
        if (users == null) {
            return List.of();
        }
        
        return users.stream()
            .map(DTOMapper::toUserDTO)
            .collect(Collectors.toList());
    }
    
    public static User toUser(UserDTO dto, User existingUser) {
        if (dto == null) {
            return null;
        }
        
        User user = existingUser != null ? existingUser : new User();
        user.setId(dto.id());
        user.setBirthdate(dto.birDate());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPhoneNumber(dto.phoneNumber());
        user.setAddress(toAddress(dto.address()));
        user.setRole(dto.role());
        user.setStatus(dto.status());
        user.setPassword(dto.password());
        
        return user;
    }
    public static Site toSite(SiteDTOWithoutMachines dto, Site existingSite) {
        if (dto == null) {
            return null;
        }
        
        Site site = existingSite != null ? existingSite : new Site();
        site.setId(dto.id());
        site.setSiteName(dto.siteName());
        
        site.setAddress(toAddress(dto.address()));
        
        if (dto.verantwoordelijke() != null) {
            site.setVerantwoordelijke(toUser(dto.verantwoordelijke(), null));
        }
        
        site.setStatus(dto.status());
        
        return site;
    }

    
    public static SiteDTOWithoutMachines toSiteDTOWithoutMachines(Site site) {
        if (site == null) {
            return null;
        }
        
        return new SiteDTOWithoutMachines(
            site.getId(),
            site.getSiteName(),
            toUserDTO(site.getVerantwoordelijke()),
            site.getStatus(),
            toAddressDTO(site.getAddress())
        );
    }
    
    public static List<SiteDTOWithoutMachines> toSiteDTOsWithoutMachines(List<Site> sites) {
        if (sites == null) {
            return List.of();
        }
        
        return sites.stream()
            .map(DTOMapper::toSiteDTOWithoutMachines)
            .collect(Collectors.toList());
    }
    
    public static SiteDTOWithMachines toSiteDTOWithMachines(Site site) {
        if (site == null) {
            return null;
        }
        
        SiteDTOWithoutMachines siteDTOWithoutMachines = toSiteDTOWithoutMachines(site);
        
        Set<MachineDTO> machineDTOs = site.getMachines().stream()
            .map(machine -> toMachineDTO(machine, siteDTOWithoutMachines))
            .collect(Collectors.toSet());
        
        return new SiteDTOWithMachines(
            site.getId(),
            site.getSiteName(),
            toUserDTO(site.getVerantwoordelijke()),
            machineDTOs,
            site.getStatus(),
            toAddressDTO(site.getAddress())
        );
    }
    
    public static List<SiteDTOWithMachines> toSiteDTOsWithMachines(List<Site> sites) {
        if (sites == null) {
            return List.of();
        }
        
        return sites.stream()
            .map(DTOMapper::toSiteDTOWithMachines)
            .collect(Collectors.toList());
    }
    
    public static MachineDTO toMachineDTO(Machine machine, SiteDTOWithoutMachines siteDTO) {
        if (machine == null) {
            return null;
        }
        
        return new MachineDTO(
            machine.getId(),
            siteDTO,
            toUserDTO(machine.getTechnician()),
            machine.getCode(),
            machine.getMachineStatus(),
            machine.getProductionStatus(),
            machine.getLocation(),
            machine.getProductInfo(),
            machine.getLastMaintenance(),
            machine.getFutureMaintenance(),
            machine.getNumberDaysSinceLastMaintenance(),
            machine.getUpTimeInHours()
        );
    }
    
    public static List<MachineDTO> toMachineDTOs(List<Machine> machines, SiteDTOWithoutMachines siteDTO) {
        if (machines == null) {
            return List.of();
        }
        
        return machines.stream()
            .map(machine -> toMachineDTO(machine, siteDTO))
            .collect(Collectors.toList());
    }
    
    public static Machine toMachine(MachineDTO dto, Machine existingMachine, Site site) {
        if (dto == null) {
            return null;
        }
        
        Machine machine = existingMachine != null ? existingMachine : new Machine();
        machine.setId(dto.id());
        machine.setSite(site);
        machine.setTechnician(dto.technician() != null ? toUser(dto.technician(), null) : null);
        machine.setCode(dto.code());
        machine.setMachineStatus(dto.machineStatus());
        machine.setProductionStatus(dto.productionStatus());
        machine.setLocation(dto.location());
        machine.setProductInfo(dto.productInfo());
        machine.setLastMaintenance(dto.lastMaintenance());
        machine.setFutureMaintenance(dto.futureMaintenance());
        machine.setNumberDaysSinceLastMaintenance(dto.numberDaysSinceLastMaintenance());
        
        return machine;
    }
}