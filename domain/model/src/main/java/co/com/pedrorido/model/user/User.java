package co.com.pedrorido.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String address;
    private String phone;
    private String email;
    private BigDecimal baseSalary;
    private String roleId;
    private String documentNumber;

    private static final Pattern EMAIL_RX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static User of(
            String id, String name, String surname, LocalDate birthDate,
            String address, String phone, String email,
            BigDecimal baseSalary, String roleId, String documentNumber
    ) {
        requireText("name", name);
        requireText("surname", surname);
        requireText("email", email);
        Objects.requireNonNull(baseSalary, "baseSalary is required");

        if (!EMAIL_RX.matcher(email).matches()) {
            throw new IllegalArgumentException("email: invalid format");
        }
        if (baseSalary.compareTo(BigDecimal.ZERO) < 0 ||
                baseSalary.compareTo(new BigDecimal("15000000")) > 0) {
            throw new IllegalArgumentException("baseSalary: out of range [0, 15000000]");
        }

        return new User(
                id,
                name.trim(),
                surname.trim(),
                birthDate,
                address,
                phone,
                email.trim(),
                baseSalary,
                roleId,
                documentNumber
        );
    }

    private static void requireText(String field, String v) {
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException(field + ": required and non-empty");
        }
    }
}