package sistema_compras.SistemaCompras.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.PuntosRecompensa;
import sistema_compras.SistemaCompras.service.PuntosRecompensaService;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/puntos-recompensa")
@CrossOrigin(origins = "*")
public class PuntosRecompensaController {

    private static final Logger logger = LoggerFactory.getLogger(PuntosRecompensaController.class);

    @Autowired
    private PuntosRecompensaService puntosRecompensaService;

    // ==================== CRUD BÁSICO ====================

    /**
     * GET /api/puntos-recompensa/listar
     * Listar todos los sistemas de puntos
     */
    @GetMapping("/listar")
    public ResponseEntity<List<PuntosRecompensa>> listar() {
        logger.info("Listando todos los sistemas de puntos de recompensa");
        List<PuntosRecompensa> listarPuntosRecompensa = puntosRecompensaService.listar();
        return ResponseEntity.ok(listarPuntosRecompensa);
    }

    /**
     * GET /api/puntos-recompensa/buscar/{id}
     * Buscar sistema de puntos por ID
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        logger.info("Buscando puntos de recompensa con ID: {}", id);
        PuntosRecompensa puntosRecompensa = puntosRecompensaService.buscar(id);

        if (puntosRecompensa == null) {
            logger.warn("Puntos de recompensa no encontrados con ID: {}", id);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Sistema de puntos no encontrado");
            error.put("id", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(puntosRecompensa);
    }

    /**
     * POST /api/puntos-recompensa/agregar
     * Crear nuevo sistema de puntos
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody PuntosRecompensa puntosRecompensa) {
        try {
            logger.info("Agregando nuevo sistema de puntos para cliente ID: {}",
                    puntosRecompensa.getCliente() != null ? puntosRecompensa.getCliente().getId() : "null");
            PuntosRecompensa puntosRecompensaAgregado = puntosRecompensaService.agregar(puntosRecompensa);
            return ResponseEntity.status(HttpStatus.CREATED).body(puntosRecompensaAgregado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al agregar puntos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/puntos-recompensa/modificar
     * Modificar sistema de puntos existente
     */
    @PutMapping("/modificar")
    public ResponseEntity<?> modificar(@Valid @RequestBody PuntosRecompensa puntosRecompensa) {
        try {
            logger.info("Modificando puntos de recompensa con ID: {}", puntosRecompensa.getId());
            PuntosRecompensa puntosRecompensaModificado = puntosRecompensaService.modificar(puntosRecompensa);
            return ResponseEntity.ok(puntosRecompensaModificado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al modificar puntos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/puntos-recompensa/eliminar/{id}
     * Eliminar sistema de puntos
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            logger.info("Eliminando puntos de recompensa con ID: {}", id);
            puntosRecompensaService.eliminar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Sistema de puntos eliminado exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al eliminar puntos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ==================== GESTIÓN DE PUNTOS ====================

    /**
     * PUT /api/puntos-recompensa/{id}/agregar-puntos
     * Agregar puntos a un cliente
     * Body: { "cantidad": 100 }
     */
    @PutMapping("/{id}/agregar-puntos")
    public ResponseEntity<?> agregarPuntos(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> data) {

        Integer cantidad = data.get("cantidad");

        if (cantidad == null || cantidad <= 0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La cantidad debe ser un número positivo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Agregando {} puntos al sistema ID: {}", cantidad, id);
            puntosRecompensaService.agregarPuntos(id, cantidad);

            PuntosRecompensa puntos = puntosRecompensaService.buscar(id);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Puntos agregados exitosamente");
            response.put("cantidadAgregada", cantidad);
            response.put("nuevoTotal", puntos.getPuntosDisponibles());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al agregar puntos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/puntos-recompensa/{id}/canjear-puntos
     * Canjear puntos de un cliente
     * Body: { "cantidad": 50 }
     */
    @PutMapping("/{id}/canjear-puntos")
    public ResponseEntity<?> canjearPuntos(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> data) {

        Integer cantidad = data.get("cantidad");

        if (cantidad == null || cantidad <= 0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La cantidad debe ser un número positivo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Canjeando {} puntos del sistema ID: {}", cantidad, id);
            puntosRecompensaService.canjearPuntos(id, cantidad);

            PuntosRecompensa puntos = puntosRecompensaService.buscar(id);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Puntos canjeados exitosamente");
            response.put("cantidadCanjeada", cantidad);
            response.put("puntosRestantes", puntos.getPuntosDisponibles());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al canjear puntos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * POST /api/puntos-recompensa/{id}/aplicar-cupon
     * Aplicar cupón de puntos
     * Body: { "codigoCupon": "VERANO2025" }
     */
    @PostMapping("/{id}/aplicar-cupon")
    public ResponseEntity<?> aplicarCupon(
            @PathVariable Integer id,
            @RequestBody Map<String, String> data) {

        String codigoCupon = data.get("codigoCupon");

        if (codigoCupon == null || codigoCupon.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El código de cupón es obligatorio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Aplicando cupón {} al sistema ID: {}", codigoCupon, id);
            boolean aplicado = puntosRecompensaService.aplicarCupon(id, codigoCupon);

            if (aplicado) {
                PuntosRecompensa puntos = puntosRecompensaService.buscar(id);

                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "Cupón aplicado exitosamente");
                response.put("codigoCupon", codigoCupon);
                response.put("puntosActuales", puntos.getPuntosDisponibles());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Cupón inválido o ya usado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error al aplicar cupón: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/puntos-recompensa/{id}/equivalente-dinero
     * Calcular equivalente en dinero de los puntos
     */
    @GetMapping("/{id}/equivalente-dinero")
    public ResponseEntity<?> calcularEquivalenteDinero(@PathVariable Integer id) {
        logger.info("Calculando equivalente en dinero para puntos ID: {}", id);

        PuntosRecompensa puntos = puntosRecompensaService.buscar(id);
        if (puntos == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Sistema de puntos no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        BigDecimal equivalente = puntosRecompensaService.calcularEquivalenteDinero(id);

        Map<String, Object> response = new HashMap<>();
        response.put("puntosDisponibles", puntos.getPuntosDisponibles());
        response.put("tasaConversion", puntos.getTasaConversion());
        response.put("equivalenteDinero", equivalente);
        response.put("moneda", "ARS");

        return ResponseEntity.ok(response);
    }

    // ==================== BÚSQUEDAS ESPECÍFICAS ====================

    /**
     * GET /api/puntos-recompensa/cliente/{clienteId}
     * Buscar puntos de un cliente específico
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> buscarPorCliente(@PathVariable Integer clienteId) {
        logger.info("Buscando puntos de cliente ID: {}", clienteId);
        PuntosRecompensa puntos = puntosRecompensaService.buscarPorCliente(clienteId);

        if (puntos == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No se encontraron puntos para este cliente");
            error.put("clienteId", clienteId.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(puntos);
    }

    /**
     * GET /api/puntos-recompensa/cupon/{codigoCupon}
     * Buscar por código de cupón
     */
    @GetMapping("/cupon/{codigoCupon}")
    public ResponseEntity<?> buscarPorCodigoCupon(@PathVariable String codigoCupon) {
        logger.info("Buscando puntos por código de cupón: {}", codigoCupon);
        PuntosRecompensa puntos = puntosRecompensaService.buscarPorCodigoCupon(codigoCupon);

        if (puntos == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No se encontró ningún sistema de puntos con ese cupón");
            error.put("codigoCupon", codigoCupon);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(puntos);
    }

    /**
     * GET /api/puntos-recompensa/mayores-a/{cantidad}
     * Buscar clientes con puntos mayores a cierta cantidad
     */
    @GetMapping("/mayores-a/{cantidad}")
    public ResponseEntity<?> buscarConPuntosMayoresA(@PathVariable Integer cantidad) {
        logger.info("Buscando clientes con más de {} puntos", cantidad);
        List<PuntosRecompensa> puntosLista = puntosRecompensaService.buscarConPuntosMayoresA(cantidad);

        Map<String, Object> response = new HashMap<>();
        response.put("puntosMinimos", cantidad);
        response.put("cantidad", puntosLista.size());
        response.put("clientes", puntosLista);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/puntos-recompensa/sin-puntos
     * Buscar clientes sin puntos
     */
    @GetMapping("/sin-puntos")
    public ResponseEntity<?> buscarClientesSinPuntos() {
        logger.info("Buscando clientes sin puntos");
        List<PuntosRecompensa> puntosLista = puntosRecompensaService.buscarClientesSinPuntos();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", puntosLista.size());
        response.put("clientes", puntosLista);

        return ResponseEntity.ok(response);
    }

    // ==================== RANKINGS Y ESTADÍSTICAS ====================

    /**
     * GET /api/puntos-recompensa/ranking
     * Top 10 clientes con más puntos
     */
    @GetMapping("/ranking")
    public ResponseEntity<?> obtenerRankingClientes() {
        logger.info("Obteniendo ranking de clientes con más puntos");
        List<PuntosRecompensa> top10 = puntosRecompensaService.obtenerTop10ClientesConMasPuntos();

        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", top10.size());
        response.put("ranking", top10);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/puntos-recompensa/estadisticas
     * Obtener estadísticas generales del sistema de puntos
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas generales de puntos de recompensa");

        List<PuntosRecompensa> todos = puntosRecompensaService.listar();
        Long totalPuntosSistema = puntosRecompensaService.calcularTotalPuntosEnSistema();
        List<PuntosRecompensa> sinPuntos = puntosRecompensaService.buscarClientesSinPuntos();
        List<PuntosRecompensa> conPuntos = puntosRecompensaService.buscarConPuntosMayoresA(0);

        // Calcular promedio de puntos
        double promedioPuntos = todos.stream()
                .mapToInt(PuntosRecompensa::getPuntosDisponibles)
                .average()
                .orElse(0.0);

        // Calcular equivalente total en dinero
        BigDecimal equivalenteTotalDinero = todos.stream()
                .map(p -> BigDecimal.valueOf(p.getPuntosDisponibles()).multiply(p.getTasaConversion()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalClientes", todos.size());
        estadisticas.put("clientesConPuntos", conPuntos.size());
        estadisticas.put("clientesSinPuntos", sinPuntos.size());
        estadisticas.put("totalPuntosEnSistema", totalPuntosSistema);
        estadisticas.put("promedioPuntosPorCliente", Math.round(promedioPuntos));
        estadisticas.put("equivalenteTotalDinero", equivalenteTotalDinero);

        return ResponseEntity.ok(estadisticas);
    }

    /**
     * GET /api/puntos-recompensa/{id}/detalle
     * Obtener detalle completo de un sistema de puntos
     */
    @GetMapping("/{id}/detalle")
    public ResponseEntity<?> obtenerDetalle(@PathVariable Integer id) {
        logger.info("Obteniendo detalle completo de puntos ID: {}", id);

        PuntosRecompensa puntos = puntosRecompensaService.buscar(id);
        if (puntos == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Sistema de puntos no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        BigDecimal equivalenteDinero = puntosRecompensaService.calcularEquivalenteDinero(id);

        Map<String, Object> detalle = new HashMap<>();
        detalle.put("id", puntos.getId());
        detalle.put("cliente", puntos.getCliente().getNombre());
        detalle.put("clienteId", puntos.getCliente().getId());
        detalle.put("puntosDisponibles", puntos.getPuntosDisponibles());
        detalle.put("tasaConversion", puntos.getTasaConversion());
        detalle.put("equivalenteDinero", equivalenteDinero);
        detalle.put("codigoCupon", puntos.getCodigoCupon());

        return ResponseEntity.ok(detalle);
    }
}