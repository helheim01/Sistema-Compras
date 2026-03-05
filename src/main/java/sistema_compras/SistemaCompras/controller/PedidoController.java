package sistema_compras.SistemaCompras.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.EstadoPedido;
import sistema_compras.SistemaCompras.entity.Pedido;
import sistema_compras.SistemaCompras.service.PedidoService;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/pedido")
@CrossOrigin(origins = "*")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private PedidoService pedidoService;

    // ==================== CRUD BÁSICO ====================

    /**
     * GET /api/pedido/listar
     * Listar todos los pedidos
     */
    @GetMapping("/listar")
    public ResponseEntity<List<Pedido>> listar() {
        logger.info("Listando todos los pedidos");
        List<Pedido> listaPedido = pedidoService.listar();
        return ResponseEntity.ok(listaPedido);
    }

    /**
     * GET /api/pedido/buscar/{id}
     * Buscar pedido por ID
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        logger.info("Buscando pedido con ID: {}", id);
        Pedido pedido = pedidoService.buscar(id);

        if (pedido == null) {
            logger.warn("Pedido no encontrado con ID: {}", id);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Pedido no encontrado");
            error.put("id", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(pedido);
    }

    /**
     * POST /api/pedido/agregar
     * Crear nuevo pedido
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Pedido pedido) {
        try {
            logger.info("Agregando nuevo pedido para cliente ID: {}",
                    pedido.getCliente() != null ? pedido.getCliente().getId() : "null");
            Pedido pedidoAgregado = pedidoService.agregar(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoAgregado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al agregar pedido: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/pedido/modificar
     * Modificar pedido existente
     */
    @PutMapping("/modificar")
    public ResponseEntity<?> modificar(@Valid @RequestBody Pedido pedido) {
        try {
            logger.info("Modificando pedido con ID: {}", pedido.getId());
            Pedido pedidoModificado = pedidoService.modificar(pedido);
            return ResponseEntity.ok(pedidoModificado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al modificar pedido: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/pedido/eliminar/{id}
     * Eliminar pedido
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            logger.info("Eliminando pedido con ID: {}", id);
            pedidoService.eliminar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Pedido eliminado exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al eliminar pedido: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ==================== CREACIÓN DE PEDIDOS ====================

    /**
     * POST /api/pedido/crear-desde-carrito
     * Crear pedido desde un carrito de compras
     * Body: { "carritoId": 1, "direccionEnvio": "Calle 123, Córdoba" }
     */
    @PostMapping("/crear-desde-carrito")
    public ResponseEntity<?> crearPedidoDesdeCarrito(@RequestBody Map<String, Object> request) {
        Integer carritoId = (Integer) request.get("carritoId");
        String direccionEnvio = (String) request.get("direccionEnvio");

        if (carritoId == null || direccionEnvio == null || direccionEnvio.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "carritoId y direccionEnvio son obligatorios");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Creando pedido desde carrito ID: {}", carritoId);
            Pedido pedido = pedidoService.crearPedidoDesdeCarrito(carritoId, direccionEnvio);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Pedido creado exitosamente desde el carrito");
            response.put("pedido", pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al crear pedido desde carrito: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== GESTIÓN DE PEDIDOS ====================

    /**
     * PUT /api/pedido/{id}/actualizar-estado
     * Actualizar estado de un pedido
     * Body: { "estado": "ENVIADO" }
     */
    @PutMapping("/{id}/actualizar-estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> data) {

        String estadoStr = data.get("estado");

        if (estadoStr == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El estado es obligatorio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            EstadoPedido nuevoEstado = EstadoPedido.valueOf(estadoStr);
            logger.info("Actualizando estado de pedido {} a {}", id, nuevoEstado);
            pedidoService.actualizarEstado(id, nuevoEstado);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Estado actualizado exitosamente");
            response.put("nuevoEstado", nuevoEstado.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al actualizar estado: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/pedido/{id}/cancelar
     * Cancelar un pedido
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Integer id) {
        try {
            logger.info("Cancelando pedido ID: {}", id);
            pedidoService.cancelarPedido(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Pedido cancelado exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al cancelar pedido: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== BÚSQUEDAS ESPECÍFICAS ====================

    /**
     * GET /api/pedido/numero/{numeroPedido}
     * Buscar pedido por número (ej: PED-20250303-00001)
     */
    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<?> buscarPorNumeroPedido(@PathVariable String numeroPedido) {
        logger.info("Buscando pedido por número: {}", numeroPedido);
        Pedido pedido = pedidoService.buscarPorNumeroPedido(numeroPedido);

        if (pedido == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Pedido no encontrado con ese número");
            error.put("numeroPedido", numeroPedido);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(pedido);
    }

    /**
     * GET /api/pedido/cliente/{clienteId}
     * Buscar todos los pedidos de un cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> buscarPorCliente(@PathVariable Integer clienteId) {
        logger.info("Buscando pedidos de cliente ID: {}", clienteId);
        List<Pedido> pedidos = pedidoService.buscarPorCliente(clienteId);

        Map<String, Object> response = new HashMap<>();
        response.put("clienteId", clienteId);
        response.put("cantidad", pedidos.size());
        response.put("pedidos", pedidos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/pedido/cliente/{clienteId}/recientes
     * Buscar últimos 10 pedidos de un cliente
     */
    @GetMapping("/cliente/{clienteId}/recientes")
    public ResponseEntity<?> buscarPedidosRecientesCliente(@PathVariable Integer clienteId) {
        logger.info("Buscando pedidos recientes de cliente ID: {}", clienteId);
        List<Pedido> pedidos = pedidoService.buscarPedidosRecientesCliente(clienteId);

        Map<String, Object> response = new HashMap<>();
        response.put("clienteId", clienteId);
        response.put("cantidad", pedidos.size());
        response.put("pedidos", pedidos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/pedido/cuenta/{cuentaId}
     * Buscar pedidos de una cuenta
     */
    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<?> buscarPorCuenta(@PathVariable Integer cuentaId) {
        logger.info("Buscando pedidos de cuenta ID: {}", cuentaId);
        List<Pedido> pedidos = pedidoService.buscarPorCuenta(cuentaId);

        Map<String, Object> response = new HashMap<>();
        response.put("cuentaId", cuentaId);
        response.put("cantidad", pedidos.size());
        response.put("pedidos", pedidos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/pedido/estado/{estado}
     * Buscar pedidos por estado
     * Estados válidos: NUEVO, PENDIENTE, PROCESANDO, ENVIADO, ENTREGADO, CERRADO, CANCELADO, PROHIBIDO
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> buscarPorEstado(@PathVariable String estado) {
        try {
            EstadoPedido estadoPedido = EstadoPedido.valueOf(estado.toUpperCase());
            logger.info("Buscando pedidos con estado: {}", estadoPedido);

            List<Pedido> pedidos = pedidoService.buscarPorEstado(estadoPedido);

            Map<String, Object> response = new HashMap<>();
            response.put("estado", estadoPedido);
            response.put("cantidad", pedidos.size());
            response.put("pedidos", pedidos);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Estado inválido. Estados válidos: NUEVO, PENDIENTE, PROCESANDO, ENVIADO, ENTREGADO, CERRADO, CANCELADO, PROHIBIDO");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/pedido/cliente/{clienteId}/estado/{estado}
     * Buscar pedidos de un cliente con un estado específico
     */
    @GetMapping("/cliente/{clienteId}/estado/{estado}")
    public ResponseEntity<?> buscarPorClienteYEstado(
            @PathVariable Integer clienteId,
            @PathVariable String estado) {

        try {
            EstadoPedido estadoPedido = EstadoPedido.valueOf(estado.toUpperCase());
            logger.info("Buscando pedidos de cliente {} con estado {}", clienteId, estadoPedido);

            List<Pedido> pedidos = pedidoService.buscarPorClienteYEstado(clienteId, estadoPedido);

            Map<String, Object> response = new HashMap<>();
            response.put("clienteId", clienteId);
            response.put("estado", estadoPedido);
            response.put("cantidad", pedidos.size());
            response.put("pedidos", pedidos);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Estado inválido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/pedido/rango-fechas
     * Buscar pedidos por rango de fechas
     * Params: inicio, fin (formato: yyyy-MM-dd'T'HH:mm:ss)
     * Ejemplo: /api/pedido/rango-fechas?inicio=2025-03-01T00:00:00&fin=2025-03-31T23:59:59
     */
    @GetMapping("/rango-fechas")
    public ResponseEntity<?> buscarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        logger.info("Buscando pedidos entre {} y {}", inicio, fin);
        List<Pedido> pedidos = pedidoService.buscarPorRangoFechas(inicio, fin);

        Map<String, Object> response = new HashMap<>();
        response.put("inicio", inicio);
        response.put("fin", fin);
        response.put("cantidad", pedidos.size());
        response.put("pedidos", pedidos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/pedido/pendientes-envio
     * Listar pedidos pendientes de envío (NUEVO, PENDIENTE, PROCESANDO)
     */
    @GetMapping("/pendientes-envio")
    public ResponseEntity<?> listarPendientesEnvio() {
        logger.info("Listando pedidos pendientes de envío");
        List<Pedido> pedidos = pedidoService.buscarPedidosPendientesEnvio();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", pedidos.size());
        response.put("pedidos", pedidos);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/pedido/total-minimo/{total}
     * Buscar pedidos con total mayor o igual a cierta cantidad
     */
    @GetMapping("/total-minimo/{total}")
    public ResponseEntity<?> buscarPorTotalMinimo(@PathVariable String total) {
        try {
            BigDecimal totalMinimo = new BigDecimal(total);
            logger.info("Buscando pedidos con total >= {}", totalMinimo);

            List<Pedido> pedidos = pedidoService.buscarPorTotalMayorIgual(totalMinimo);

            Map<String, Object> response = new HashMap<>();
            response.put("totalMinimo", totalMinimo);
            response.put("cantidad", pedidos.size());
            response.put("pedidos", pedidos);

            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Total inválido. Debe ser un número");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== REPORTES Y ESTADÍSTICAS ====================

    /**
     * GET /api/pedido/ventas-periodo
     * Calcular total de ventas en un período
     * Params: inicio, fin (formato: yyyy-MM-dd'T'HH:mm:ss)
     * Ejemplo: /api/pedido/ventas-periodo?inicio=2025-03-01T00:00:00&fin=2025-03-31T23:59:59
     */
    @GetMapping("/ventas-periodo")
    public ResponseEntity<?> calcularVentasPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        logger.info("Calculando ventas entre {} y {}", inicio, fin);
        BigDecimal totalVentas = pedidoService.calcularTotalVentas(inicio, fin);

        Map<String, Object> response = new HashMap<>();
        response.put("inicio", inicio);
        response.put("fin", fin);
        response.put("totalVentas", totalVentas);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/pedido/estadisticas-estado
     * Contar pedidos agrupados por estado
     */
    @GetMapping("/estadisticas-estado")
    public ResponseEntity<?> contarPorEstado() {
        logger.info("Obteniendo estadísticas de pedidos por estado");
        List<Object[]> estadisticas = pedidoService.contarPedidosPorEstado();

        Map<String, Long> estadoPorCantidad = new HashMap<>();
        for (Object[] stat : estadisticas) {
            EstadoPedido estado = (EstadoPedido) stat[0];
            Long cantidad = (Long) stat[1];
            estadoPorCantidad.put(estado.toString(), cantidad);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("estadisticas", estadoPorCantidad);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/pedido/estadisticas
     * Obtener estadísticas generales de pedidos
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas generales de pedidos");

        List<Pedido> todosPedidos = pedidoService.listar();
        List<Pedido> pendientes = pedidoService.buscarPedidosPendientesEnvio();

        long cancelados = todosPedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.CANCELADO)
                .count();

        long entregados = todosPedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO)
                .count();

        BigDecimal totalVentas = todosPedidos.stream()
                .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalPedidos", todosPedidos.size());
        estadisticas.put("pendientesEnvio", pendientes.size());
        estadisticas.put("cancelados", cancelados);
        estadisticas.put("entregados", entregados);
        estadisticas.put("totalVentas", totalVentas);

        return ResponseEntity.ok(estadisticas);
    }

    /**
     * GET /api/pedido/{id}/detalle
     * Obtener detalle completo de un pedido
     */
    @GetMapping("/{id}/detalle")
    public ResponseEntity<?> obtenerDetalle(@PathVariable Integer id) {
        logger.info("Obteniendo detalle completo de pedido ID: {}", id);

        Pedido pedido = pedidoService.buscar(id);
        if (pedido == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Map<String, Object> detalle = new HashMap<>();
        detalle.put("pedidoId", pedido.getId());
        detalle.put("numeroPedido", pedido.getNumeroPedido());
        detalle.put("cliente", pedido.getCliente().getNombre());
        detalle.put("direccionEnvio", pedido.getDireccionEnvio());
        detalle.put("estado", pedido.getEstado());
        detalle.put("fechaPedido", pedido.getFechaPedido());
        detalle.put("fechaEnvio", pedido.getFechaEnvio());
        detalle.put("fechaEntrega", pedido.getFechaEntrega());
        detalle.put("subtotal", pedido.getSubtotal());
        detalle.put("impuesto", pedido.getImpuesto());
        detalle.put("costoEnvio", pedido.getCostoEnvio());
        detalle.put("total", pedido.getTotal());
        detalle.put("notasEspeciales", pedido.getNotasEspeciales());
        detalle.put("cantidadProductos", pedido.getLineas().size());
        detalle.put("lineas", pedido.getLineas());
        detalle.put("pagos", pedido.getPagos());

        return ResponseEntity.ok(detalle);
    }
}