package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.Cliente;
import sistema_compras.SistemaCompras.entity.PuntosRecompensa;
import sistema_compras.SistemaCompras.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService implements ICrud<Cliente> {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PuntosRecompensaService puntosRecompensaService;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public Cliente agregar(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getEmail() == null) {
            throw new IllegalArgumentException("Nombre y email son obligatorios.");
        }

        // Validar email único
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese email.");
        }

        // Crear sistema de puntos automáticamente
        Cliente guardado = clienteRepository.save(cliente);

        // Inicializar puntos de recompensa
        PuntosRecompensa puntos = new PuntosRecompensa();
        puntos.setCliente(guardado);
        puntos.setPuntosDisponibles(0);
        puntosRecompensaService.agregar(puntos);

        logger.info("Cliente agregado con éxito: {}", guardado.getNombre());
        return guardado;
    }

    // ------------------ BUSCAR POR EMAIL ------------------
    public Cliente buscarPorEmail(String email) {
        logger.info("Buscando cliente por email: {}", email);
        return clienteRepository.findByEmail(email).orElse(null);
    }

    // ------------------ ACTUALIZAR DATOS ------------------
    @Transactional
    public Cliente actualizarDatos(Integer id, String nombre, String direccion, String telefono) {
        Cliente cliente = buscar(id);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }

        cliente.setNombre(nombre);
        cliente.setDireccion(direccion);
        cliente.setTelefono(telefono);

        Cliente actualizado = clienteRepository.save(cliente);
        logger.info("Datos actualizados para cliente: {}", actualizado.getNombre());
        return actualizado;
    }

    // ------------------ OBTENER PUNTOS DISPONIBLES ------------------
    public Integer obtenerPuntosDisponibles(Integer clienteId) {
        Cliente cliente = buscar(clienteId);
        if (cliente == null || cliente.getPuntosRecompensa() == null) {
            return 0;
        }
        return cliente.getPuntosRecompensa().getPuntosDisponibles();
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public Cliente modificar(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar el cliente.");
        }

        Cliente existente = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe un cliente con ID " + cliente.getId()));

        existente.setNombre(cliente.getNombre());
        existente.setDireccion(cliente.getDireccion());
        existente.setEmail(cliente.getEmail());
        existente.setTelefono(cliente.getTelefono());

        Cliente actualizado = clienteRepository.save(existente);
        logger.info("Cliente modificado con éxito: {}", actualizado.getNombre());
        return actualizado;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public Cliente buscar(Integer id) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            logger.warn("No se encontró cliente con ID: {}", id);
        }
        return cliente;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un cliente con ID " + id);
        }
        clienteRepository.deleteById(id);
        logger.info("Cliente eliminado con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<Cliente> listar() {
        List<Cliente> clientes = clienteRepository.findAll();
        logger.info("Se listaron {} clientes.", clientes.size());
        return clientes;
    }

    // ------------------ BUSCAR POR NOMBRE ------------------
    public List<Cliente> buscarPorNombre(String nombre) {
        logger.info("Buscando clientes por nombre: {}", nombre);
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // ------------------ BUSCAR POR TELÉFONO ------------------
    public Cliente buscarPorTelefono(String telefono) {
        logger.info("Buscando cliente por teléfono: {}", telefono);
        return clienteRepository.findByTelefono(telefono).orElse(null);
    }

    // ------------------ BUSCAR CLIENTES CON PUNTOS MAYORES A ------------------
    public List<Cliente> buscarClientesConPuntosMayoresA(Integer puntos) {
        logger.info("Buscando clientes con más de {} puntos", puntos);
        return clienteRepository.findClientesConPuntosMayoresA(puntos);
    }

    // ------------------ BUSCAR POR USUARIO WEB ID ------------------
    public Cliente buscarPorUsuarioWebId(Integer usuarioWebId) {
        logger.info("Buscando cliente por usuario web ID: {}", usuarioWebId);
        return clienteRepository.findByUsuarioWebId(usuarioWebId).orElse(null);
    }
}