package co.com.pedrorido.usecase.user.api;

import co.com.pedrorido.model.user.User;
import reactor.core.publisher.Mono;

public interface IUserApi {
    Mono<User> saveUser(User user);
}
