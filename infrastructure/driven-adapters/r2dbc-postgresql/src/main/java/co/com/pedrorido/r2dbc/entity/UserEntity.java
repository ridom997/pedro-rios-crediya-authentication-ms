package co.com.pedrorido.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table("usuario")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @Column("id_usuario")
    private UUID id;

    @Column("nombre")
    private String name;

    @Column("apellido")
    private String surname;

    @Column("fecha_nacimiento")
    private LocalDate birthDate;

    @Column("email")
    private String email;

    @Column("telefono")
    private String phone;

    @Column("id_rol")
    private UUID roleId;

    @Column("salario_base")
    private BigDecimal baseSalary;

    @Column("direccion")
    private String address;

    @Column("documento_identidad")
    private String documentNumber;


}
