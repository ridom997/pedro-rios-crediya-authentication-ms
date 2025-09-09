package co.com.pedrorido.usecase.role;

import co.com.pedrorido.model.role.Role;
import co.com.pedrorido.model.role.gateways.RoleRepository;
import co.com.pedrorido.usecase.role.api.IRoleApi;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RoleUseCase implements IRoleApi {
    private final RoleRepository roleRepository;

    @Override
    public Mono<Boolean> roleExistsById(Long id) {
        return roleRepository.roleExistsById(id);
    }

    @Override
    public Mono<Role> findById(Long id) {
        return roleRepository.findById(id);
    }
}
