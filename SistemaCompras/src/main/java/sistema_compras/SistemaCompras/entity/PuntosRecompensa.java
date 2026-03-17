package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
@Table(name = "puntos_recompensa")
public class PuntosRecompensa extends MetodoPago implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    // ✅ NO tiene @Id propio, lo hereda de MetodoPago

    @Column(name="codigo_cupon")
    private String codigoCupon;  // nullable (opcional)

    @Column(name = "puntos_disponibles", nullable = false)  // ✅ Corregido nombre
    @NotNull(message = "Los puntos disponibles son obligatorios")
    // En PuntosRecompensa.java
    @PositiveOrZero(message = "Los puntos disponibles no pueden ser negativos")
    private Integer puntosDisponibles;

    @Column(name = "tasa_conversion", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "La tasa de conversión es obligatoria")  // ✅ @NotNull
    @Positive(message = "La tasa de conversión debe ser positiva")
    private BigDecimal tasaConversion;

    //-------------Relaciones---------------------------------------------------------------

    // ✅ 1:1 con Cliente (dueño)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", unique = true, nullable = false)
    @JsonBackReference
    private Cliente cliente;
}