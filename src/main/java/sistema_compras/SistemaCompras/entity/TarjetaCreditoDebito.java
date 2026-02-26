package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tarjeta_credito_debito")
public class TarjetaCreditoDebito extends MetodoPago implements Serializable  {
    @Serial
    private static final long serialVersionUID=1L;

    // ✅ NO tiene @Id propio, lo hereda de MetodoPago

    @Column(name="numero_tarjeta", nullable = false, updatable = false)
    @NotBlank(message = "El número de la tarjeta es obligatorio")
    private String numeroTarjeta;  // Guardar encriptado y solo últimos 4 dígitos visibles

    @Column(name="nombre_titular", nullable = false)
    @NotBlank(message = "El nombre del titular es obligatorio")
    private String nombreTitular;

    // ✅ CVV NO SE GUARDA EN BASE DE DATOS
    // El CVV debe ser validado en tiempo real pero NUNCA persistido
    // @Transient si necesitas el campo temporalmente en memoria

    @Column(name="fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarjeta", nullable = false, length = 10)
    private TipoTarjeta tipoTarjeta;

    //-------------Relaciones---------------------------------------------------------------

    // ✅ N:1 con Cliente (dueño)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cliente cliente;
}