package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.LineaCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineaCarritoRepository extends JpaRepository<LineaCarrito, Integer> {

    // Buscar por carrito
    List<LineaCarrito> findByCarritoCompraId(Integer carritoId);

    // Buscar por producto
    List<LineaCarrito> findByProductoId(Integer productoId);

    // Buscar línea específica (carrito + producto)
    Optional<LineaCarrito> findByCarritoCompraIdAndProductoId(Integer carritoId, Integer productoId);

    // Eliminar líneas de un carrito
    void deleteByCarritoCompraId(Integer carritoId);

    // Productos más agregados al carrito
    @Query("SELECT l.producto.id, COUNT(l) as cantidad FROM LineaCarrito l GROUP BY l.producto.id ORDER BY cantidad DESC")
    List<Object[]> findProductosMasAgregados();
}