package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pago")
public class Pago implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_pago")  // ✅ Corregido
    private Integer id;

    @Column(name="codigo_transaccion", nullable = false, unique = true, updatable = false)
    @NotBlank(message = "El código de transacción es obligatorio")
    private String codigoTransaccion;

    @Column(name="fecha_pago", nullable = false, updatable = false)
    private LocalDateTime fechaPago;

    @Column(name="hora_pago", nullable = false, updatable = false)
    private LocalTime horaPago;

    @Column(name = "importe", nullable = false)
    @NotNull(message = "El importe es obligatorio")  // ✅ @NotNull
    @Positive(message = "El importe debe ser positivo")
    private BigDecimal importe;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 30)
    private TipoPago tipoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 20)
    private EstadoPago estado;  // ← Faltaba este campo

    @Column(name="detalle_pago", columnDefinition = "TEXT")
    private String detalle;

    //-------------Relaciones---------------------------------------------------------------

    // ✅ N:1 con Pedido
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)  // ✅ Corregido
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Pedido pedido;

    // ✅ N:1 con Cuenta
    @ManyToOne
    @JoinColumn(name = "cuenta_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cuenta cuenta;

    // ✅ 1:1 con MetodoPago (sin @MapsId)
    @OneToOne
    @JoinColumn(name = "metodo_pago_id")
    @JsonBackReference
    private MetodoPago metodoPago;
}