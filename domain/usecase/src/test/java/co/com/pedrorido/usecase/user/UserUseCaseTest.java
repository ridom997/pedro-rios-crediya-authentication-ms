package co.com.pedrorido.usecase.user;

import co.com.pedrorido.model.user.User;
import co.com.pedrorido.model.user.gateways.UserRepository;
import co.com.pedrorido.usecase.role.RoleUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;


class UserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleUseCase roleUseCase;

    @InjectMocks
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User getMockedUser() {
        return User.builder()
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

    @Test
    void saveUser_Success() {
        User user = getMockedUser();

        when(userRepository.emailAlreadyRegistered(user.getEmail())).thenReturn(Mono.just(false));
        when(roleUseCase.roleExistsById(user.getRoleId())).thenReturn(Mono.just(true));
        when(userRepository.saveUser(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).emailAlreadyRegistered(user.getEmail());
        verify(roleUseCase).roleExistsById(user.getRoleId());
        verify(userRepository).saveUser(user);
    }

    @Test
    void saveUser_EmailAlreadyRegistered() {
        User user = getMockedUser();

        when(userRepository.emailAlreadyRegistered(user.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
                        throwable.getMessage().equals("email already registered"))
                .verify();

        verify(userRepository).emailAlreadyRegistered(user.getEmail());
        verifyNoInteractions(roleUseCase);
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void saveUser_RoleDoesNotExist() {
        User user = getMockedUser();

        when(userRepository.emailAlreadyRegistered(user.getEmail())).thenReturn(Mono.just(false));
        when(roleUseCase.roleExistsById(user.getRoleId())).thenReturn(Mono.just(false));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
                        throwable.getMessage().equals("role does not exist"))
                .verify();

        verify(userRepository).emailAlreadyRegistered(user.getEmail());
        verify(roleUseCase).roleExistsById(user.getRoleId());
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void userExistsByDocumentNumber_UserExists() {
        String documentNumber = "12345678";

        when(userRepository.userExistsByDocumentNumber(documentNumber)).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.userExistsByDocumentNumber(documentNumber))
                .expectNext(Map.of("userExists", true))
                .verifyComplete();

        verify(userRepository).userExistsByDocumentNumber(documentNumber);
    }

    @Test
    void userExistsByDocumentNumber_UserDoesNotExist() {
        String documentNumber = "12345678";

        when(userRepository.userExistsByDocumentNumber(documentNumber)).thenReturn(Mono.just(false));

        StepVerifier.create(userUseCase.userExistsByDocumentNumber(documentNumber))
                .expectNext(Map.of("userExists", false))
                .verifyComplete();

        verify(userRepository).userExistsByDocumentNumber(documentNumber);
    }

    @Test
    void findByEmail_UserFound() {
        User user = getMockedUser();
        String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.findByEmail(email))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).findByEmail(email);
    }

    @Test
    void findByEmail_UserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.findByEmail(email))
                .expectErrorMatches(throwable -> throwable instanceof NoSuchElementException &&
                        throwable.getMessage().equals("user not found"))
                .verify();

        verify(userRepository).findByEmail(email);
    }


}