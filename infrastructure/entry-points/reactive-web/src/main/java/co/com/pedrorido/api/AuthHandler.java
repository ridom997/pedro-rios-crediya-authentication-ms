package co.com.pedrorido.api;

import co.com.pedrorido.api.auth.JwtService;
import co.com.pedrorido.api.auth.LoginService;
import co.com.pedrorido.api.dto.AuthRequest;
import co.com.pedrorido.api.dto.GeneralResponseDTO;
import co.com.pedrorido.model.role.Role;
import co.com.pedrorido.usecase.role.RoleUseCase;
import co.com.pedrorido.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class AuthHandler {

    private final JwtService jwtService;
    private final UserUseCase userUseCase;
    private final RoleUseCase roleUseCase;
    private final LoginService loginService;


    public Mono<ResponseEntity<GeneralResponseDTO<String>>> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AuthRequest.class)
                .flatMap(req -> {
                    return userUseCase.findByEmail(req.email())
                            .doOnError(NoSuchElementException.class, ex ->
                                    Mono.error(new IllegalArgumentException("bad_credentials"))
                            )
                            .flatMap(user -> {
                                return loginService.validatePassword(req.password(), user).flatMap(u -> {
                                            return roleUseCase.findById(u.getRoleId());
                                        })
                                        .map(Role::getName)
                                        .flatMap(role -> {
                                            return jwtService.generateToken(req.email(), Arrays.asList(role), Map.of("scope", "read write", "id", user.getDocumentNumber()))
                                                    .flatMap(token -> {
                                                        HashMap<String, String> data = new HashMap<>();
                                                        data.put("role", role);
                                                        data.put("token", token);
                                                        return Mono.just(new ResponseEntity<>(
                                                                GeneralResponseDTO.<String>builder()
                                                                        .success(true)
                                                                        .message("login was successful")
                                                                        .data(data)
                                                                        .build(),
                                                                HttpStatus.OK));
                                                    });
                                        });
                            });
                });
    }
}