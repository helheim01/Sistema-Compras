package sistema_compras.SistemaCompras.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.Categoria;
import sistema_compras.SistemaCompras.entity.Producto;
import sistema_compras.SistemaCompras.service.CategoriaService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/categoria")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaController.class);

    @Autowired
    private CategoriaService categoriaService;

    // ==================== CRUD BÁSICO ====================

    /**
     * GET /api/categoria/listar
     * Listar todas las categorías
     */
    @GetMapping("/listar")
    public ResponseEntity<List<Categoria>> listar() {
        logger.info("Listando todas las categorías");
        List<Categoria> listarCategoria = categoriaService.listar();
        return ResponseEntity.ok(listarCategoria);
    }

    /**
     * GET /api/categoria/buscar/{id}
     * Buscar categoría por ID
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        logger.info("Buscando categoría con ID: {}", id);
        Categoria categoria = categoriaService.buscar(id);

        if (categoria == null) {
            logger.warn("Categoría no encontrada con ID: {}", id);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Categoría no encontrada");
            error.put("id", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(categoria);
    }

    /**
     * POST /api/categoria/agregar
     * Agregar nueva categoría
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Categoria categoria) {
        try {
            logger.info("Agregando nueva categoría: {}", categoria.getNombre());

            // Validar si ya existe
            if (categoriaService.existePorNombre(categoria.getNombre())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ya existe una categoría con ese nombre");
                error.put("nombre", categoria.getNombre());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            Categoria categoriaAgregada = categoriaService.agregar(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaAgregada);
        } catch (IllegalArgumentException e) {
            logger.error("Error al agregar categoría: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/categoria/modificar
     * Modificar categoría existente
     */
    @PutMapping("/modificar")
    public ResponseEntity<?> modificar(@Valid @RequestBody Categoria categoria) {
        try {
            logger.info("Modificando categoría con ID: {}", categoria.getId());
            Categoria categoriaModificada = categoriaService.modificar(categoria);
            return ResponseEntity.ok(categoriaModificada);
        } catch (IllegalArgumentException e) {
            logger.error("Error al modificar categoría: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/categoria/eliminar/{id}
     * Eliminar categoría
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            logger.info("Eliminando categoría con ID: {}", id);
            categoriaService.eliminar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Categoría eliminada exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al eliminar categoría: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== GESTIÓN DE ESTADO ====================

    /**
     * PUT /api/categoria/activar/{id}
     * Activar categoría
     */
    @PutMapping("/activar/{id}")
    public ResponseEntity<?> activar(@PathVariable Integer id) {
        try {
            logger.info("Activando categoría con ID: {}", id);
            categoriaService.activar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Categoría activada exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al activar categoría: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/categoria/desactivar/{id}
     * Desactivar categoría
     */
    @PutMapping("/desactivar/{id}")
    public ResponseEntity<?> desactivar(@PathVariable Integer id) {
        try {
            logger.info("Desactivando categoría con ID: {}", id);
            categoriaService.desactivar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Categoría desactivada exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al desactivar categoría: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * GET /api/categoria/buscar-nombre/{nombre}
     * Buscar categoría por nombre exacto
     */
    @GetMapping("/buscar-nombre/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre) {
        logger.info("Buscando categoría por nombre: {}", nombre);
        Categoria categoria = categoriaService.buscarPorNombre(nombre);

        if (categoria == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No se encontró categoría con ese nombre");
            error.put("nombre", nombre);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(categoria);
    }

    /**
     * GET /api/categoria/buscar-nombre-parcial/{nombre}
     * Buscar categorías por nombre (búsqueda parcial)
     */
    @GetMapping("/buscar-nombre-parcial/{nombre}")
    public ResponseEntity<?> buscarPorNombreParcial(@PathVariable String nombre) {
        logger.info("Buscando categorías por nombre parcial: {}", nombre);
        List<Categoria> categorias = categoriaService.buscarPorNombreParcial(nombre);

        Map<String, Object> response = new HashMap<>();
        response.put("busqueda", nombre);
        response.put("cantidad", categorias.size());
        response.put("categorias", categorias);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/categoria/activas
     * Listar categorías activas
     */
    @GetMapping("/activas")
    public ResponseEntity<?> listarActivas() {
        logger.info("Listando categorías activas");
        List<Categoria> categorias = categoriaService.listarActivas();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", categorias.size());
        response.put("categorias", categorias);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/categoria/inactivas
     * Listar categorías inactivas
     */
    @GetMapping("/inactivas")
    public ResponseEntity<?> listarInactivas() {
        logger.info("Listando categorías inactivas");
        List<Categoria> categorias = categoriaService.listarInactivas();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", categorias.size());
        response.put("categorias", categorias);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/categoria/con-productos
     * Listar categorías que tienen productos
     */
    @GetMapping("/con-productos")
    public ResponseEntity<?> listarCategoriasConProductos() {
        logger.info("Listando categorías con productos");
        List<Categoria> categorias = categoriaService.listarCategoriasConProductos();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", categorias.size());
        response.put("categorias", categorias);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/categoria/sin-productos
     * Listar categorías sin productos
     */
    @GetMapping("/sin-productos")
    public ResponseEntity<?> listarCategoriasSinProductos() {
        logger.info("Listando categorías sin productos");
        List<Categoria> categorias = categoriaService.listarCategoriasSinProductos();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", categorias.size());
        response.put("categorias", categorias);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/categoria/{id}/productos
     * Listar productos de una categoría específica
     */
    @GetMapping("/{id}/productos")
    public ResponseEntity<?> listarProductos(@PathVariable Integer id) {
        try {
            logger.info("Listando productos de categoría ID: {}", id);
            List<Producto> productos = categoriaService.listarProductos(id);

            Categoria categoria = categoriaService.buscar(id);
            Map<String, Object> response = new HashMap<>();
            response.put("categoriaId", id);
            response.put("categoriaNombre", categoria != null ? categoria.getNombre() : "N/A");
            response.put("cantidad", productos.size());
            response.put("productos", productos);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al listar productos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * GET /api/categoria/verificar-nombre/{nombre}
     * Verificar si existe una categoría con ese nombre
     */
    @GetMapping("/verificar-nombre/{nombre}")
    public ResponseEntity<?> verificarNombre(@PathVariable String nombre) {
        logger.info("Verificando existencia de categoría: {}", nombre);
        boolean existe = categoriaService.existePorNombre(nombre);

        Map<String, Object> response = new HashMap<>();
        response.put("nombre", nombre);
        response.put("existe", existe);

        return ResponseEntity.ok(response);
    }

    // ==================== ESTADÍSTICAS Y REPORTES ====================

    /**
     * GET /api/categoria/estadisticas
     * Obtener estadísticas de categorías
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas de categorías");

        List<Categoria> todas = categoriaService.listar();
        List<Categoria> activas = categoriaService.listarActivas();
        List<Categoria> inactivas = categoriaService.listarInactivas();
        List<Categoria> conProductos = categoriaService.listarCategoriasConProductos();
        List<Categoria> sinProductos = categoriaService.listarCategoriasSinProductos();

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", todas.size());
        estadisticas.put("activas", activas.size());
        estadisticas.put("inactivas", inactivas.size());
        estadisticas.put("conProductos", conProductos.size());
        estadisticas.put("sinProductos", sinProductos.size());

        return ResponseEntity.ok(estadisticas);
    }

    /**
     * GET /api/categoria/reporte-productos
     * Reporte de productos por categoría
     */
    @GetMapping("/reporte-productos")
    public ResponseEntity<?> reporteProductosPorCategoria() {
        logger.info("Generando reporte de productos por categoría");
        List<Object[]> reporte = categoriaService.contarProductosPorCategoria();

        // Convertir Object[] a Map para mejor legibilidad
        List<Map<String, Object>> reporteFormateado = reporte.stream()
                .map(obj -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("categoria", obj[0]);
                    item.put("cantidadProductos", obj[1]);
                    return item;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("total", reporteFormateado.size());
        response.put("reporte", reporteFormateado);

        return ResponseEntity.ok(response);
    }
}