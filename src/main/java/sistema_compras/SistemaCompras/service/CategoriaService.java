package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.Categoria;
import sistema_compras.SistemaCompras.entity.Producto;
import sistema_compras.SistemaCompras.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService implements ICrud<Categoria> {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public Categoria agregar(Categoria categoria) {
        if (categoria.getNombre() == null) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }

        categoria.setActiva(true);

        Categoria guardada = categoriaRepository.save(categoria);
        logger.info("Categoría agregada con éxito: {}", guardada.getNombre());
        return guardada;
    }

    // ------------------ LISTAR PRODUCTOS ------------------
    public List<Producto> listarProductos(Integer categoriaId) {
        Categoria categoria = buscar(categoriaId);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoría no encontrada.");
        }

        logger.info("Listando productos de categoría: {}", categoria.getNombre());
        return categoria.getProductos();
    }

    // ------------------ ACTIVAR/DESACTIVAR ------------------
    @Transactional
    public void activar(Integer id) {
        Categoria categoria = buscar(id);
        if (categoria != null) {
            categoria.setActiva(true);
            categoriaRepository.save(categoria);
            logger.info("Categoría activada: {}", categoria.getNombre());
        }
    }

    @Transactional
    public void desactivar(Integer id) {
        Categoria categoria = buscar(id);
        if (categoria != null) {
            categoria.setActiva(false);
            categoriaRepository.save(categoria);
            logger.info("Categoría desactivada: {}", categoria.getNombre());
        }
    }

    // ------------------ BUSCAR POR NOMBRE ------------------
    public Categoria buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre).orElse(null);
    }

    // ------------------ LISTAR ACTIVAS ------------------
    public List<Categoria> listarActivas() {
        return categoriaRepository.findByActivaTrue();
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public Categoria modificar(Categoria categoria) {
        if (categoria.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar la categoría.");
        }

        Categoria existente = categoriaRepository.findById(categoria.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe una categoría con ID " + categoria.getId()));

        existente.setNombre(categoria.getNombre());
        existente.setDescripcion(categoria.getDescripcion());
        existente.setActiva(categoria.getActiva());

        Categoria actualizada = categoriaRepository.save(existente);
        logger.info("Categoría modificada con éxito: {}", actualizada.getNombre());
        return actualizada;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public Categoria buscar(Integer id) {
        Categoria categoria = categoriaRepository.findById(id).orElse(null);
        if (categoria == null) {
            logger.warn("No se encontró categoría con ID: {}", id);
        }
        return categoria;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        Categoria categoria = buscar(id);
        if (categoria == null) {
            throw new IllegalArgumentException("No existe una categoría con ID " + id);
        }

        if (!categoria.getProductos().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar una categoría con productos asociados.");
        }

        categoriaRepository.deleteById(id);
        logger.info("Categoría eliminada con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<Categoria> listar() {
        List<Categoria> categorias = categoriaRepository.findAll();
        logger.info("Se listaron {} categorías.", categorias.size());
        return categorias;
    }

    // ------------------ VERIFICAR SI EXISTE POR NOMBRE ------------------
    public boolean existePorNombre(String nombre) {
        logger.info("Verificando si existe categoría con nombre: {}", nombre);
        return categoriaRepository.existsByNombre(nombre);
    }

    // ------------------ LISTAR INACTIVAS ------------------
    public List<Categoria> listarInactivas() {
        logger.info("Listando categorías inactivas");
        return categoriaRepository.findByActivaFalse();
    }

    // ------------------ BUSCAR POR NOMBRE (BÚSQUEDA PARCIAL) ------------------
    public List<Categoria> buscarPorNombreParcial(String nombre) {
        logger.info("Buscando categorías por nombre parcial: {}", nombre);
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // ------------------ CATEGORÍAS CON PRODUCTOS ------------------
    public List<Categoria> listarCategoriasConProductos() {
        logger.info("Listando categorías con productos");
        return categoriaRepository.findCategoriasConProductos();
    }

    // ------------------ CATEGORÍAS SIN PRODUCTOS ------------------
    public List<Categoria> listarCategoriasSinProductos() {
        logger.info("Listando categorías sin productos");
        return categoriaRepository.findCategoriasSinProductos();
    }

    // ------------------ CONTAR PRODUCTOS POR CATEGORÍA ------------------
    public List<Object[]> contarProductosPorCategoria() {
        logger.info("Contando productos por categoría");
        return categoriaRepository.contarProductosPorCategoria();
    }
}