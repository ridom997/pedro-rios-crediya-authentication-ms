package co.com.pedrorido.api;

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
public class UserRouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    beanClass = UserHandler.class,
                    beanMethod = "listenSaveUser"
            )
    })
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
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
        );
    }
}
