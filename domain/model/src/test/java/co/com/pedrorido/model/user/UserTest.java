package co.com.pedrorido.model.user;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserTest {
    @Test
    void shouldCreateUserSuccessfully() {
        // Arrange
        String id = "123";
        String name = "John";
        String surname = "Doe";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String address = "123 Some St";
        String phone = "555-1234";
        String email = "john.doe@example.com";
        String password = "password";
        BigDecimal baseSalary = BigDecimal.valueOf(5000000);
        Long roleId = 1L;
        String documentNumber = "123456";

        // Act
        User user = User.of(name, surname, birthDate, address, phone, email, baseSalary, roleId, documentNumber, password);

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getSurname()).isEqualTo(surname);
        assertThat(user.getBirthDate()).isEqualTo(birthDate);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getPhone()).isEqualTo(phone);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getBaseSalary()).isEqualTo(baseSalary);
        assertThat(user.getRoleId()).isEqualTo(roleId);
        assertThat(user.getDocumentNumber()).isEqualTo(documentNumber);
    }

    @Test
    void shouldThrowExceptionWhenNameIsNullOrEmpty() {
        // Arrange
        String name = null;

        // Act & Assert
        assertThatThrownBy(() ->
                User.of(
                        name,
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        "john.doe@example.com",
                        BigDecimal.valueOf(5000000),
                        1L,
                        "123456",
                        "password"
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name: required and non-empty");

        assertThatThrownBy(() ->
                User.of(
                        "   ",
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        "john.doe@example.com",
                        BigDecimal.valueOf(5000000),
                        1L,
                        "123456",
                        "password"
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name: required and non-empty");
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Arrange
        String invalidEmail = "not-an-email";

        // Act & Assert
        assertThatThrownBy(() ->
                User.of(
                        "John",
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        invalidEmail,
                        BigDecimal.valueOf(5000000),
                        1L,
                        "123456",
                        "password"
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email: invalid format");
    }

    @Test
    void shouldThrowExceptionWhenBaseSalaryIsOutOfRange() {
        // Act & Assert
        assertThatThrownBy(() ->
                User.of(
                        "John",
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        "john.doe@example.com",
                        BigDecimal.valueOf(-1),
                        1L,
                        "123456",
                        "password"
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("baseSalary: out of range [0, 15000000]");

        assertThatThrownBy(() ->
                User.of(
                        "John",
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        "john.doe@example.com",
                        BigDecimal.valueOf(20000000),
                        1L,
                        "123456",
                        "password"
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("baseSalary: out of range [0, 15000000]");
    }

    @Test
    void shouldAllowValidBaseSalary() {
        // Act & Assert
        BigDecimal minSalary = BigDecimal.ZERO;
        BigDecimal maxSalary = new BigDecimal("15000000");

        User userWithMinSalary = User.of(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Some St",
                "555-1234",
                "john.doe@example.com",
                minSalary,
                1L,
                "123456",
                "password"
        );

        User userWithMaxSalary = User.of(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Some St",
                "555-1234",
                "john.doe@example.com",
                maxSalary,
                1L,
                "123456",
                "password"
        );

        assertThat(userWithMinSalary.getBaseSalary()).isEqualTo(minSalary);
        assertThat(userWithMaxSalary.getBaseSalary()).isEqualTo(maxSalary);
    }

}