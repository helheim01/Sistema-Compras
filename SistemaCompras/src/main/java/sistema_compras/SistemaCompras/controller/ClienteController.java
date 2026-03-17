package sistema_compras.SistemaCompras.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.Cliente;
import sistema_compras.SistemaCompras.service.ClienteService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "*")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private ClienteService clienteService;

    // ==================== CRUD BÁSICO ====================

    /**
     * GET /api/cliente/listar
     * Listar todos los clientes
     */
    @GetMapping("/listar")
    public ResponseEntity<List<Cliente>> listar() {
        logger.info("Listando todos los clientes");
        List<Cliente> listarCliente = clienteService.listar();
        return ResponseEntity.ok(listarCliente);
    }

    /**
     * GET /api/cliente/buscar/{id}
     * Buscar cliente por ID
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        logger.info("Buscando cliente con ID: {}", id);
        Cliente cliente = clienteService.buscar(id);

        if (cliente == null) {
            logger.warn("Cliente no encontrado con ID: {}", id);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cliente no encontrado");
            error.put("id", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(cliente);
    }

    /**
     * POST /api/cliente/agregar
     * Agregar nuevo cliente
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Cliente cliente) {
        try {
            logger.info("Agregando nuevo cliente: {}", cliente.getEmail());
            Cliente clienteAgregado = clienteService.agregar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteAgregado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al agregar cliente: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/cliente/modificar
     * Modificar cliente existente
     */
    @PutMapping("/modificar")
    public ResponseEntity<?> modificar(@Valid @RequestBody Cliente cliente) {  // ✅ Corregido: @RequestBody
        try {
            logger.info("Modificando cliente con ID: {}", cliente.getId());
            Cliente clienteModificado = clienteService.modificar(cliente);
            return ResponseEntity.ok(clienteModificado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al modificar cliente: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/cliente/eliminar/{id}
     * Eliminar cliente
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            logger.info("Eliminando cliente con ID: {}", id);
            clienteService.eliminar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cliente eliminado exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al eliminar cliente: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ==================== FUNCIONALIDADES ADICIONALES ====================

    /**
     * GET /api/cliente/buscar-email/{email}
     * Buscar cliente por email
     */
    @GetMapping("/buscar-email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        logger.info("Buscando cliente por email: {}", email);
        Cliente cliente = clienteService.buscarPorEmail(email);

        if (cliente == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cliente no encontrado con ese email");
            error.put("email", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(cliente);
    }

    /**
     * GET /api/cliente/buscar-nombre/{nombre}
     * Buscar clientes por nombre (búsqueda parcial, case-insensitive)
     */
    @GetMapping("/buscar-nombre/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre) {
        logger.info("Buscando clientes por nombre: {}", nombre);
        List<Cliente> clientes = clienteService.buscarPorNombre(nombre);

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", clientes.size());
        response.put("clientes", clientes);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/cliente/buscar-telefono/{telefono}
     * Buscar cliente por teléfono
     */
    @GetMapping("/buscar-telefono/{telefono}")
    public ResponseEntity<?> buscarPorTelefono(@PathVariable String telefono) {
        logger.info("Buscando cliente por teléfono: {}", telefono);
        Cliente cliente = clienteService.buscarPorTelefono(telefono);

        if (cliente == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cliente no encontrado con ese teléfono");
            error.put("telefono", telefono);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(cliente);
    }

    /**
     * GET /api/cliente/usuario-web/{usuarioWebId}
     * Buscar cliente por ID de usuario web
     */
    @GetMapping("/usuario-web/{usuarioWebId}")
    public ResponseEntity<?> buscarPorUsuarioWebId(@PathVariable Integer usuarioWebId) {
        logger.info("Buscando cliente por usuario web ID: {}", usuarioWebId);
        Cliente cliente = clienteService.buscarPorUsuarioWebId(usuarioWebId);

        if (cliente == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cliente no encontrado para este usuario web");
            error.put("usuarioWebId", usuarioWebId.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(cliente);
    }

    /**
     * PUT /api/cliente/actualizar-datos/{id}
     * Actualizar datos básicos del cliente
     * Body: { "nombre": "Juan Pérez", "direccion": "Calle 123", "telefono": "123456789" }
     */
    @PutMapping("/actualizar-datos/{id}")
    public ResponseEntity<?> actualizarDatos(
            @PathVariable Integer id,
            @RequestBody Map<String, String> datos) {

        String nombre = datos.get("nombre");
        String direccion = datos.get("direccion");
        String telefono = datos.get("telefono");

        if (nombre == null || direccion == null || telefono == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Nombre, dirección y teléfono son obligatorios");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Actualizando datos para cliente ID: {}", id);
            Cliente cliente = clienteService.actualizarDatos(id, nombre, direccion, telefono);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Datos actualizados exitosamente");
            response.put("cliente", cliente);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al actualizar datos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/cliente/{id}/puntos
     * Obtener puntos disponibles del cliente
     */
    @GetMapping("/{id}/puntos")
    public ResponseEntity<?> obtenerPuntosDisponibles(@PathVariable Integer id) {
        logger.info("Obteniendo puntos para cliente ID: {}", id);

        try {
            Integer puntos = clienteService.obtenerPuntosDisponibles(id);

            Map<String, Object> response = new HashMap<>();
            response.put("clienteId", id);
            response.put("puntosDisponibles", puntos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener puntos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "No se pudieron obtener los puntos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/cliente/puntos-mayores-a/{cantidad}
     * Buscar clientes con puntos mayores a cierta cantidad
     */
    @GetMapping("/puntos-mayores-a/{cantidad}")
    public ResponseEntity<?> buscarClientesConPuntosMayoresA(@PathVariable Integer cantidad) {
        logger.info("Buscando clientes con más de {} puntos", cantidad);

        try {
            List<Cliente> clientes = clienteService.buscarClientesConPuntosMayoresA(cantidad);

            Map<String, Object> response = new HashMap<>();
            response.put("cantidad", clientes.size());
            response.put("puntosMinimosBuscados", cantidad);
            response.put("clientes", clientes);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al buscar clientes: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al buscar clientes con puntos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/cliente/{id}/historial-pedidos
     * Obtener historial de pedidos de un cliente
     */
    @GetMapping("/{id}/historial-pedidos")
    public ResponseEntity<?> obtenerHistorialPedidos(@PathVariable Integer id) {
        logger.info("Obteniendo historial de pedidos para cliente ID: {}", id);

        Cliente cliente = clienteService.buscar(id);
        if (cliente == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cliente no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("clienteId", id);
        response.put("clienteNombre", cliente.getNombre());
        response.put("cantidadPedidos", cliente.getPedidos().size());
        response.put("pedidos", cliente.getPedidos());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/cliente/{id}/tarjetas
     * Obtener tarjetas registradas de un cliente
     */
    @GetMapping("/{id}/tarjetas")
    public ResponseEntity<?> obtenerTarjetas(@PathVariable Integer id) {
        logger.info("Obteniendo tarjetas para cliente ID: {}", id);

        Cliente cliente = clienteService.buscar(id);
        if (cliente == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cliente no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("clienteId", id);
        response.put("cantidadTarjetas", cliente.getTarjetas().size());
        response.put("tarjetas", cliente.getTarjetas());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/cliente/estadisticas
     * Obtener estadísticas generales de clientes
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas de clientes");

        List<Cliente> todosClientes = clienteService.listar();

        long clientesConPedidos = todosClientes.stream()
                .filter(c -> c.getPedidos() != null && !c.getPedidos().isEmpty())
                .count();

        long clientesConPuntos = todosClientes.stream()
                .filter(c -> c.getPuntosRecompensa() != null &&
                        c.getPuntosRecompensa().getPuntosDisponibles() > 0)
                .count();

        long clientesConTarjetas = todosClientes.stream()
                .filter(c -> c.getTarjetas() != null && !c.getTarjetas().isEmpty())
                .count();

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalClientes", todosClientes.size());
        estadisticas.put("clientesConPedidos", clientesConPedidos);
        estadisticas.put("clientesConPuntos", clientesConPuntos);
        estadisticas.put("clientesConTarjetas", clientesConTarjetas);

        return ResponseEntity.ok(estadisticas);
    }
}