package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.EstadoPago;
import sistema_compras.SistemaCompras.entity.Pago;
import sistema_compras.SistemaCompras.entity.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    // Buscar por código de transacción
    Optional<Pago> findByCodigoTransaccion(String codigoTransaccion);

    // Buscar por pedido
    List<Pago> findByPedidoId(Integer pedidoId);

    // Buscar por cuenta
    List<Pago> findByCuentaId(Integer cuentaId);

    // Buscar por estado
    List<Pago> findByEstado(EstadoPago estado);

    // Buscar por tipo de pago
    List<Pago> findByTipoPago(TipoPago tipoPago);

    // Buscar por rango de fechas
    List<Pago> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);

    // Buscar pagos pendientes
    List<Pago> findByEstadoIn(List<EstadoPago> estados);

    Optional<Pago> findByPedidoIdAndEstado(Integer pedidoId, EstadoPago estado);

    // Calcular total pagado por un pedido
    @Query("SELECT SUM(p.importe) FROM Pago p WHERE p.pedido.id = :pedidoId AND p.estado = 'COMPLETADO'")
    BigDecimal calcularTotalPagadoPorPedido(@Param("pedidoId") Integer pedidoId);

    // Calcular ingresos por período
    @Query("SELECT SUM(p.importe) FROM Pago p WHERE p.fechaPago BETWEEN :inicio AND :fin AND p.estado = 'COMPLETADO'")
    BigDecimal calcularIngresosPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Pagos por tipo
    @Query("SELECT p.tipoPago, COUNT(p), SUM(p.importe) FROM Pago p WHERE p.estado = 'COMPLETADO' GROUP BY p.tipoPago")
    List<Object[]> estadisticasPorTipoPago();

    // Buscar pagos de un cliente
    @Query("SELECT p FROM Pago p WHERE p.pedido.cliente.id = :clienteId ORDER BY p.fechaPago DESC")
    List<Pago> findPagosPorCliente(@Param("clienteId") Integer clienteId);
}