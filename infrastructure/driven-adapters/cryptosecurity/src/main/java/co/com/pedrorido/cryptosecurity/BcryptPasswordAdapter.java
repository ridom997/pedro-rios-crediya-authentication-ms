package co.com.pedrorido.cryptosecurity;

import co.com.pedrorido.model.security.gateways.PasswordHashPort;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class BcryptPasswordAdapter implements PasswordHashPort {

    private final PasswordEncoder enc = new BCryptPasswordEncoder();

    @Override
    public Mono<Boolean> matches(String raw, String hashed) {
        log.info("Checking if raw password matches hashed password");
        return Mono.fromCallable(() -> enc.matches(raw, hashed))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    @Override
    public Mono<String> hash(String raw) {
        log.info("Hashing raw password");
        return Mono.fromCallable(() -> enc.encode(raw))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }
}
