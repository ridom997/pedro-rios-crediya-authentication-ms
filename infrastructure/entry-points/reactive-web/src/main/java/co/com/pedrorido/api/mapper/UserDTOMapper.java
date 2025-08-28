package co.com.pedrorido.api.mapper;

import co.com.pedrorido.api.dto.UserDTO;
import co.com.pedrorido.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserDTOMapper {
    default User toDomain(UserDTO dto) {
        if (dto == null) throw new IllegalArgumentException("UserDTO is null");
        return User.of(
                null,
                dto.getName(),
                dto.getSurname(),
                dto.getBirthDate(),
                dto.getAddress(),
                dto.getPhone(),
                dto.getEmail(),
                dto.getBaseSalary(),
                dto.getRoleId(),
                dto.getDocumentNumber()
        );
    }

    default UserDTO toDto(User u) {
        if (u == null) throw new IllegalArgumentException("User is null");
        return UserDTO.builder()
                .id(u.getId())
                .name(u.getName())
                .surname(u.getSurname())
                .birthDate(u.getBirthDate())
                .address(u.getAddress())
                .phone(u.getPhone())
                .email(u.getEmail())
                .baseSalary(u.getBaseSalary())
                .roleId(u.getRoleId())
                .documentNumber(u.getDocumentNumber())
                .build();
    }
}
