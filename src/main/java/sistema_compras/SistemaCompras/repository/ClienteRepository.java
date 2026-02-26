package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // Buscar por email
    Optional<Cliente> findByEmail(String email);

    // Verificar si existe por email
    boolean existsByEmail(String email);

    // Buscar por nombre (búsqueda parcial)
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por teléfono
    Optional<Cliente> findByTelefono(String telefono);

    // Buscar clientes con puntos mayores a cierta cantidad
    @Query("SELECT c FROM Cliente c WHERE c.puntosRecompensa.puntosDisponibles > :puntos")
    List<Cliente> findClientesConPuntosMayoresA(@Param("puntos") Integer puntos);

    // Buscar por ID de usuario web
    Optional<Cliente> findByUsuarioWebId(Integer usuarioWebId);
}