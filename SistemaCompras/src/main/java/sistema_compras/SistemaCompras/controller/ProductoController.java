package sistema_compras.SistemaCompras.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.ImagenProducto;
import sistema_compras.SistemaCompras.entity.Producto;
import sistema_compras.SistemaCompras.service.ProductoService;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/producto")
@CrossOrigin(origins = "*")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    // ==================== CRUD BÁSICO ====================

    /**
     * GET /api/producto/listar
     * Listar todos los productos
     */
    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listar() {
        logger.info("Listando todos los productos");
        List<Producto> listaProducto = productoService.listar();
        return ResponseEntity.ok(listaProducto);
    }

    /**
     * GET /api/producto/buscar/{id}
     * Buscar producto por ID
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        logger.info("Buscando producto con ID: {}", id);
        Producto producto = productoService.buscar(id);

        if (producto == null) {
            logger.warn("Producto no encontrado con ID: {}", id);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Producto no encontrado");
            error.put("id", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(producto);
    }

    /**
     * POST /api/producto/agregar
     * Agregar nuevo producto
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Producto producto) {
        try {
            logger.info("Agregando nuevo producto: {}", producto.getNombre());
            Producto productoAgregado = productoService.agregar(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(productoAgregado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al agregar producto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/producto/modificar
     * Modificar producto existente
     */
    @PutMapping("/modificar")
    public ResponseEntity<?> modificar(@Valid @RequestBody Producto producto) {
        try {
            logger.info("Modificando producto con ID: {}", producto.getId());
            Producto productoModificado = productoService.modificar(producto);
            return ResponseEntity.ok(productoModificado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al modificar producto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/producto/eliminar/{id}
     * Eliminar producto
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            logger.info("Eliminando producto con ID: {}", id);
            productoService.eliminar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Producto eliminado exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al eliminar producto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ==================== GESTIÓN DE IMÁGENES ====================

    /**
     * POST /api/producto/{id}/imagenes
     * Agregar imágenes a un producto
     * Body: [{ "urlImagen": "http://...", "orden": 0 }, ...]
     */
    @PostMapping("/{id}/imagenes")
    public ResponseEntity<?> agregarImagenes(
            @PathVariable Integer id,
            @RequestBody List<ImagenProducto> imagenes) {

        try {
            logger.info("Agregando {} imágenes al producto ID: {}", imagenes.size(), id);
            Producto producto = productoService.agregarImagenes(id, imagenes);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Imágenes agregadas exitosamente");
            response.put("cantidadImagenes", imagenes.size());
            response.put("producto", producto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al agregar imágenes: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== GESTIÓN DE STOCK ====================

    /**
     * PUT /api/producto/{id}/stock
     * Actualizar stock de un producto
     * Body: { "cantidad": 10 }
     * Nota: Cantidad positiva suma, negativa resta
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> data) {

        Integer cantidad = data.get("cantidad");

        if (cantidad == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La cantidad es obligatoria");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Actualizando stock del producto ID: {}. Cantidad: {}", id, cantidad);
            productoService.actualizarStock(id, cantidad);

            Producto producto = productoService.buscar(id);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Stock actualizado exitosamente");
            response.put("nuevoStock", producto.getStock());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al actualizar stock: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/producto/{id}/disponibilidad
     * Verificar disponibilidad de un producto
     * Param: cantidad (requerida)
     * Ejemplo: /api/producto/1/disponibilidad?cantidad=5
     */
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<?> verificarDisponibilidad(
            @PathVariable Integer id,
            @RequestParam Integer cantidad) {

        logger.info("Verificando disponibilidad de producto ID: {} para cantidad: {}", id, cantidad);

        Producto producto = productoService.buscar(id);
        if (producto == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Producto no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        boolean disponible = productoService.verificarDisponibilidad(id, cantidad);

        Map<String, Object> response = new HashMap<>();
        response.put("productoId", id);
        response.put("productoNombre", producto.getNombre());
        response.put("stockActual", producto.getStock());
        response.put("cantidadSolicitada", cantidad);
        response.put("disponible", disponible);

        return ResponseEntity.ok(response);
    }

    // ==================== GESTIÓN DE PRECIOS ====================

    /**
     * PUT /api/producto/{id}/descuento
     * Aplicar descuento a un producto
     * Body: { "porcentaje": 20 }
     */
    @PutMapping("/{id}/descuento")
    public ResponseEntity<?> aplicarDescuento(
            @PathVariable Integer id,
            @RequestBody Map<String, String> data) {

        String porcentajeStr = data.get("porcentaje");

        if (porcentajeStr == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El porcentaje es obligatorio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            BigDecimal porcentaje = new BigDecimal(porcentajeStr);

            if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(BigDecimal.valueOf(100)) > 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El porcentaje debe estar entre 0 y 100");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            logger.info("Aplicando {}% de descuento al producto ID: {}", porcentaje, id);
            productoService.aplicarDescuento(id, porcentaje);

            Producto producto = productoService.buscar(id);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Descuento aplicado exitosamente");
            response.put("porcentajeDescuento", porcentaje);
            response.put("nuevoPrecio", producto.getPrecio());
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Porcentaje inválido. Debe ser un número");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalArgumentException e) {
            logger.error("Error al aplicar descuento: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== ACTIVAR/DESACTIVAR ====================

    /**
     * PUT /api/producto/{id}/activar
     * Activar un producto
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Integer id) {
        try {
            logger.info("Activando producto ID: {}", id);
            productoService.activar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Producto activado exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al activar producto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al activar producto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * PUT /api/producto/{id}/desactivar
     * Desactivar un producto
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable Integer id) {
        try {
            logger.info("Desactivando producto ID: {}", id);
            productoService.desactivar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Producto desactivado exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al desactivar producto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al desactivar producto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ==================== BÚSQUEDAS ESPECÍFICAS ====================

    /**
     * GET /api/producto/codigo/{codigo}
     * Buscar producto por código
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        logger.info("Buscando producto por código: {}", codigo);
        Producto producto = productoService.buscarPorCodigo(codigo);

        if (producto == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Producto no encontrado con ese código");
            error.put("codigo", codigo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(producto);
    }

    /**
     * GET /api/producto/nombre/{nombre}
     * Buscar productos por nombre (búsqueda parcial)
     */
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre) {
        logger.info("Buscando productos por nombre: {}", nombre);
        List<Producto> productos = productoService.buscarPorNombre(nombre);

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/categoria/{categoriaId}
     * Buscar productos de una categoría (todos)
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> buscarPorCategoria(@PathVariable Integer categoriaId) {
        logger.info("Buscando productos de categoría ID: {}", categoriaId);
        List<Producto> productos = productoService.buscarPorCategoria(categoriaId);

        Map<String, Object> response = new HashMap<>();
        response.put("categoriaId", categoriaId);
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/categoria/{categoriaId}/activos
     * Buscar productos activos de una categoría
     */
    @GetMapping("/categoria/{categoriaId}/activos")
    public ResponseEntity<?> buscarPorCategoriaActivos(@PathVariable Integer categoriaId) {
        logger.info("Buscando productos activos de categoría ID: {}", categoriaId);
        List<Producto> productos = productoService.buscarPorCategoriaActivos(categoriaId);

        Map<String, Object> response = new HashMap<>();
        response.put("categoriaId", categoriaId);
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/proveedor/{proveedor}
     * Buscar productos por proveedor
     */
    @GetMapping("/proveedor/{proveedor}")
    public ResponseEntity<?> buscarPorProveedor(@PathVariable String proveedor) {
        logger.info("Buscando productos del proveedor: {}", proveedor);
        List<Producto> productos = productoService.buscarPorProveedor(proveedor);

        Map<String, Object> response = new HashMap<>();
        response.put("proveedor", proveedor);
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/activos
     * Listar productos activos
     */
    @GetMapping("/activos")
    public ResponseEntity<?> listarActivos() {
        logger.info("Listando productos activos");
        List<Producto> productos = productoService.listarActivos();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/inactivos
     * Listar productos inactivos
     */
    @GetMapping("/inactivos")
    public ResponseEntity<?> listarInactivos() {
        logger.info("Listando productos inactivos");
        List<Producto> productos = productoService.listarInactivos();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/rango-precio
     * Buscar productos por rango de precio
     * Params: min, max
     * Ejemplo: /api/producto/rango-precio?min=100&max=500
     */
    @GetMapping("/rango-precio")
    public ResponseEntity<?> buscarPorRangoPrecio(
            @RequestParam String min,
            @RequestParam String max) {

        try {
            BigDecimal precioMin = new BigDecimal(min);
            BigDecimal precioMax = new BigDecimal(max);

            logger.info("Buscando productos entre ${} y ${}", precioMin, precioMax);
            List<Producto> productos = productoService.buscarPorRangoPrecio(precioMin, precioMax);

            Map<String, Object> response = new HashMap<>();
            response.put("precioMinimo", precioMin);
            response.put("precioMaximo", precioMax);
            response.put("cantidad", productos.size());
            response.put("productos", productos);

            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Precios inválidos. Deben ser números");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/producto/stock-bajo
     * Buscar productos con stock bajo
     * Param: minimo (opcional, default: 10)
     * Ejemplo: /api/producto/stock-bajo?minimo=5
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<?> buscarConStockBajo(
            @RequestParam(defaultValue = "10") Integer minimo) {

        logger.info("Buscando productos con stock menor a: {}", minimo);
        List<Producto> productos = productoService.buscarProductosConStockBajo(minimo);

        Map<String, Object> response = new HashMap<>();
        response.put("stockMinimo", minimo);
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/sin-stock
     * Buscar productos sin stock (stock = 0)
     */
    @GetMapping("/sin-stock")
    public ResponseEntity<?> buscarSinStock() {
        logger.info("Buscando productos sin stock");
        List<Producto> productos = productoService.buscarPorStock(0);

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/mas-caros
     * Top 10 productos más caros
     */
    @GetMapping("/mas-caros")
    public ResponseEntity<?> listarMasCaros() {
        logger.info("Listando top 10 productos más caros");
        List<Producto> productos = productoService.listarMasCaros();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/mas-baratos
     * Top 10 productos más baratos
     */
    @GetMapping("/mas-baratos")
    public ResponseEntity<?> listarMasBaratos() {
        logger.info("Listando top 10 productos más baratos");
        List<Producto> productos = productoService.listarMasBaratos();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", productos.size());
        response.put("productos", productos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/producto/busqueda-avanzada
     * Búsqueda avanzada de productos
     * Params: nombre, categoriaId, precioMin, precioMax (todos opcionales)
     * Ejemplo: /api/producto/busqueda-avanzada?nombre=laptop&categoriaId=1&precioMin=500&precioMax=2000
     */
    @GetMapping("/busqueda-avanzada")
    public ResponseEntity<?> busquedaAvanzada(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String precioMin,
            @RequestParam(required = false) String precioMax) {

        try {
            BigDecimal min = precioMin != null ? new BigDecimal(precioMin) : null;
            BigDecimal max = precioMax != null ? new BigDecimal(precioMax) : null;

            logger.info("Búsqueda avanzada - Nombre: {}, Categoría: {}, Precio: {} - {}",
                    nombre, categoriaId, min, max);

            List<Producto> productos = productoService.busquedaAvanzada(nombre, categoriaId, min, max);

            Map<String, Object> response = new HashMap<>();
            response.put("filtros", Map.of(
                    "nombre", nombre != null ? nombre : "todos",
                    "categoriaId", categoriaId != null ? categoriaId : "todas",
                    "precioMin", min != null ? min : "sin límite",
                    "precioMax", max != null ? max : "sin límite"
            ));
            response.put("cantidad", productos.size());
            response.put("productos", productos);

            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Precios inválidos. Deben ser números");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== ESTADÍSTICAS ====================

    /**
     * GET /api/producto/estadisticas
     * Obtener estadísticas generales de productos
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas de productos");

        List<Producto> todos = productoService.listar();
        List<Producto> activos = productoService.listarActivos();
        List<Producto> inactivos = productoService.listarInactivos();
        List<Producto> sinStock = productoService.buscarPorStock(0);
        List<Producto> stockBajo = productoService.buscarProductosConStockBajo(10);

        BigDecimal precioPromedio = todos.stream()
                .map(Producto::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(todos.size()), 2, BigDecimal.ROUND_HALF_UP);

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalProductos", todos.size());
        estadisticas.put("productosActivos", activos.size());
        estadisticas.put("productosInactivos", inactivos.size());
        estadisticas.put("productosSinStock", sinStock.size());
        estadisticas.put("productosConStockBajo", stockBajo.size());
        estadisticas.put("precioPromedio", precioPromedio);

        return ResponseEntity.ok(estadisticas);
    }
}