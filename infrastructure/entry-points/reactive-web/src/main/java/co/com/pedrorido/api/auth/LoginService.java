package co.com.pedrorido.api.auth;

import co.com.pedrorido.model.user.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class LoginService {
    private final BCryptPasswordEncoder encoder;

    public Mono<User> validatePassword(String password, User user) {
        boolean matches = encoder.matches(password, user.getPassword());
        if (!matches) return Mono.error(new IllegalArgumentException("bad_credentials"));
        return Mono.just(user);
    }

}
