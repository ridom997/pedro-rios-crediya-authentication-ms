package co.com.pedrorido.usecase.auth.api;

import co.com.pedrorido.model.security.AuthResponse;
import reactor.core.publisher.Mono;

public interface IAuthApi {
    Mono<AuthResponse> login(String email, String rawPassword);
}
