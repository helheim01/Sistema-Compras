package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.PuntosRecompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuntosRecompensaRepository extends JpaRepository<PuntosRecompensa, Integer> {

    // Buscar por cliente
    Optional<PuntosRecompensa> findByClienteId(Integer clienteId);

    // Buscar por código de cupón
    Optional<PuntosRecompensa> findByCodigoCupon(String codigoCupon);

    // Buscar clientes con puntos mayores a cierta cantidad
    List<PuntosRecompensa> findByPuntosDisponiblesGreaterThan(Integer puntos);

    // Top clientes con más puntos
    List<PuntosRecompensa> findTop10ByOrderByPuntosDisponiblesDesc();

    // Total de puntos en el sistema
    @Query("SELECT SUM(pr.puntosDisponibles) FROM PuntosRecompensa pr")
    Long calcularTotalPuntosEnSistema();

    // Clientes sin puntos
    List<PuntosRecompensa> findByPuntosDisponiblesEquals(Integer puntos);

    // Puntos totales ganados por un cliente
    @Query("SELECT pr.puntosGanadosTotal FROM PuntosRecompensa pr WHERE pr.cliente.id = :clienteId")
    Integer obtenerPuntosGanadosTotales(@Param("clienteId") Integer clienteId);
}