package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cliente")
public class Cliente implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_cliente")
    private Integer id;

    @Column(name="nombre_cliente", nullable = false)
    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombre;

    @Column(name="direccion_cliente", nullable = false)
    @NotBlank(message = "La dirección del cliente es obligatoria")
    private String direccion;

    @Column(name="email_cliente", nullable = false, unique = true)
    @NotBlank(message = "El email del cliente es obligatorio")
    private String email;

    @Column(name="telefono_cliente", nullable = false)
    @NotBlank(message = "El teléfono del cliente es obligatorio")
    private String telefono;

//    @Column(name="puntos_recompensa")
//    private Integer puntosRecompensa = 0;

    //-------------Relaciones---------------------------------------------------------------

    // ✅ Lado dueño 1:1 con UsuarioWeb
    @OneToOne
    @JoinColumn(name = "usuario_web_id", unique = true)
    private UsuarioWeb usuarioWeb;

    // ✅ Relación 1:1 inversa con Cuenta
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Cuenta cuenta;  // ← NO Cliente

    // ✅ Relación 1:1 inversa con CarritoCompra
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private CarritoCompra carritoCompra;

    // ✅ Relación 1:N con Pedido
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pedido> pedidos = new ArrayList<>();

    // ✅ Relación 1:N con Tarjeta
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TarjetaCreditoDebito> tarjetas = new ArrayList<>();

    // ✅ Relación 1:1 con PuntosRecompensa (NO 1:N)
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PuntosRecompensa puntosRecompensa;
}