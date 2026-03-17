package sistema_compras.SistemaCompras.repository;

import sistema_compras.SistemaCompras.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    // Buscar por nombre
    Optional<Categoria> findByNombre(String nombre);

    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);

    // Buscar activas
    List<Categoria> findByActivaTrue();

    // Buscar inactivas
    List<Categoria> findByActivaFalse();

    // Buscar por nombre (búsqueda parcial)
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    // Categorías con productos
    @Query("SELECT c FROM Categoria c WHERE SIZE(c.productos) > 0")
    List<Categoria> findCategoriasConProductos();

    // Categorías sin productos
    @Query("SELECT c FROM Categoria c WHERE SIZE(c.productos) = 0")
    List<Categoria> findCategoriasSinProductos();

    // Contar productos por categoría
    @Query("SELECT c.nombre, COUNT(p) FROM Categoria c LEFT JOIN c.productos p GROUP BY c.nombre")
    List<Object[]> contarProductosPorCategoria();
}