package co.com.pedrorido.api.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;


    @Value("${security.jwt.expirationSeconds}")
    private long expirationSeconds;

    public Mono<String> generateToken(String subject, List<String> roles, Map<String, Object> extraClaims) {
        return Mono.fromCallable(() -> {
            Instant now = Instant.now();
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(expirationSeconds)))
                    .claim("roles", roles);

            if (extraClaims != null) extraClaims.forEach(builder::claim);

            JWTClaimsSet claims = builder.build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
            SignedJWT signedJWT = new SignedJWT(header, claims);

            byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
            JWSSigner signer = new MACSigner(new SecretKeySpec(secretBytes, "HmacSHA256"));
            signedJWT.sign(signer);

            return signedJWT.serialize();
        });
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}