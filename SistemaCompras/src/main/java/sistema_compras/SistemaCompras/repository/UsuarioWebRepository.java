package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.EstadoUsuario;
import sistema_compras.SistemaCompras.entity.UsuarioWeb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioWebRepository extends JpaRepository<UsuarioWeb, Integer> {

    // Buscar por email
    Optional<UsuarioWeb> findByEmail(String email);

    // Verificar si existe por email
    boolean existsByEmail(String email);

    // Buscar por estado
    List<UsuarioWeb> findByEstado(EstadoUsuario estado);

    // Buscar activos
    List<UsuarioWeb> findByEstadoNot(EstadoUsuario estado);
}