package co.com.pedrorido.usecase.user;

import co.com.pedrorido.model.user.User;
import co.com.pedrorido.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;


class UserUseCaseTest {
    private UserUseCase userUseCase;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userUseCase = new UserUseCase(userRepository);
    }

    @Test
    void shouldThrowErrorWhenEmailAlreadyRegistered() {
        // Given
        User user = getMockedUser();
        when(userRepository.emailAlreadyRegistered(user.getEmail())).thenReturn(Mono.just(true));

        // When
        Mono<User> result = userUseCase.saveUser(user);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();

        verify(userRepository, times(1)).emailAlreadyRegistered(user.getEmail());
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void shouldSaveUserWhenEmailNotRegistered() {
        // Given
        User user = getMockedUser();
        when(userRepository.emailAlreadyRegistered(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.saveUser(user)).thenReturn(Mono.just(user));

        // When
        Mono<User> result = userUseCase.saveUser(user);

        // Then
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(userRepository, times(1)).emailAlreadyRegistered(user.getEmail());
        verify(userRepository, times(1)).saveUser(user);
    }

    @Test
    void shouldPropagateErrorFromSaveUser() {
        // Given
        User user = getMockedUser();
        when(userRepository.emailAlreadyRegistered(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.saveUser(user)).thenReturn(Mono.error(new RuntimeException("Save failed")));

        // When
        Mono<User> result = userUseCase.saveUser(user);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository, times(1)).emailAlreadyRegistered(user.getEmail());
        verify(userRepository, times(1)).saveUser(user);
    }

    private User getMockedUser() {
        return User.builder()
                .id("12345")
                .name("Pedro")
                .surname("Rido")
                .birthDate(LocalDate.of(1990, 1, 15))
                .address("Calle Falsa 123")
                .phone("3001234567")
                .email("pedro.rido@example.com")
                .baseSalary(new BigDecimal("3500.75"))
                .roleId(1L)
                .build();
    }
}