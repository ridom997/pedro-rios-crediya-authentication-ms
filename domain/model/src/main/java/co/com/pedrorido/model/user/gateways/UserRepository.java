package co.com.pedrorido.model.user.gateways;

import co.com.pedrorido.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> saveUser(User user);
    Mono<Boolean> userExistsByDocumentNumber(String documentNumber);
    Mono<Boolean> emailAlreadyRegistered(String email);
    Mono<User> findByEmail(String email);
}
