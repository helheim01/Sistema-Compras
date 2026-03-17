package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Buscar por código
    Optional<Producto> findByCodigo(String codigo);

    // Verificar si existe por código
    boolean existsByCodigo(String codigo);

    // Buscar por categoría
    List<Producto> findByCategoriaId(Integer categoriaId);

    // Buscar productos activos
    List<Producto> findByActivoTrue();

    // Buscar productos inactivos
    List<Producto> findByActivoFalse();

    // Buscar por nombre (búsqueda parcial)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por rango de precios
    List<Producto> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    // Buscar por proveedor
    List<Producto> findByProveedor(String proveedor);

    // Buscar productos con stock bajo
    @Query("SELECT p FROM Producto p WHERE p.stock < :stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo(@Param("stockMinimo") Integer stockMinimo);

    // Buscar productos sin stock
    List<Producto> findByStockEquals(Integer stock);

    // Buscar productos más caros
    List<Producto> findTop10ByActivoTrueOrderByPrecioDesc();

    // Buscar productos más baratos
    List<Producto> findTop10ByActivoTrueOrderByPrecioAsc();

    // Buscar por categoría y activo
    List<Producto> findByCategoriaIdAndActivoTrue(Integer categoriaId);

    // Búsqueda avanzada
    @Query("SELECT p FROM Producto p WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
            "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
            "(:precioMax IS NULL OR p.precio <= :precioMax) AND " +
            "p.activo = true")
    List<Producto> busquedaAvanzada(
            @Param("nombre") String nombre,
            @Param("categoriaId") Integer categoriaId,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax
    );
}