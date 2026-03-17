package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    // Buscar por cliente
    Optional<Cuenta> findByClienteId(Integer clienteId);

    // Buscar cuentas cerradas
    List<Cuenta> findByEstaCerrada(Boolean estaCerrada);

    // Buscar cuentas con saldo pendiente
    List<Cuenta> findBySaldoPendienteGreaterThan(BigDecimal saldo);

    // Contar pedidos de una cuenta
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.cuenta.id = :cuentaId")
    Long contarPedidosPorCuenta(Integer cuentaId);

    // Buscar cuentas activas (no cerradas)
    List<Cuenta> findByEstaCerradaFalse();
}