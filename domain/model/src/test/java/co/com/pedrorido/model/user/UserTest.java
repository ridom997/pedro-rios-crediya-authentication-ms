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
        BigDecimal baseSalary = BigDecimal.valueOf(5000000);
        String roleId = "admin";
        String documentNumber = "123456";

        // Act
        User user = User.of(id, name, surname, birthDate, address, phone, email, baseSalary, roleId, documentNumber);

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(id);
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
                        "123",
                        name,
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        "john.doe@example.com",
                        BigDecimal.valueOf(5000000),
                        "admin",
                        "123456"
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name: required and non-empty");

        assertThatThrownBy(() ->
                User.of(
                        "123",
                        "   ",
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        "john.doe@example.com",
                        BigDecimal.valueOf(5000000),
                        "admin",
                        "123456"
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
                        "123",
                        "John",
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        invalidEmail,
                        BigDecimal.valueOf(5000000),
                        "admin",
                        "123456"
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email: invalid format");
    }

    @Test
    void shouldThrowExceptionWhenBaseSalaryIsOutOfRange() {
        // Act & Assert
        assertThatThrownBy(() ->
                User.of(
                        "123",
                        "John",
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        "john.doe@example.com",
                        BigDecimal.valueOf(-1),
                        "admin",
                        "123456"
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("baseSalary: out of range [0, 15000000]");

        assertThatThrownBy(() ->
                User.of(
                        "123",
                        "John",
                        "Doe",
                        LocalDate.of(1990, 1, 1),
                        "123 Some St",
                        "555-1234",
                        "john.doe@example.com",
                        BigDecimal.valueOf(20000000),
                        "admin",
                        "123456"
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
                "123",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Some St",
                "555-1234",
                "john.doe@example.com",
                minSalary,
                "admin",
                "123456"
        );

        User userWithMaxSalary = User.of(
                "123",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Some St",
                "555-1234",
                "john.doe@example.com",
                maxSalary,
                "admin",
                "123456"
        );

        assertThat(userWithMinSalary.getBaseSalary()).isEqualTo(minSalary);
        assertThat(userWithMaxSalary.getBaseSalary()).isEqualTo(maxSalary);
    }

}