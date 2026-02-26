package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.Admin;
import sistema_compras.SistemaCompras.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService implements ICrud<Admin> {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public Admin agregar(Admin admin) {
        if (admin.getNombre() == null || admin.getEmail() == null || admin.getContrasena() == null) {
            throw new IllegalArgumentException("Nombre, email y contraseña son obligatorios.");
        }

        // Validar que el email no exista
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException("Ya existe un admin con ese email.");
        }

        // Encriptar contraseña
        admin.setContrasena(passwordEncoder.encode(admin.getContrasena()));

        // Rol por defecto
        if (admin.getRol() == null) {
            admin.setRol("ADMIN");
        }

        Admin guardado = adminRepository.save(admin);
        logger.info("Admin agregado con éxito: {}", guardado.getEmail());
        return guardado;
    }

    // ------------------ LOGIN ------------------
    public Optional<Admin> login(String email, String contrasena) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(contrasena, admin.getContrasena())) {
                logger.info("Login exitoso para admin: {}", email);
                return Optional.of(admin);
            }
        }

        logger.warn("Intento de login fallido para email: {}", email);
        return Optional.empty();
    }

    // ------------------ BUSCAR POR EMAIL ------------------
    public Optional<Admin> buscarPorEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    // ------------------ CAMBIAR CONTRASEÑA ------------------
    @Transactional
    public void cambiarContrasena(Integer id, String antiguaContrasena, String nuevaContrasena) {
        Admin admin = buscar(id);
        if (admin == null) {
            throw new IllegalArgumentException("Admin no encontrado.");
        }

        if (!passwordEncoder.matches(antiguaContrasena, admin.getContrasena())) {
            throw new IllegalArgumentException("La contraseña antigua no es correcta.");
        }

        admin.setContrasena(passwordEncoder.encode(nuevaContrasena));
        adminRepository.save(admin);
        logger.info("Contraseña cambiada para admin: {}", admin.getEmail());
    }

    // ------------------ CAMBIAR ROL ------------------
    @Transactional
    public void cambiarRol(Integer id, String nuevoRol) {
        Admin admin = buscar(id);
        if (admin == null) {
            throw new IllegalArgumentException("Admin no encontrado.");
        }

        if (!nuevoRol.equals("SUPER_ADMIN") && !nuevoRol.equals("ADMIN") && !nuevoRol.equals("MODERADOR")) {
            throw new IllegalArgumentException("Rol inválido. Debe ser: SUPER_ADMIN, ADMIN o MODERADOR");
        }

        admin.setRol(nuevoRol);
        adminRepository.save(admin);
        logger.info("Rol actualizado para admin {}: {}", admin.getEmail(), nuevoRol);
    }

    // ------------------ LISTAR POR ROL ------------------
    public List<Admin> listarPorRol(String rol) {
        return adminRepository.findByRol(rol);
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public Admin modificar(Admin admin) {
        if (admin.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar el admin.");
        }

        Admin existente = adminRepository.findById(admin.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe un admin con ID " + admin.getId()));

        existente.setNombre(admin.getNombre());
        existente.setEmail(admin.getEmail());
        existente.setRol(admin.getRol());

        Admin actualizado = adminRepository.save(existente);
        logger.info("Admin modificado con éxito: {}", actualizado.getEmail());
        return actualizado;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public Admin buscar(Integer id) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) {
            logger.warn("No se encontró admin con ID: {}", id);
        }
        return admin;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!adminRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un admin con ID " + id);
        }
        adminRepository.deleteById(id);
        logger.info("Admin eliminado con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<Admin> listar() {
        List<Admin> admins = adminRepository.findAll();
        logger.info("Se listaron {} admins.", admins.size());
        return admins;
    }
}