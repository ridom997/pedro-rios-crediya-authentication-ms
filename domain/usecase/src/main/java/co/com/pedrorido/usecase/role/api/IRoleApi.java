package co.com.pedrorido.usecase.role.api;

import co.com.pedrorido.model.role.Role;
import reactor.core.publisher.Mono;

public interface IRoleApi {
    Mono<Boolean> roleExistsById(Long id);
    Mono<Role> findById(Long id);
}
