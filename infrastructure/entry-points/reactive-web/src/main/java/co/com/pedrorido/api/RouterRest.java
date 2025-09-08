package co.com.pedrorido.api;

import co.com.pedrorido.api.dto.AuthRequest;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    beanClass = UserHandler.class,
                    beanMethod = "listenSaveUser"
            ),
            @RouterOperation(
                    path = "/api/v1/users/exist",
                    beanClass = UserHandler.class,
                    beanMethod = "existsByDocumentNumber"
            ),
            @RouterOperation(
                    path = "/api/v1/auth/login",
                    beanClass = AuthHandler.class,
                    beanMethod = "login"
            )
    })
    public RouterFunction<ServerResponse> routerFunction(AuthHandler authHandler, UserHandler userHandler) {
        return route(
                POST("/api/v1/usuarios")
                        .and(accept(MediaType.APPLICATION_JSON))
                        .and(contentType(MediaType.APPLICATION_JSON)),
                res -> userHandler.listenSaveUser(res)
                        .flatMap(re -> ServerResponse
                                .status(re.getStatusCode())
                                .headers(h -> h.addAll(re.getHeaders()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(re.getBody()))
        ).andRoute(
                    GET("/api/v1/users/exist")
                        .and(queryParam("documentNumber", v -> v != null && !v.isBlank())),
                req -> userHandler.listenUserExistsByDocumentNumber(req)
                        .flatMap(re -> ServerResponse
                                .status(re.getStatusCode())
                                .headers(h -> h.addAll(re.getHeaders()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(re.getBody()))
        ).andRoute(
                POST("/api/v1/login")
                        .and(accept(MediaType.APPLICATION_JSON))
                        .and(contentType(MediaType.APPLICATION_JSON)),
                req -> authHandler.login(req).flatMap(re -> ServerResponse
                        .status(re.getStatusCode())
                        .headers(h -> h.addAll(re.getHeaders()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(re.getBody()))
        );
    }
}
