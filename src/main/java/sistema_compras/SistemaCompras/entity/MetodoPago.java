package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "metodo_pago")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MetodoPago implements Serializable  {
    @Serial
    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_metodo_pago")
    private Integer id;

    @Column(name = "titular", nullable = false)
    @NotBlank(message = "El titular es obligatorio")
    private String titular;

    @Column(name="activo")
    private Boolean activo = true;

}