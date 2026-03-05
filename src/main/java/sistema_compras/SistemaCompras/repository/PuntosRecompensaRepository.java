package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.PuntosRecompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuntosRecompensaRepository extends JpaRepository<PuntosRecompensa, Integer> {

    // Buscar por cliente
    Optional<PuntosRecompensa> findByClienteId(Integer clienteId);

    // Buscar por código de cupón
    Optional<PuntosRecompensa> findByCodigoCupon(String codigoCupon);

    // Buscar con puntos mayores a cierta cantidad
    List<PuntosRecompensa> findByPuntosDisponiblesGreaterThan(Integer puntos);

    // Top 10 con más puntos
    List<PuntosRecompensa> findTop10ByOrderByPuntosDisponiblesDesc();

    // Total de puntos en el sistema
    @Query("SELECT SUM(pr.puntosDisponibles) FROM PuntosRecompensa pr")
    Long calcularTotalPuntosEnSistema();

    // Clientes sin puntos (puntosDisponibles = 0)
    List<PuntosRecompensa> findByPuntosDisponiblesEquals(Integer puntos);
}