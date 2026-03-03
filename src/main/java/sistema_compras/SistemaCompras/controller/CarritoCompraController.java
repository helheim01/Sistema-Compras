package sistema_compras.SistemaCompras.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.CarritoCompra;
import sistema_compras.SistemaCompras.service.CarritoCompraService;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoCompraController {

    private static final Logger logger = LoggerFactory.getLogger(CarritoCompraController.class);

    @Autowired
    private CarritoCompraService carritoCompraService;

    // ==================== CRUD BÁSICO ====================

    /**
     * GET /api/carrito/listar
     * Listar todos los carritos
     */
    @GetMapping("/listar")
    public ResponseEntity<List<CarritoCompra>> listar() {
        logger.info("Listando todos los carritos");
        List<CarritoCompra> listaCarritoCompras = carritoCompraService.listar();
        return ResponseEntity.ok(listaCarritoCompras);
    }

    /**
     * GET /api/carrito/buscar/{id}
     * Buscar carrito por ID
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        logger.info("Buscando carrito con ID: {}", id);
        CarritoCompra carritoCompra = carritoCompraService.buscar(id);

        if (carritoCompra == null) {
            logger.warn("Carrito no encontrado con ID: {}", id);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Carrito no encontrado");
            error.put("id", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(carritoCompra);
    }

    /**
     * POST /api/carrito/agregar
     * Crear nuevo carrito
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody CarritoCompra carritoCompra) {
        try {
            logger.info("Creando nuevo carrito para cliente ID: {}",
                    carritoCompra.getCliente() != null ? carritoCompra.getCliente().getId() : "null");
            CarritoCompra carritoCompraAgregado = carritoCompraService.agregar(carritoCompra);
            return ResponseEntity.status(HttpStatus.CREATED).body(carritoCompraAgregado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al crear carrito: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/carrito/modificar
     * Modificar carrito existente
     */
    @PutMapping("/modificar")
    public ResponseEntity<?> modificar(@Valid @RequestBody CarritoCompra carritoCompra) {
        try {
            logger.info("Modificando carrito con ID: {}", carritoCompra.getId());
            CarritoCompra carritoCompraModificado = carritoCompraService.modificar(carritoCompra);
            return ResponseEntity.ok(carritoCompraModificado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al modificar carrito: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/carrito/eliminar/{id}
     * Eliminar carrito
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            logger.info("Eliminando carrito con ID: {}", id);
            carritoCompraService.eliminar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Carrito eliminado exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al eliminar carrito: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ==================== GESTIÓN DE PRODUCTOS EN EL CARRITO ====================

    /**
     * POST /api/carrito/{carritoId}/agregar-producto
     * Agregar producto al carrito
     * Body: { "productoId": 1, "cantidad": 2 }
     */
    @PostMapping("/{carritoId}/agregar-producto")
    public ResponseEntity<?> agregarProducto(
            @PathVariable Integer carritoId,
            @RequestBody Map<String, Integer> request) {

        Integer productoId = request.get("productoId");
        Integer cantidad = request.get("cantidad");

        if (productoId == null || cantidad == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "productoId y cantidad son obligatorios");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        if (cantidad <= 0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La cantidad debe ser mayor a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Agregando producto {} al carrito {}. Cantidad: {}",
                    productoId, carritoId, cantidad);
            CarritoCompra carrito = carritoCompraService.agregarProducto(carritoId, productoId, cantidad);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Producto agregado al carrito");
            response.put("carrito", carrito);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al agregar producto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/carrito/{carritoId}/eliminar-producto/{productoId}
     * Eliminar producto del carrito
     */
    @DeleteMapping("/{carritoId}/eliminar-producto/{productoId}")
    public ResponseEntity<?> eliminarProducto(
            @PathVariable Integer carritoId,
            @PathVariable Integer productoId) {

        try {
            logger.info("Eliminando producto {} del carrito {}", productoId, carritoId);
            CarritoCompra carrito = carritoCompraService.eliminarProducto(carritoId, productoId);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Producto eliminado del carrito");
            response.put("carrito", carrito);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al eliminar producto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/carrito/{carritoId}/actualizar-cantidad/{productoId}
     * Actualizar cantidad de un producto en el carrito
     * Body: { "cantidad": 5 }
     */
    @PutMapping("/{carritoId}/actualizar-cantidad/{productoId}")
    public ResponseEntity<?> actualizarCantidad(
            @PathVariable Integer carritoId,
            @PathVariable Integer productoId,
            @RequestBody Map<String, Integer> request) {

        Integer nuevaCantidad = request.get("cantidad");

        if (nuevaCantidad == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La cantidad es obligatoria");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Actualizando cantidad de producto {} en carrito {} a {}",
                    productoId, carritoId, nuevaCantidad);
            CarritoCompra carrito = carritoCompraService.actualizarCantidad(carritoId, productoId, nuevaCantidad);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Cantidad actualizada");
            response.put("carrito", carrito);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al actualizar cantidad: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/carrito/{carritoId}/vaciar
     * Vaciar carrito (eliminar todos los productos)
     */
    @DeleteMapping("/{carritoId}/vaciar")
    public ResponseEntity<?> vaciarCarrito(@PathVariable Integer carritoId) {
        try {
            logger.info("Vaciando carrito {}", carritoId);
            carritoCompraService.vaciarCarrito(carritoId);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Carrito vaciado exitosamente");
            response.put("carritoId", carritoId.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al vaciar carrito: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * GET /api/carrito/cliente/{clienteId}
     * Buscar carrito de un cliente específico
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> buscarPorCliente(@PathVariable Integer clienteId) {
        logger.info("Buscando carrito para cliente ID: {}", clienteId);
        CarritoCompra carrito = carritoCompraService.buscarPorCliente(clienteId);

        if (carrito == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No se encontró carrito para este cliente");
            error.put("clienteId", clienteId.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(carrito);
    }

    /**
     * GET /api/carrito/vacios
     * Listar carritos vacíos (sin productos)
     */
    @GetMapping("/vacios")
    public ResponseEntity<?> listarCarritosVacios() {
        logger.info("Listando carritos vacíos");
        List<CarritoCompra> carritos = carritoCompraService.buscarCarritosVacios();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", carritos.size());
        response.put("carritos", carritos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/carrito/abandonados
     * Listar carritos abandonados (sin actualizar por más de X días)
     * Param: dias (opcional, default: 7)
     * Ejemplo: /api/carrito/abandonados?dias=30
     */
    @GetMapping("/abandonados")
    public ResponseEntity<?> listarCarritosAbandonados(
            @RequestParam(defaultValue = "7") Integer dias) {

        logger.info("Listando carritos abandonados (más de {} días sin actualizar)", dias);
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(dias);
        List<CarritoCompra> carritos = carritoCompraService.buscarCarritosAbandonados(fechaLimite);

        Map<String, Object> response = new HashMap<>();
        response.put("diasAbandonados", dias);
        response.put("cantidad", carritos.size());
        response.put("carritos", carritos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/carrito/con-productos
     * Listar carritos que tienen productos
     */
    @GetMapping("/con-productos")
    public ResponseEntity<?> listarCarritosConProductos() {
        logger.info("Listando carritos con productos");
        List<CarritoCompra> carritos = carritoCompraService.buscarCarritosConProductos();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", carritos.size());
        response.put("carritos", carritos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/carrito/estadisticas
     * Obtener estadísticas de carritos
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas de carritos");

        List<CarritoCompra> todosCarritos = carritoCompraService.listar();
        List<CarritoCompra> carritosVacios = carritoCompraService.buscarCarritosVacios();
        List<CarritoCompra> carritosConProductos = carritoCompraService.buscarCarritosConProductos();
        List<CarritoCompra> carritosAbandonados = carritoCompraService.buscarCarritosAbandonados(
                LocalDateTime.now().minusDays(7)
        );

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", todosCarritos.size());
        estadisticas.put("vacios", carritosVacios.size());
        estadisticas.put("conProductos", carritosConProductos.size());
        estadisticas.put("abandonados", carritosAbandonados.size());

        return ResponseEntity.ok(estadisticas);
    }
}