package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
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
@Table(name = "imagen_producto")  // ✅ snake_case
public class ImagenProducto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Integer id;

    @Column(name = "url_imagen", nullable = false, length = 500)  // ✅ Más espacio para URLs largas
    @NotBlank(message = "La URL de la imagen es obligatoria")
    @URL(message = "Debe ser una URL válida")  // ✅ Validación de formato URL
    private String urlImagen;

    @Column(name = "orden")
    @Min(value = 0, message = "El orden debe ser mayor o igual a 0")  // ✅ Validación
    private Integer orden = 0;  // ✅ Valor por defecto

    //-------------Relaciones---------------------------------------------------------------

    // ✅ N:1 con Producto (dueño)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)  // ✅ Cambié a "producto_id" por consistencia
    @JsonBackReference
    private Producto producto;

    @JsonCreator
    public ImagenProducto (@JsonProperty("id") Integer id) {
        this.id = id;
    }
}