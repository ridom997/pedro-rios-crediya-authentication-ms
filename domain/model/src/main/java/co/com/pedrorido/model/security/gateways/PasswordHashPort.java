package co.com.pedrorido.model.security.gateways;

import reactor.core.publisher.Mono;

public interface PasswordHashPort {
    Mono<Boolean> matches(String raw, String hashed);
    Mono<String> hash(String raw);
}
