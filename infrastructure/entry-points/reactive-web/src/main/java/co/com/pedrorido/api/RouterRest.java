package co.com.pedrorido.api;

import co.com.pedrorido.api.dto.AuthRequest;
import co.com.pedrorido.api.dto.GeneralResponseDTO;
import co.com.pedrorido.api.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
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
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "listenSaveUser",
                    operation = @Operation(
                            operationId = "SaveUser",
                            summary = "Crear usuario",
                            security = @SecurityRequirement(name = "bearerAuth"),
                            requestBody = @RequestBody(
                                    description = "Información del usuario a guardar",
                                    required = true,
                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "El usuario se agregó correctamente.",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeneralResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "El cuerpo de la solicitud es inválido.",
                                            content = @Content
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Fallo funcional.",
                                            content = @Content
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/auth/login",
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            summary = "login",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Credenciales de acceso del usuario",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = AuthRequest.class
                                            )
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Inicio de sesión exitoso",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = GeneralResponseDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Credenciales inválidas",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(example = "{\"error\": \"bad_credentials\"}")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor"
                                    )
                            }
                    )
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
        ).andRoute(
                GET("/api/v1/admin/users/{email}"),
                req -> userHandler.listenGetUserByEmail(req)
                        .flatMap(re -> ServerResponse
                                .status(re.getStatusCode())
                                .headers(h -> h.addAll(re.getHeaders()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(re.getBody()))
        );
    }
}
