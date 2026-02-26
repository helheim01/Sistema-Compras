package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Table(name="cuenta")
public class Cuenta implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_cuenta")  // ✅ Corregido
    private Integer id;

    @Column(name="direccion_facturacion", nullable = false)
    @NotBlank(message = "La dirección de facturación es obligatoria")
    private String direccionFacturacion;

    @Column(name="esta_cerrada")
    private Boolean estaCerrada = false;

    @Column(name="fecha_abierta", nullable = false, updatable = false)
    private LocalDateTime fechaAbierta;

    @Column(name="fecha_cerrada")  // ✅ nullable = true (por defecto)
    private LocalDateTime fechaCerrada;

    @Column(name = "saldo_pendiente", nullable = false)
    @Positive(message = "El saldo pendiente debe ser positivo")
    private BigDecimal saldoPendiente = BigDecimal.ZERO;

    //-------------Relaciones---------------------------------------------------------------

    // ✅ Lado dueño 1:1 con Cliente
    @OneToOne
    @JoinColumn(name = "cliente_id", unique = true)
    @JsonBackReference
    private Cliente cliente;

    // Relación 1:N con Pedido
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pedido> pedidos = new ArrayList<>();

    // Relación 1:N con Pago
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pago> pagos = new ArrayList<>();
}