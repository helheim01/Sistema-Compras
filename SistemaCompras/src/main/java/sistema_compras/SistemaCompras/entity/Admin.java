package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Admin")
public class Admin {
    @Serial
    private static final long seriaVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_admin")
    private Integer id;

    @Column(name="nombre_admin")
    @NotBlank(message = "El nombre del admin es obligatorio")
    private String nombre;

    @Column(name="email_admin")
    @NotBlank(message = "El email del admin es obligatorio")
    private String email;

    @Column(name="contrasena_admin")
    @NotBlank(message = "La contrasena del admin es obligatorio")
    private String contrasena;

    @Column(name="rol_admin")
    @NotBlank(message = "El rol del admin es obligatorio")
    private String rol;

    @JsonCreator
    public Admin (@JsonProperty("id") Integer id) {
        this.id = id;
    }
}
