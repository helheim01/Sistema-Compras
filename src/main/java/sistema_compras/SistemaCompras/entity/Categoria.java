package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Categoria")
public class Categoria implements Serializable {
    @Serial
    private static final long seriaVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_confirmacion")
    private Integer id;

    @Column(name="nombre_categoria", nullable = false, updatable = false)
    @NotBlank(message = "El nombre de la categoria es obligatorio")
    private String nombre;

    @Column(name="descripcion_confirmacion", nullable = false, updatable = false)
    @NotBlank(message = "La descripcion de categoria es obligatorio")
    private String descripcion;

    @Column(name="activa")
    private Boolean activa=true;


    // Relación 1:N con Producto
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Producto> productos = new ArrayList<>();
}
