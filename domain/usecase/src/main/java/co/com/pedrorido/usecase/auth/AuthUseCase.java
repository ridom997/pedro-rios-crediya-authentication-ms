package co.com.pedrorido.usecase.auth;

import co.com.pedrorido.model.role.Role;
import co.com.pedrorido.model.role.gateways.RoleRepository;
import co.com.pedrorido.model.security.AuthResponse;
import co.com.pedrorido.model.security.gateways.PasswordHashPort;
import co.com.pedrorido.model.security.gateways.SecurityRepository;
import co.com.pedrorido.model.user.gateways.UserRepository;
import co.com.pedrorido.usecase.auth.api.IAuthApi;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Set;

@RequiredArgsConstructor
public class AuthUseCase implements IAuthApi {
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashPort passwordHashPort;

    public Mono<AuthResponse> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("bad_credentials")))
                .flatMap(u -> passwordHashPort.matches(rawPassword, u.getPassword())
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("bad_credentials")))
                        .then(roleRepository.findById(u.getRoleId()))
                        .map(Role::getName)
                        .flatMap(roleName -> securityRepository.generate(u, Set.of(roleName))
                                .map(t -> new AuthResponse(t, Set.of(roleName))))
                );
    }
}
