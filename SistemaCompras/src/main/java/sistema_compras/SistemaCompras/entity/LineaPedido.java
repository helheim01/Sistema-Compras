package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(name="LineaPedido")
public class LineaPedido implements Serializable {

    @Serial
    private static final long seriaVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_lineaPedido")
    private Integer id;

    @Column(name = "cantidad", nullable = false)
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidad;

    @Column(name = "precioUnitario", nullable = false)
    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser positivo")
    private BigDecimal precioUnitario;

    @Column(name = "descuento", nullable = false)
    @NotNull(message = "El descuento es obligatorio")
    @PositiveOrZero(message = "El descuento no puede ser negativo")
    private BigDecimal descuento;

    @Column(name = "subtotal", nullable = false)
    @NotNull(message = "El subtotal es obligatorio")
    @Positive(message = "El subtotal debe ser positivo")
    private BigDecimal subtotal;

    // Relaciones

    // N:1 con Pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ubicaciones", "impuestoProvincial"})
    private Pedido pedido;

    // N:1 con Producto
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ubicaciones", "impuestoProvincial"})
    private Producto producto;

    @JsonCreator
    public LineaPedido (@JsonProperty("id") Integer id) {
        this.id = id;
    }


}
