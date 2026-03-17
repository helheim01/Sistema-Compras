package sistema_compras.SistemaCompras.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto")
public class Producto implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_producto")
    private Integer id;

    @Column(name="codigo_producto", nullable = false, unique = true, updatable = false)
    @NotBlank(message = "El código del producto es obligatorio")
    private String codigo;

    @Column(name="nombre_producto", nullable = false)
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @Column(name="descripcion_producto", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_producto", nullable = false, precision = 10, scale = 2)  // ✅ Corregido typo
    @NotNull(message = "El precio del producto es obligatorio")  // ✅ @NotNull
    @Positive(message = "El precio del producto debe ser positivo")
    private BigDecimal precio;

    @Column(name = "stock_producto", nullable = false)
    @NotNull(message = "El stock es obligatorio")  // ✅ @NotNull
    @Positive(message = "El stock debe ser positivo")
    private Integer stock;

    @Column(name = "proveedor_producto")
    private String proveedor;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name="activo")
    private Boolean activo = true;

    @Column(name="fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    //-------------Relaciones---------------------------------------------------------------

    // ✅ 1:N con LineaCarrito (inverso)
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LineaCarrito> lineasCarrito = new ArrayList<>();

    // ✅ 1:N con LineaPedido (inverso)
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LineaPedido> lineasPedido = new ArrayList<>();

    // ✅ N:1 con Categoria (dueño)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Categoria categoria;

    // ✅ 1:N con Imagenes (inverso)
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ImagenProducto> imagenes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    @JsonCreator
    public Producto (@JsonProperty("id") Integer id) {
        this.id = id;
    }
}