package co.com.pedrorido.api;

import co.com.pedrorido.api.dto.GeneralResponseDTO;
import co.com.pedrorido.api.dto.UserDTO;
import co.com.pedrorido.api.mapper.UserDTOMapper;
import co.com.pedrorido.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Component
@RequiredArgsConstructor
public class UserHandler {
    private final UserUseCase userUseCase;
    private final UserDTOMapper userDTOMapper;

    @Operation(
            summary = "Guardar usuario",
            description = "Recibe un objeto UserDTO en el cuerpo de la solicitud, lo procesa y guarda el usuario. Devuelve la información del usuario guardado junto con un mensaje de éxito.",
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
    public Mono<ResponseEntity<GeneralResponseDTO<UserDTO>>> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is required")))
                .map(userDTOMapper::toDomain)
                .flatMap(userUseCase::saveUser)
                .map(userDTOMapper::toDto)
                .map(savedUserDto -> {
                    HashMap<String, UserDTO> data = new HashMap<>();
                    data.put("user", savedUserDto);
                    return new ResponseEntity<>(
                            GeneralResponseDTO.<UserDTO>builder()
                                    .success(true)
                                    .message("User added successfully")
                                    .data(data)
                                    .build(),
                            HttpStatus.CREATED);
                });
    }
}
