package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {

    // Buscar por titular
    List<MetodoPago> findByTitularContainingIgnoreCase(String titular);

    // Buscar activos
    List<MetodoPago> findByActivoTrue();

    // Buscar inactivos
    List<MetodoPago> findByActivoFalse();
}