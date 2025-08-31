package co.com.pedrorido.usecase.user.api;

import co.com.pedrorido.model.user.User;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IUserApi {
    Mono<User> saveUser(User user);
    Mono<Map<String, Boolean>> userExistsByDocumentNumber(String documentNumber);
}
