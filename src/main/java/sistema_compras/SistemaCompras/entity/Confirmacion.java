package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Confirmacion")
public class Confirmacion implements Serializable {
    @Serial
    private static final long seriaVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_confirmacion")
    private Integer id;

    @Column(name="emailDestino_confirmacion", nullable = false, updatable = false)
    @NotBlank(message = "El codigo de transaccion es obligatorio")
    private String emailDestino;

    @Column(name="asunto_confirmacion", nullable = false, updatable = false)
    @NotBlank(message = "El asunto de confirmacion es obligatorio")
    private String asunto;

    @Column(name="mensaje_confirmacion", nullable = false, updatable = false)
    @NotBlank(message = "El mensaje de confirmacion es obligatorio")
    private String mensaje;

    @Column(name="fechaEnvio_confirmacion", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Column(name="enviado")
    private Boolean enviado=false;

    @JsonCreator
    public Confirmacion (@JsonProperty("id") Integer id) {
        this.id = id;
    }
}
