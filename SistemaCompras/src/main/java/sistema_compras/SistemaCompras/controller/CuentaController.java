package sistema_compras.SistemaCompras.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.Cuenta;
import sistema_compras.SistemaCompras.service.CuentaService;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/cuenta")
@CrossOrigin(origins = "*")
public class CuentaController {

    private static final Logger logger = LoggerFactory.getLogger(CuentaController.class);

    @Autowired
    private CuentaService cuentaService;

    // ==================== CRUD BÁSICO ====================

    /**
     * GET /api/cuenta/listar
     * Listar todas las cuentas
     */
    @GetMapping("/listar")
    public ResponseEntity<List<Cuenta>> listar() {
        logger.info("Listando todas las cuentas");
        List<Cuenta> listarCuenta = cuentaService.listar();
        return ResponseEntity.ok(listarCuenta);
    }

    /**
     * GET /api/cuenta/buscar/{id}
     * Buscar cuenta por ID
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        logger.info("Buscando cuenta con ID: {}", id);
        Cuenta cuenta = cuentaService.buscar(id);

        if (cuenta == null) {
            logger.warn("Cuenta no encontrada con ID: {}", id);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cuenta no encontrada");
            error.put("id", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(cuenta);
    }

    /**
     * POST /api/cuenta/agregar
     * Crear nueva cuenta
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Cuenta cuenta) {
        try {
            logger.info("Creando nueva cuenta para cliente ID: {}",
                    cuenta.getCliente() != null ? cuenta.getCliente().getId() : "null");
            Cuenta cuentaAgregada = cuentaService.agregar(cuenta);
            return ResponseEntity.status(HttpStatus.CREATED).body(cuentaAgregada);
        } catch (IllegalArgumentException e) {
            logger.error("Error al crear cuenta: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/cuenta/modificar
     * Modificar cuenta existente
     */
    @PutMapping("/modificar")
    public ResponseEntity<?> modificar(@Valid @RequestBody Cuenta cuenta) {
        try {
            logger.info("Modificando cuenta con ID: {}", cuenta.getId());
            Cuenta cuentaModificada = cuentaService.modificar(cuenta);
            return ResponseEntity.ok(cuentaModificada);
        } catch (IllegalArgumentException e) {
            logger.error("Error al modificar cuenta: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/cuenta/eliminar/{id}
     * Eliminar cuenta
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            logger.info("Eliminando cuenta con ID: {}", id);
            cuentaService.eliminar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cuenta eliminada exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al eliminar cuenta: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ==================== GESTIÓN DE CUENTA ====================

    /**
     * PUT /api/cuenta/{id}/abrir
     * Abrir una cuenta cerrada
     */
    @PutMapping("/{id}/abrir")
    public ResponseEntity<?> abrirCuenta(@PathVariable Integer id) {
        try {
            logger.info("Abriendo cuenta ID: {}", id);
            cuentaService.abrirCuenta(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cuenta abierta exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al abrir cuenta: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/cuenta/{id}/cerrar
     * Cerrar una cuenta
     */
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrarCuenta(@PathVariable Integer id) {
        try {
            logger.info("Cerrando cuenta ID: {}", id);
            cuentaService.cerrarCuenta(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cuenta cerrada exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al cerrar cuenta: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/cuenta/{id}/actualizar-direccion
     * Actualizar dirección de facturación
     * Body: { "direccion": "Nueva dirección 123" }
     */
    @PutMapping("/{id}/actualizar-direccion")
    public ResponseEntity<?> actualizarDireccionFacturacion(
            @PathVariable Integer id,
            @RequestBody Map<String, String> data) {

        String direccion = data.get("direccion");

        if (direccion == null || direccion.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La dirección es obligatoria");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Actualizando dirección de facturación para cuenta ID: {}", id);
            cuentaService.actualizarDireccionFacturacion(id, direccion);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Dirección actualizada exitosamente");
            response.put("nuevaDireccion", direccion);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al actualizar dirección: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/cuenta/{id}/actualizar-saldo
     * Actualizar saldo pendiente
     * Body: { "monto": 100.50 }
     * Nota: Monto positivo suma, negativo resta
     */
    @PutMapping("/{id}/actualizar-saldo")
    public ResponseEntity<?> actualizarSaldoPendiente(
            @PathVariable Integer id,
            @RequestBody Map<String, String> data) {

        String montoStr = data.get("monto");

        if (montoStr == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El monto es obligatorio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);
            logger.info("Actualizando saldo pendiente para cuenta ID: {}. Monto: {}", id, monto);
            cuentaService.actualizarSaldoPendiente(id, monto);

            Cuenta cuenta = cuentaService.buscar(id);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Saldo actualizado exitosamente");
            response.put("nuevoSaldo", cuenta.getSaldoPendiente());
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Monto inválido. Debe ser un número");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalArgumentException e) {
            logger.error("Error al actualizar saldo: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * GET /api/cuenta/cliente/{clienteId}
     * Buscar cuenta de un cliente específico
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> buscarPorCliente(@PathVariable Integer clienteId) {
        logger.info("Buscando cuenta para cliente ID: {}", clienteId);
        Cuenta cuenta = cuentaService.buscarPorCliente(clienteId);

        if (cuenta == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No se encontró cuenta para este cliente");
            error.put("clienteId", clienteId.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(cuenta);
    }

    /**
     * GET /api/cuenta/{id}/pedidos
     * Contar pedidos de una cuenta
     */
    @GetMapping("/{id}/pedidos")
    public ResponseEntity<?> contarPedidos(@PathVariable Integer id) {
        logger.info("Contando pedidos para cuenta ID: {}", id);

        try {
            Integer cantidad = cuentaService.contarPedidos(id);
            Long cantidadQuery = cuentaService.contarPedidosPorCuentaQuery(id);

            Map<String, Object> response = new HashMap<>();
            response.put("cuentaId", id);
            response.put("cantidadPedidos", cantidad);
            response.put("cantidadPedidosQuery", cantidadQuery);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al contar pedidos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al contar pedidos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/cuenta/cerradas
     * Listar cuentas cerradas
     */
    @GetMapping("/cerradas")
    public ResponseEntity<?> listarCuentasCerradas() {
        logger.info("Listando cuentas cerradas");
        List<Cuenta> cuentas = cuentaService.buscarCuentasCerradas();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", cuentas.size());
        response.put("cuentas", cuentas);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/cuenta/activas
     * Listar cuentas activas (no cerradas)
     */
    @GetMapping("/activas")
    public ResponseEntity<?> listarCuentasActivas() {
        logger.info("Listando cuentas activas");
        List<Cuenta> cuentas = cuentaService.buscarCuentasActivas();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", cuentas.size());
        response.put("cuentas", cuentas);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/cuenta/con-saldo
     * Listar cuentas con saldo pendiente
     * Param: minimo (opcional, default: 0)
     * Ejemplo: /api/cuenta/con-saldo?minimo=100
     */
    @GetMapping("/con-saldo")
    public ResponseEntity<?> listarCuentasConSaldo(
            @RequestParam(defaultValue = "0") String minimo) {

        try {
            BigDecimal saldoMinimo = new BigDecimal(minimo);
            logger.info("Listando cuentas con saldo mayor a: {}", saldoMinimo);

            List<Cuenta> cuentas = cuentaService.buscarCuentasConSaldoPendiente(saldoMinimo);

            BigDecimal totalSaldoPendiente = cuentas.stream()
                    .map(Cuenta::getSaldoPendiente)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> response = new HashMap<>();
            response.put("saldoMinimoBuscado", saldoMinimo);
            response.put("cantidad", cuentas.size());
            response.put("totalSaldoPendiente", totalSaldoPendiente);
            response.put("cuentas", cuentas);

            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Mínimo inválido. Debe ser un número");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/cuenta/{id}/detalle
     * Obtener detalle completo de una cuenta con sus pedidos y pagos
     */
    @GetMapping("/{id}/detalle")
    public ResponseEntity<?> obtenerDetalle(@PathVariable Integer id) {
        logger.info("Obteniendo detalle completo de cuenta ID: {}", id);

        Cuenta cuenta = cuentaService.buscar(id);
        if (cuenta == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cuenta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Map<String, Object> detalle = new HashMap<>();
        detalle.put("cuentaId", cuenta.getId());
        detalle.put("cliente", cuenta.getCliente().getNombre());
        detalle.put("direccionFacturacion", cuenta.getDireccionFacturacion());
        detalle.put("estaCerrada", cuenta.getEstaCerrada());
        detalle.put("fechaAbierta", cuenta.getFechaAbierta());
        detalle.put("fechaCerrada", cuenta.getFechaCerrada());
        detalle.put("saldoPendiente", cuenta.getSaldoPendiente());
        detalle.put("cantidadPedidos", cuenta.getPedidos().size());
        detalle.put("cantidadPagos", cuenta.getPagos().size());
        detalle.put("pedidos", cuenta.getPedidos());
        detalle.put("pagos", cuenta.getPagos());

        return ResponseEntity.ok(detalle);
    }

    /**
     * GET /api/cuenta/estadisticas
     * Obtener estadísticas generales de cuentas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas de cuentas");

        List<Cuenta> todasCuentas = cuentaService.listar();
        List<Cuenta> cuentasActivas = cuentaService.buscarCuentasActivas();
        List<Cuenta> cuentasCerradas = cuentaService.buscarCuentasCerradas();
        List<Cuenta> cuentasConSaldo = cuentaService.buscarCuentasConSaldoPendiente(BigDecimal.ZERO);

        BigDecimal totalSaldoPendiente = todasCuentas.stream()
                .map(Cuenta::getSaldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalPedidos = todasCuentas.stream()
                .mapToInt(c -> c.getPedidos().size())
                .sum();

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalCuentas", todasCuentas.size());
        estadisticas.put("cuentasActivas", cuentasActivas.size());
        estadisticas.put("cuentasCerradas", cuentasCerradas.size());
        estadisticas.put("cuentasConSaldo", cuentasConSaldo.size());
        estadisticas.put("totalSaldoPendiente", totalSaldoPendiente);
        estadisticas.put("totalPedidos", totalPedidos);

        return ResponseEntity.ok(estadisticas);
    }
}