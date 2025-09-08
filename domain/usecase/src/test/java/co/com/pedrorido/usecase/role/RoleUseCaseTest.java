package co.com.pedrorido.usecase.role;



import co.com.pedrorido.model.role.Role;
import co.com.pedrorido.model.role.gateways.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RoleUseCaseTest {
    private RoleRepository roleRepository;
    private RoleUseCase roleUseCase;

    @BeforeEach
    void setUp() {
        // Mock del RoleRepository
        roleRepository = Mockito.mock(RoleRepository.class);

        // Instancia de RoleUseCase con el mock del repositorio
        roleUseCase = new RoleUseCase(roleRepository);
    }

    @Test
    void roleExistsById_shouldReturnTrueWhenRoleExists() {
        // Configurar el comportamiento del mock
        Long roleId = 1L;
        when(roleRepository.roleExistsById(roleId)).thenReturn(Mono.just(true));

        // Ejecutar el método
        Mono<Boolean> result = roleUseCase.roleExistsById(roleId);

        // Verificar el resultado con StepVerifier
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        // Verificar que el mock fue llamado con el argumento correcto
        verify(roleRepository, times(1)).roleExistsById(roleId);
    }

    @Test
    void roleExistsById_shouldReturnFalseWhenRoleDoesNotExist() {
        Long roleId = 1L;
        when(roleRepository.roleExistsById(roleId)).thenReturn(Mono.just(false));

        Mono<Boolean> result = roleUseCase.roleExistsById(roleId);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository, times(1)).roleExistsById(roleId);
    }

    @Test
    void findById_shouldReturnRoleWhenFound() {
        Long roleId = 1L;
        Role role = new Role("roleId", "Admin"); // Asumiendo que role tiene estos parámetros en su constructor
        when(roleRepository.findById(roleId)).thenReturn(Mono.just(role));

        Mono<Role> result = roleUseCase.findById(roleId);

        StepVerifier.create(result)
                .expectNext(role)
                .verifyComplete();

        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void findById_shouldReturnEmptyWhenRoleNotFound() {
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(Mono.empty());

        Mono<Role> result = roleUseCase.findById(roleId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(roleRepository, times(1)).findById(roleId);
    }


}