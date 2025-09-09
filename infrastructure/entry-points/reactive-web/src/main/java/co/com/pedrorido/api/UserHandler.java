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


@Component
@RequiredArgsConstructor
@Log4j2
public class UserHandler {
    private final UserUseCase userUseCase;
    private final UserDTOMapper userDTOMapper;
    private final TransactionalOperator tx;


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

    public Mono<ResponseEntity<GeneralResponseDTO<UserDTO>>> listenGetUserByEmail(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.pathVariable("email"))
                .filter(StringUtils::hasText)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Missing 'email' query param")))
                .doOnNext(log::info)
                .flatMap(email -> userUseCase.findByEmail(email).as(tx::transactional))
                .map(userDTOMapper::toDto)
                .map(userDto -> {
                    HashMap<String, UserDTO> data = new HashMap<>();
                    data.put("user", userDto);
                    return new ResponseEntity<>(
                            GeneralResponseDTO.<UserDTO>builder()
                                    .success(true)
                                    .message("User found successfully")
                                    .data(data)
                                    .build(),
                            HttpStatus.OK);
                })
                .doOnSuccess(log::info)
                .doOnError(log::error);
    }
}
