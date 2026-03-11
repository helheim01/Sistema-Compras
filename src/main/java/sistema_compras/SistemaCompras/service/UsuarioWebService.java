package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.EstadoUsuario;
import sistema_compras.SistemaCompras.entity.UsuarioWeb;
import sistema_compras.SistemaCompras.repository.UsuarioWebRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioWebService implements ICrud<UsuarioWeb> {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioWebService.class);

    @Autowired
    private UsuarioWebRepository usuarioWebRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ------------------ HELPER: construir respuesta sin contraseña ------------------
    private UsuarioWeb sinContrasena(UsuarioWeb u) {
        UsuarioWeb respuesta = new UsuarioWeb();
        respuesta.setId(u.getId());
        respuesta.setEmail(u.getEmail());
        respuesta.setEstado(u.getEstado());
        respuesta.setFechaRegistro(u.getFechaRegistro());
        respuesta.setUltimoAcceso(u.getUltimoAcceso());
        return respuesta;
    }

    // ------------------ AGREGAR (REGISTRO) ------------------
    @Transactional
    @Override
    public UsuarioWeb agregar(UsuarioWeb usuarioWeb) {
        if (usuarioWeb.getEmail() == null || usuarioWeb.getContrasena() == null) {
            throw new IllegalArgumentException("Email y contraseña son obligatorios.");
        }

        if (usuarioWebRepository.existsByEmail(usuarioWeb.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        usuarioWeb.setContrasena(passwordEncoder.encode(usuarioWeb.getContrasena()));
        usuarioWeb.setEstado(EstadoUsuario.NUEVO);
        usuarioWeb.setFechaRegistro(LocalDateTime.now());
        usuarioWeb.setUltimoAcceso(LocalDateTime.now());

        UsuarioWeb guardado = usuarioWebRepository.save(usuarioWeb);
        logger.info("Usuario registrado con éxito: {}", guardado.getEmail());
        return sinContrasena(guardado);
    }

    // ------------------ BUSCAR POR EMAIL ------------------
    public Optional<UsuarioWeb> buscarPorEmail(String email) {
        logger.info("Buscando usuario por email: {}", email);
        return usuarioWebRepository.findByEmail(email)
                .map(this::sinContrasena);
    }

    // ------------------ BUSCAR POR ESTADO ------------------
    public List<UsuarioWeb> buscarPorEstado(EstadoUsuario estado) {
        return usuarioWebRepository.findByEstado(estado)
                .stream().map(this::sinContrasena).toList();
    }

    // ------------------ BUSCAR EXCLUYENDO ESTADO ------------------
    public List<UsuarioWeb> buscarExcluyendoEstado(EstadoUsuario estado) {
        return usuarioWebRepository.findByEstadoNot(estado)
                .stream().map(this::sinContrasena).toList();
    }

    // ------------------ ACTUALIZAR ÚLTIMO ACCESO ------------------
    @Transactional
    public void actualizarUltimoAcceso(Integer id) {
        UsuarioWeb usuario = usuarioWebRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ID " + id));
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioWebRepository.save(usuario);
        logger.info("Último acceso actualizado para usuario: {}", usuario.getEmail());
    }

    // ------------------ CAMBIAR CONTRASEÑA ------------------
    @Transactional
    public void cambiarContrasena(Integer id, String antiguaContrasena, String nuevaContrasena) {
        UsuarioWeb usuario = usuarioWebRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ID " + id));

        if (!passwordEncoder.matches(antiguaContrasena, usuario.getContrasena())) {
            throw new IllegalArgumentException("La contraseña antigua no es correcta.");
        }

        if (passwordEncoder.matches(nuevaContrasena, usuario.getContrasena())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior.");
        }

        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioWebRepository.save(usuario);
        logger.info("Contraseña cambiada para usuario: {}", usuario.getEmail());
    }

    // ------------------ ACTIVAR CUENTA ------------------
    @Transactional
    public void activarCuenta(Integer id) {
        UsuarioWeb usuario = usuarioWebRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ID " + id));
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuarioWebRepository.save(usuario);
        logger.info("Cuenta activada: {}", usuario.getEmail());
    }

    // ------------------ BLOQUEAR CUENTA ------------------
    @Transactional
    public void bloquearCuenta(Integer id) {
        UsuarioWeb usuario = usuarioWebRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ID " + id));
        usuario.setEstado(EstadoUsuario.BLOQUEADO);
        usuarioWebRepository.save(usuario);
        logger.warn("Cuenta bloqueada: {}", usuario.getEmail());
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public UsuarioWeb modificar(UsuarioWeb usuarioWeb) {
        if (usuarioWeb.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar el usuario.");
        }

        UsuarioWeb existente = usuarioWebRepository.findById(usuarioWeb.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ID " + usuarioWeb.getId()));

        existente.setEmail(usuarioWeb.getEmail());
        existente.setEstado(usuarioWeb.getEstado());

        UsuarioWeb actualizado = usuarioWebRepository.save(existente);
        logger.info("Usuario modificado con éxito: {}", actualizado.getEmail());
        return sinContrasena(actualizado);
    }

    // ------------------ BUSCAR ------------------
    @Override
    public UsuarioWeb buscar(Integer id) {
        UsuarioWeb usuario = usuarioWebRepository.findById(id).orElse(null);
        if (usuario == null) {
            logger.warn("No se encontró usuario con ID: {}", id);
            return null;
        }
        return sinContrasena(usuario);
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!usuarioWebRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un usuario con ID " + id);
        }
        usuarioWebRepository.deleteById(id);
        logger.info("Usuario eliminado con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<UsuarioWeb> listar() {
        List<UsuarioWeb> usuarios = usuarioWebRepository.findAll();
        logger.info("Se listaron {} usuarios.", usuarios.size());
        return usuarios.stream().map(this::sinContrasena).toList();
    }
}