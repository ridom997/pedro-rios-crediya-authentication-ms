package co.com.pedrorido.api;

import co.com.pedrorido.api.dto.GeneralResponseDTO;
import co.com.pedrorido.api.dto.UserDTO;
import co.com.pedrorido.api.mapper.UserDTOMapper;
import co.com.pedrorido.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.StringUtils;
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
@Log4j2
public class UserHandler {
    private final UserUseCase userUseCase;
    private final UserDTOMapper userDTOMapper;
    private final TransactionalOperator tx;

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
                .doOnNext(log::info)
                .map(userDTOMapper::toDomain)
                .flatMap(domainUser -> {
                    return userUseCase.saveUser(domainUser).as(tx::transactional);
                })
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
                })
                .doOnSuccess(log::info)
                .doOnError(log::error);
    }

    @Operation(
            summary = "Verificar existencia de un usuario por número de documento",
            description = "Comprueba si un usuario existe en el sistema proporcionado su número de documento. Devuelve un valor booleano indicando la existencia del usuario.",
            requestBody = @RequestBody(
                    description = "Número de documento del usuario",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "La solicitud fue exitosa y la respuesta contiene un valor booleano indicando si el usuario existe.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeneralResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "El usuario no fue encontrado o no existe.",
                            content = @Content
                    )
            }
    )

    public Mono<ResponseEntity<GeneralResponseDTO<Boolean>>> listenUserExistsByDocumentNumber(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.queryParam("documentNumber"))
                .filter(StringUtils::hasText)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Missing 'documentNumber' query param")))
                .doOnNext(log::info)
                .flatMap(doc -> userUseCase.userExistsByDocumentNumber(doc).as(tx::transactional))
                .map(existMap -> {
                    return new ResponseEntity<>(
                            GeneralResponseDTO.<Boolean>builder()
                                    .success(true)
                                    .message("User existence validated successfully")
                                    .data(existMap)
                                    .build(),
                            existMap.get("userExists") ? HttpStatus.OK : HttpStatus.NOT_FOUND);
                })
                .doOnSuccess(log::info)
                .doOnError(log::error);
    }
}
