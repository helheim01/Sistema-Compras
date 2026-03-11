package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="linea_carrito")
public class LineaCarrito implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_linea_carrito")  // ✅ Corregido
    private Integer id;

    @Column(name = "cantidad", nullable = false)
    @NotNull(message = "La cantidad es obligatoria")  // ✅ @NotNull, no @NotBlank
    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    @NotNull(message = "El precio unitario es obligatorio")  // ✅ @NotNull
    @Positive(message = "El precio unitario debe ser positivo")
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false)
    @NotNull(message = "El subtotal es obligatorio")  // ✅ @NotNull
    @Positive(message = "El subtotal debe ser positivo")
    private BigDecimal subtotal;

    // ✅ N:1 con CarritoCompra
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "lineas", "cliente"})
    private CarritoCompra carritoCompra;

    // ✅ N:1 con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Producto producto;

    @JsonCreator
    public LineaCarrito (@JsonProperty("id") Integer id) {
        this.id = id;
    }
}