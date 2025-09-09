package co.com.pedrorido.usecase.user;

import co.com.pedrorido.model.user.User;
import co.com.pedrorido.model.user.gateways.UserRepository;
import co.com.pedrorido.usecase.role.RoleUseCase;
import co.com.pedrorido.usecase.user.api.IUserApi;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class UserUseCase implements IUserApi {
    private final UserRepository userRepository;
    private final RoleUseCase roleUseCase;

    @Override
    public Mono<User> saveUser(User user) {
        return userRepository.emailAlreadyRegistered(user.getEmail())
                .flatMap(exists -> {
                    if (exists.booleanValue()) return Mono.error(new IllegalStateException("email already registered"));
                    return roleUseCase.roleExistsById(user.getRoleId());
                }).flatMap(exists -> {
                    if (!exists.booleanValue()) return Mono.error(new IllegalStateException("role does not exist"));
                    return userRepository.saveUser(user);
                });
    }

    @Override
    public Mono<Map<String, Boolean>> userExistsByDocumentNumber(String documentNumber) {
        return userRepository.userExistsByDocumentNumber(documentNumber)
                .flatMap(exists -> {
                    Map<String, Boolean> map = Map.of("userExists", exists);
                    return Mono.just(map);
                });
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new NoSuchElementException("user not found")));
    }
}
