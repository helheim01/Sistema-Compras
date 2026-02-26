package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="pedido")
public class Pedido implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_pedido")
    private Integer id;

    @Column(name="numero_pedido", nullable = false, unique = true, updatable = false)
    @NotBlank(message = "El número de pedido es obligatorio")
    private String numeroPedido;

    @Column(name="fecha_pedido", nullable = false, updatable = false)
    private LocalDateTime fechaPedido;

    @Column(name="fecha_envio")  // ✅ nullable = true (no siempre enviado inmediatamente)
    private LocalDateTime fechaEnvio;

    @Column(name="fecha_entrega")  // ✅ nullable = true
    private LocalDateTime fechaEntrega;

    @Column(name="direccion_envio", nullable = false)
    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccionEnvio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido", nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El subtotal es obligatorio")  // ✅ @NotNull
    @Positive(message = "El subtotal debe ser positivo")
    private BigDecimal subtotal;

    @Column(name = "impuesto", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El impuesto es obligatorio")  // ✅ @NotNull
    @Positive(message = "El impuesto debe ser positivo")
    private BigDecimal impuesto;

    @Column(name = "costo_envio", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El costo de envío es obligatorio")  // ✅ @NotNull
    @Positive(message = "El costo de envío debe ser positivo")
    private BigDecimal costoEnvio;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El total es obligatorio")  // ✅ @NotNull
    @Positive(message = "El total debe ser positivo")
    private BigDecimal total;

    @Column(name="notas_especiales", columnDefinition = "TEXT")  // ✅ nullable = true
    private String notasEspeciales;

    //-------------Relaciones---------------------------------------------------------------

    // ✅ N:1 con Cliente (dueño)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cliente cliente;

    // ✅ 1:N con LineaPedido (inverso)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<LineaPedido> lineas = new ArrayList<>();

    // ✅ N:1 con Cuenta (dueño)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cuenta cuenta;

    // ✅ 1:N con Pago (inverso)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pago> pagos = new ArrayList<>();
}