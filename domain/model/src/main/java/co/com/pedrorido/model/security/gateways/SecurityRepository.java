package co.com.pedrorido.model.security.gateways;

import co.com.pedrorido.model.security.TokenClaims;
import co.com.pedrorido.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface SecurityRepository {
    Mono<String> generate(User user, Set<String> roles);
    Mono<TokenClaims> verify(String token);
}
