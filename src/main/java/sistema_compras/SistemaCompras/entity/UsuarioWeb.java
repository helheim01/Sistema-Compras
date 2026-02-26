package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="usuario_web")
public class UsuarioWeb implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario_web")
    private Integer id;

    @Column(name="email", nullable = false, unique = true)  // ✅ unique = true
    @NotBlank(message = "El email del usuario es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @Column(name="contrasena", nullable = false)  // ✅ Corregido nombre
    @NotBlank(message = "La contraseña del usuario es obligatoria")
    private String contrasena;  // Debe estar hasheada con BCrypt

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoUsuario estado;

    @Column(name="fecha_registro", nullable = false, updatable = false)  // ✅ Corregido nombre
    private LocalDateTime fechaRegistro;

    @Column(name="ultimo_acceso")  // ✅ Corregido nombre
    private LocalDateTime ultimoAcceso;

    //-------------Relaciones---------------------------------------------------------------

    // ✅ 1:1 inverso con Cliente
    @OneToOne(mappedBy = "usuarioWeb", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Cliente cliente;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoUsuario.NUEVO;
        }
    }
}