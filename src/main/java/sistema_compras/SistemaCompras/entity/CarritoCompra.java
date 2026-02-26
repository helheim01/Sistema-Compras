package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="CarritoCompra")
public class CarritoCompra implements Serializable {
    @Serial
    private static final long seriaVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_carritoCompra")
    private Integer id;

    @Column(name="fechaCreacion_carrito", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name="fechaActualizacion_carrito", nullable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    @Column(name = "subtotal", nullable = false)
    @Positive(message = "El subtotal pendiente debe ser positiva")
    private BigDecimal subtotal;

    //-------------Relaciones---------------------------------------------------------------

    // Lado dueño de la relación 1:1
    @OneToOne
    @MapsId // Indica que la PK viene de Cliente
    @JoinColumn(name = "cliente_id")
    @JsonBackReference
    private Cliente cliente;

    // Relación 1:N con Linea Carrito
    @OneToMany(mappedBy = "carritoCompra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LineaCarrito> lineas = new ArrayList<>();
}
