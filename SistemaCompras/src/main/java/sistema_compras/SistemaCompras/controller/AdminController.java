package sistema_compras.SistemaCompras.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.Admin;
import sistema_compras.SistemaCompras.service.AdminService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    // ==================== CRUD BÁSICO ====================

    /**
     * GET /api/admin/listar
     * Listar todos los administradores
     */
    @GetMapping("/listar")
    public ResponseEntity<List<Admin>> listar() {
        logger.info("Listando todos los administradores");
        List<Admin> listarAdmin = adminService.listar();
        return ResponseEntity.ok(listarAdmin);
    }

    /**
     * GET /api/admin/buscar/{id}
     * Buscar administrador por ID
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        logger.info("Buscando admin con ID: {}", id);
        Admin admin = adminService.buscar(id);

        if (admin == null) {
            logger.warn("Admin no encontrado con ID: {}", id);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Admin no encontrado");
            error.put("id", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(admin);
    }

    /**
     * POST /api/admin/agregar
     * Agregar nuevo administrador
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Admin admin) {
        try {
            logger.info("Agregando nuevo admin: {}", admin.getEmail());
            Admin adminAgregado = adminService.agregar(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(adminAgregado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al agregar admin: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/admin/modificar
     * Modificar administrador existente
     */
    @PutMapping("/modificar")
    public ResponseEntity<?> modificar(@Valid @RequestBody Admin admin) {
        try {
            logger.info("Modificando admin con ID: {}", admin.getId());
            Admin adminModificado = adminService.modificar(admin);
            return ResponseEntity.ok(adminModificado);
        } catch (IllegalArgumentException e) {
            logger.error("Error al modificar admin: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/admin/eliminar/{id}
     * Eliminar administrador
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            logger.info("Eliminando admin con ID: {}", id);
            adminService.eliminar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Admin eliminado exitosamente");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al eliminar admin: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ==================== FUNCIONALIDADES ADICIONALES ====================

    /**
     * POST /api/admin/login
     * Login de administrador
     * Body: { "email": "admin@example.com", "contrasena": "password123" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String contrasena = credentials.get("contrasena");

        if (email == null || contrasena == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email y contraseña son obligatorios");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        logger.info("Intento de login para email: {}", email);
        Optional<Admin> adminOpt = adminService.login(email, contrasena);

        if (adminOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Login exitoso");
            response.put("admin", adminOpt.get());
            // En producción, aquí generarías un JWT token
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * GET /api/admin/buscar-email/{email}
     * Buscar administrador por email
     */
    @GetMapping("/buscar-email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        logger.info("Buscando admin por email: {}", email);
        Optional<Admin> adminOpt = adminService.buscarPorEmail(email);

        if (adminOpt.isPresent()) {
            return ResponseEntity.ok(adminOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Admin no encontrado con ese email");
            error.put("email", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * PUT /api/admin/cambiar-contrasena/{id}
     * Cambiar contraseña de administrador
     * Body: { "antiguaContrasena": "old123", "nuevaContrasena": "new123" }
     */
    @PutMapping("/cambiar-contrasena/{id}")
    public ResponseEntity<?> cambiarContrasena(
            @PathVariable Integer id,
            @RequestBody Map<String, String> passwords) {

        String antiguaContrasena = passwords.get("antiguaContrasena");
        String nuevaContrasena = passwords.get("nuevaContrasena");

        if (antiguaContrasena == null || nuevaContrasena == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Antigua y nueva contraseña son obligatorias");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Cambiando contraseña para admin ID: {}", id);
            adminService.cambiarContrasena(id, antiguaContrasena, nuevaContrasena);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Contraseña cambiada exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al cambiar contraseña: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * PUT /api/admin/cambiar-rol/{id}
     * Cambiar rol de administrador
     * Body: { "nuevoRol": "SUPER_ADMIN" }
     */
    @PutMapping("/cambiar-rol/{id}")
    public ResponseEntity<?> cambiarRol(
            @PathVariable Integer id,
            @RequestBody Map<String, String> data) {

        String nuevoRol = data.get("nuevoRol");

        if (nuevoRol == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El rol es obligatorio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            logger.info("Cambiando rol para admin ID: {} a {}", id, nuevoRol);
            adminService.cambiarRol(id, nuevoRol);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Rol actualizado exitosamente");
            response.put("nuevoRol", nuevoRol);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al cambiar rol: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/admin/listar-por-rol/{rol}
     * Listar administradores por rol
     * Roles válidos: SUPER_ADMIN, ADMIN, MODERADOR
     */
    @GetMapping("/listar-por-rol/{rol}")
    public ResponseEntity<?> listarPorRol(@PathVariable String rol) {
        logger.info("Listando admins con rol: {}", rol);

        // Validar rol
        if (!rol.equals("SUPER_ADMIN") && !rol.equals("ADMIN") && !rol.equals("MODERADOR")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Rol inválido. Debe ser: SUPER_ADMIN, ADMIN o MODERADOR");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        List<Admin> admins = adminService.listarPorRol(rol);

        Map<String, Object> response = new HashMap<>();
        response.put("rol", rol);
        response.put("cantidad", admins.size());
        response.put("admins", admins);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/buscar-nombre/{nombre}
     * Buscar administradores por nombre (búsqueda parcial, case-insensitive)
     */
    @GetMapping("/buscar-nombre/{nombre}")
    public ResponseEntity<List<Admin>> buscarPorNombre(@PathVariable String nombre) {
        logger.info("Buscando admins por nombre: {}", nombre);
        List<Admin> admins = adminService.buscarPorNombre(nombre);
        return ResponseEntity.ok(admins);
    }

    /**
     * GET /api/admin/estadisticas
     * Obtener estadísticas de administradores
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas de administradores");

        List<Admin> todosAdmins = adminService.listar();
        long totalSuperAdmins = todosAdmins.stream()
                .filter(a -> "SUPER_ADMIN".equals(a.getRol()))
                .count();
        long totalAdmins = todosAdmins.stream()
                .filter(a -> "ADMIN".equals(a.getRol()))
                .count();
        long totalModeradores = todosAdmins.stream()
                .filter(a -> "MODERADOR".equals(a.getRol()))
                .count();

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", todosAdmins.size());
        estadisticas.put("superAdmins", totalSuperAdmins);
        estadisticas.put("admins", totalAdmins);
        estadisticas.put("moderadores", totalModeradores);

        return ResponseEntity.ok(estadisticas);
    }
}