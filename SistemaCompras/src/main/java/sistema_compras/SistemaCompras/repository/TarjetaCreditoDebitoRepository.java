package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.TarjetaCreditoDebito;
import sistema_compras.SistemaCompras.entity.TipoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarjetaCreditoDebitoRepository extends JpaRepository<TarjetaCreditoDebito, Integer> {

    // Buscar por cliente
    List<TarjetaCreditoDebito> findByClienteId(Integer clienteId);

    // Buscar por cliente y activas
    List<TarjetaCreditoDebito> findByClienteIdAndActivoTrue(Integer clienteId);

    // Buscar por tipo
    List<TarjetaCreditoDebito> findByTipoTarjeta(TipoTarjeta tipo);

    // Buscar por últimos 4 dígitos (parcial)
    @Query("SELECT t FROM TarjetaCreditoDebito t WHERE t.numeroTarjeta LIKE %:ultimosDigitos%")
    List<TarjetaCreditoDebito> findByUltimosDigitos(@Param("ultimosDigitos") String ultimosDigitos);

    // Buscar tarjetas próximas a vencer
    @Query("SELECT t FROM TarjetaCreditoDebito t WHERE t.fechaVencimiento BETWEEN :hoy AND :fechaLimite AND t.activo = true")
    List<TarjetaCreditoDebito> findTarjetasProximasAVencer(
            @Param("hoy") LocalDate hoy,
            @Param("fechaLimite") LocalDate fechaLimite
    );

    // Buscar tarjetas vencidas
    List<TarjetaCreditoDebito> findByFechaVencimientoBefore(LocalDate fecha);

    // Buscar tarjeta específica de un cliente por número
    Optional<TarjetaCreditoDebito> findByClienteIdAndNumeroTarjeta(Integer clienteId, String numeroTarjeta);
}