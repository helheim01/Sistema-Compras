package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.EstadoPedido;
import sistema_compras.SistemaCompras.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Buscar por número de pedido
    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    // Buscar por cliente
    List<Pedido> findByClienteId(Integer clienteId);

    // Buscar por cuenta
    List<Pedido> findByCuentaId(Integer cuentaId);

    // Buscar por estado
    List<Pedido> findByEstado(EstadoPedido estado);

    // Buscar por cliente y estado
    List<Pedido> findByClienteIdAndEstado(Integer clienteId, EstadoPedido estado);

    // Buscar pedidos por rango de fechas
    List<Pedido> findByFechaPedidoBetween(LocalDateTime inicio, LocalDateTime fin);

    // Buscar pedidos pendientes de envío
    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('NUEVO', 'PENDIENTE', 'PROCESANDO') ORDER BY p.fechaPedido ASC")
    List<Pedido> findPedidosPendientesEnvio();

    // Calcular total de ventas por período
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.fechaPedido BETWEEN :inicio AND :fin AND p.estado != 'CANCELADO'")
    BigDecimal calcularTotalVentas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Buscar pedidos recientes de un cliente
    List<Pedido> findTop10ByClienteIdOrderByFechaPedidoDesc(Integer clienteId);

    // Buscar por total mayor a cierta cantidad
    List<Pedido> findByTotalGreaterThanEqual(BigDecimal total);

    // Contar pedidos por estado
    @Query("SELECT p.estado, COUNT(p) FROM Pedido p GROUP BY p.estado")
    List<Object[]> contarPedidosPorEstado();
}