package co.com.pedrorido.model.role.gateways;

import co.com.pedrorido.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Boolean> roleExistsById(Long id);
    Mono<Role> findById(Long id);
}
