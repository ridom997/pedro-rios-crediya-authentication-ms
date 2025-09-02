package co.com.pedrorido.cryptosecurity;

import co.com.pedrorido.model.security.TokenClaims;
import co.com.pedrorido.model.security.gateways.SecurityRepository;
import co.com.pedrorido.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements SecurityRepository {

    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.issuer:my-app}")
    private String issuer;

    @Value("${security.jwt.ttl-seconds:3600}")
    private long ttlSeconds;

    @Override
    public Mono<String> generate(User user, Set<String> roles) {
        return Mono.fromCallable(() -> {
                    Instant now = Instant.now();
                    Instant exp = now.plusSeconds(ttlSeconds);

                    JwtClaimsSet claims = JwtClaimsSet.builder()
                            .issuer(issuer)
                            .issuedAt(now)
                            .expiresAt(exp)
                            .subject(user.getEmail())
                            .claim("roles", roles)
                            .build();

                    String token = jwtEncoder
                            .encode(JwtEncoderParameters.from(claims))
                            .getTokenValue();

                    log.debug("JWT generado para {}", user.getEmail());
                    return token;
                })
                // Mueve el trabajo de firma a un hilo elástico (evita cargar el event-loop)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<TokenClaims> verify(String token) {
        return null;
    }
}
