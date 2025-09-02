package co.com.pedrorido.r2dbc;

import co.com.pedrorido.model.role.Role;
import co.com.pedrorido.model.role.gateways.RoleRepository;
import co.com.pedrorido.model.user.User;
import co.com.pedrorido.model.user.gateways.UserRepository;
import co.com.pedrorido.r2dbc.entity.RoleEntity;
import co.com.pedrorido.r2dbc.entity.UserEntity;
import co.com.pedrorido.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.log4j.Log4j2;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Log4j2
@Repository
public class RoleRepositoryAdapter extends ReactiveAdapterOperations<Role, RoleEntity, Long, RoleReactiveRepository> implements RoleRepository {
    public RoleRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }

    @Override
    public Mono<Boolean> roleExistsById(Long id) {
        log.info("Checking if role with id {} is registered", id);
        return repository.existsById(id);
    }
}
