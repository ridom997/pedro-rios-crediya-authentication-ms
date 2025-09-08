package co.com.pedrorido.r2dbc;

import co.com.pedrorido.model.user.User;
import co.com.pedrorido.model.user.gateways.UserRepository;
import co.com.pedrorido.r2dbc.entity.UserEntity;
import co.com.pedrorido.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.log4j.Log4j2;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Log4j2
@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, String, UserReactiveRepository> implements UserRepository {
    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    private final PasswordEncoder enc = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public Mono<User> saveUser(User user) {
        return hash(user.getPassword())
                .flatMap(hashedPassword -> {
                            user.setPassword(hashedPassword);
                            log.info("Saving user");
                            return super.save(user);
                        }
                );
    }

    @Override
    public Mono<Boolean> userExistsByDocumentNumber(String documentNumber) {
        log.info("Checking if user with documentNumber {} is registered", documentNumber);
        return repository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public Mono<Boolean> emailAlreadyRegistered(String email) {
        log.info("Checking if email {} is already registered", email);
        return repository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.info("Finding user with email {}", email);
        return repository.findByEmail(email);
    }

    private Mono<String> hash(String raw) {
        log.info("Hashing raw password");
        return Mono.fromCallable(() -> enc.encode(raw))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }
}
