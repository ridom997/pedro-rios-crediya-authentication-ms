package co.com.pedrorido.r2dbc;

import co.com.pedrorido.model.user.User;
import co.com.pedrorido.model.user.gateways.UserRepository;
import co.com.pedrorido.r2dbc.entity.UserEntity;
import co.com.pedrorido.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.log4j.Log4j2;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Log4j2
@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, String, UserReactiveRepository> implements UserRepository {
    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    @Transactional
    public Mono<User> saveUser(User user) {
        log.info("Saving user {}", user);
        return super.save(user);
    }

    @Override
    public Mono<User> findUserById(String idUser) {
        log.info("Finding user by id {}", idUser);
        return super.findById(idUser);
    }

    @Override
    public Mono<Boolean> emailAlreadyRegistered(String email) {
        log.info("Checking if email {} is already registered", email);
        return repository.existsByEmailIgnoreCase(email);
    }
}
