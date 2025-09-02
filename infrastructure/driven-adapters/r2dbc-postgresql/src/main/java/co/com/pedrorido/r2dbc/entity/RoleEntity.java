package co.com.pedrorido.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("rol")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleEntity {
    @Id
    @Column("id_rol")
    private Long id;
    @Column("nombre")
    private String name;
    @Column("descripcion")
    private String description;
    // Relación OneToMany con UserEntity
    // Indicamos que este rol está relacionado con muchos usuarios
    @Transient
    private List<UserEntity> users; // Spring Data R2DBC utiliza @Transient para relaciones no mapeadas directamente en la tabla

}
