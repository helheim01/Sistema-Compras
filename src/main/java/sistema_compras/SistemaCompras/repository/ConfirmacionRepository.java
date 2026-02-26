package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.Confirmacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConfirmacionRepository extends JpaRepository<Confirmacion, Integer> {

    // Buscar por email destino
    List<Confirmacion> findByEmailDestino(String email);

    // Buscar enviados
    List<Confirmacion> findByEnviadoTrue();

    // Buscar pendientes
    List<Confirmacion> findByEnviadoFalse();

    // Buscar por rango de fechas
    List<Confirmacion> findByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);

    // Buscar últimas confirmaciones
    List<Confirmacion> findTop10ByOrderByFechaEnvioDesc();
}