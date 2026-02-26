package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    // Buscar por email
    Optional<Admin> findByEmail(String email);

    // Verificar si existe por email
    boolean existsByEmail(String email);

    // Buscar por rol
    List<Admin> findByRol(String rol);

    // Buscar por nombre
    List<Admin> findByNombreContainingIgnoreCase(String nombre);
}