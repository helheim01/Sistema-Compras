package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.CarritoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoCompraRepository extends JpaRepository<CarritoCompra, Integer> {

    // Buscar por cliente
    Optional<CarritoCompra> findByClienteId(Integer clienteId);

    // Buscar carritos vacíos (sin líneas)
    @Query("SELECT c FROM CarritoCompra c WHERE c.lineas IS EMPTY")
    List<CarritoCompra> findCarritosVacios();

    // Buscar carritos abandonados (sin actualizar por más de X días)
    @Query("SELECT c FROM CarritoCompra c WHERE c.fechaActualizacion < :fecha AND SIZE(c.lineas) > 0")
    List<CarritoCompra> findCarritosAbandonados(@Param("fecha") LocalDateTime fecha);

    // Buscar carritos con productos
    @Query("SELECT c FROM CarritoCompra c WHERE SIZE(c.lineas) > 0")
    List<CarritoCompra> findCarritosConProductos();
}