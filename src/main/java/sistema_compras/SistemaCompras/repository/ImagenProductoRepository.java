package sistema_compras.SistemaCompras.repository;

import org.springframework.data.repository.query.Param;
import sistema_compras.SistemaCompras.entity.ImagenProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Integer> {

    // Buscar por producto
    List<ImagenProducto> findByProductoId(Integer productoId);

    // Buscar por producto ordenadas
    List<ImagenProducto> findByProductoIdOrderByOrdenAsc(Integer productoId);

    // Eliminar por producto
    void deleteByProductoId(Integer productoId);

    // Contar imágenes de un producto
    @Query("SELECT COUNT(i) FROM ImagenProducto i WHERE i.producto.id = :productoId")
    Long contarImagenesPorProducto(@Param("productoId") Integer productoId);

    // Buscar imagen principal (orden = 0)
    @Query("SELECT i FROM ImagenProducto i WHERE i.producto.id = :productoId AND i.orden = 0")
    ImagenProducto findImagenPrincipal(@Param("productoId") Integer productoId);

    @Query("SELECT COALESCE(MAX(i.orden), 0) FROM ImagenProducto i WHERE i.producto.id = :productoId")
    Integer findMaxOrdenByProductoId(@Param("productoId") Integer productoId);
}