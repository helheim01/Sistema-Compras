package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.LineaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineaPedidoRepository extends JpaRepository<LineaPedido, Integer> {

    // Buscar por pedido
    List<LineaPedido> findByPedidoId(Integer pedidoId);

    // Buscar por producto
    List<LineaPedido> findByProductoId(Integer productoId);

    // Productos más vendidos
    @Query("SELECT l.producto.id, l.producto.nombre, SUM(l.cantidad) as totalVendido " +
            "FROM LineaPedido l " +
            "GROUP BY l.producto.id, l.producto.nombre " +
            "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos();

    // Total de unidades vendidas de un producto
    @Query("SELECT SUM(l.cantidad) FROM LineaPedido l WHERE l.producto.id = :productoId")
    Long contarUnidadesVendidas(@Param("productoId") Integer productoId);

    // Productos comprados juntos (análisis de cesta)
    @Query("SELECT l1.producto.id, l2.producto.id, COUNT(*) as veces " +
            "FROM LineaPedido l1 " +
            "JOIN LineaPedido l2 ON l1.pedido.id = l2.pedido.id " +
            "WHERE l1.producto.id < l2.producto.id " +
            "GROUP BY l1.producto.id, l2.producto.id " +
            "ORDER BY veces DESC")
    List<Object[]> findProductosCompradosJuntos();
}