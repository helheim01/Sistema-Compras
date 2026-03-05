package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.PuntosRecompensa;
import sistema_compras.SistemaCompras.repository.PuntosRecompensaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PuntosRecompensaService implements ICrud<PuntosRecompensa> {

    private static final Logger logger = LoggerFactory.getLogger(PuntosRecompensaService.class);

    @Autowired
    private PuntosRecompensaRepository puntosRepository;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public PuntosRecompensa agregar(PuntosRecompensa puntos) {
        if (puntos.getCliente() == null) {
            throw new IllegalArgumentException("Los puntos deben estar asociados a un cliente.");
        }

        if (puntos.getPuntosDisponibles() == null) {
            puntos.setPuntosDisponibles(0);
        }
        if (puntos.getTasaConversion() == null) {
            puntos.setTasaConversion(BigDecimal.valueOf(0.01)); // 1 punto = $0.01 por defecto
        }

        PuntosRecompensa guardados = puntosRepository.save(puntos);
        logger.info("Sistema de puntos creado para cliente ID: {}", puntos.getCliente().getId());
        return guardados;
    }

    // ------------------ AGREGAR PUNTOS ------------------
    @Transactional
    public void agregarPuntos(Integer id, Integer cantidad) {
        PuntosRecompensa puntos = buscar(id);
        if (puntos == null) {
            throw new IllegalArgumentException("Sistema de puntos no encontrado.");
        }

        int nuevaCantidad = puntos.getPuntosDisponibles() + cantidad;
        puntos.setPuntosDisponibles(nuevaCantidad);

        puntosRepository.save(puntos);
        logger.info("Puntos agregados. Cliente: {}, Cantidad: {}, Total: {}",
                puntos.getCliente().getId(), cantidad, nuevaCantidad);
    }

    // ------------------ CANJEAR PUNTOS ------------------
    @Transactional
    public void canjearPuntos(Integer id, Integer cantidad) {
        PuntosRecompensa puntos = buscar(id);
        if (puntos == null) {
            throw new IllegalArgumentException("Sistema de puntos no encontrado.");
        }

        if (puntos.getPuntosDisponibles() < cantidad) {
            throw new IllegalArgumentException("Puntos insuficientes. Disponibles: " +
                    puntos.getPuntosDisponibles() + ", Requeridos: " + cantidad);
        }

        int nuevaCantidad = puntos.getPuntosDisponibles() - cantidad;
        puntos.setPuntosDisponibles(nuevaCantidad);

        puntosRepository.save(puntos);
        logger.info("Puntos canjeados. Cliente: {}, Cantidad: {}, Restantes: {}",
                puntos.getCliente().getId(), cantidad, nuevaCantidad);
    }

    // ------------------ APLICAR CUPÓN ------------------
    @Transactional
    public boolean aplicarCupon(Integer id, String codigoCupon) {
        PuntosRecompensa puntos = buscar(id);
        if (puntos == null) {
            throw new IllegalArgumentException("Sistema de puntos no encontrado.");
        }

        // Aquí iría la validación del cupón contra una tabla de cupones
        // Por ahora simulamos la aplicación

        if (validarCupon(codigoCupon)) {
            puntos.setCodigoCupon(codigoCupon);
            // Agregar puntos bonus por el cupón
            agregarPuntos(id, 100); // Ejemplo: 100 puntos bonus

            puntosRepository.save(puntos);
            logger.info("Cupón aplicado: {} para cliente ID: {}", codigoCupon, puntos.getCliente().getId());
            return true;
        }

        logger.warn("Cupón inválido: {}", codigoCupon);
        return false;
    }

    // ------------------ VALIDAR CUPÓN ------------------
    private boolean validarCupon(String codigoCupon) {
        // Aquí iría la lógica de validación contra BD
        // Por ahora simulamos
        return codigoCupon != null && codigoCupon.length() >= 6;
    }

    // ------------------ CALCULAR EQUIVALENTE EN DINERO ------------------
    public BigDecimal calcularEquivalenteDinero(Integer id) {
        PuntosRecompensa puntos = buscar(id);
        if (puntos == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(puntos.getPuntosDisponibles())
                .multiply(puntos.getTasaConversion());
    }

    // ------------------ BUSCAR POR CLIENTE ------------------
    public PuntosRecompensa buscarPorCliente(Integer clienteId) {
        return puntosRepository.findByClienteId(clienteId).orElse(null);
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public PuntosRecompensa modificar(PuntosRecompensa puntos) {
        if (puntos.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar los puntos.");
        }

        PuntosRecompensa existente = puntosRepository.findById(puntos.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe un sistema de puntos con ID " + puntos.getId()));

        existente.setPuntosDisponibles(puntos.getPuntosDisponibles());
        existente.setTasaConversion(puntos.getTasaConversion());
        existente.setCodigoCupon(puntos.getCodigoCupon());

        PuntosRecompensa actualizado = puntosRepository.save(existente);
        logger.info("Puntos modificados con éxito. ID: {}", actualizado.getId());
        return actualizado;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public PuntosRecompensa buscar(Integer id) {
        PuntosRecompensa puntos = puntosRepository.findById(id).orElse(null);
        if (puntos == null) {
            logger.warn("No se encontró sistema de puntos con ID: {}", id);
        }
        return puntos;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!puntosRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un sistema de puntos con ID " + id);
        }
        puntosRepository.deleteById(id);
        logger.info("Sistema de puntos eliminado con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<PuntosRecompensa> listar() {
        List<PuntosRecompensa> puntos = puntosRepository.findAll();
        logger.info("Se listaron {} sistemas de puntos.", puntos.size());
        return puntos;
    }

    // ------------------ BUSCAR POR CÓDIGO DE CUPÓN ------------------
    public PuntosRecompensa buscarPorCodigoCupon(String codigoCupon) {
        logger.info("Buscando puntos por código de cupón: {}", codigoCupon);
        return puntosRepository.findByCodigoCupon(codigoCupon).orElse(null);
    }

    // ------------------ BUSCAR CON PUNTOS MAYORES A ------------------
    public List<PuntosRecompensa> buscarConPuntosMayoresA(Integer puntos) {
        logger.info("Buscando clientes con más de {} puntos", puntos);
        return puntosRepository.findByPuntosDisponiblesGreaterThan(puntos);
    }

    // ------------------ TOP 10 CLIENTES CON MÁS PUNTOS ------------------
    public List<PuntosRecompensa> obtenerTop10ClientesConMasPuntos() {
        logger.info("Obteniendo top 10 clientes con más puntos");
        return puntosRepository.findTop10ByOrderByPuntosDisponiblesDesc();
    }

    // ------------------ CALCULAR TOTAL PUNTOS EN SISTEMA ------------------
    public Long calcularTotalPuntosEnSistema() {
        logger.info("Calculando total de puntos en el sistema");
        Long total = puntosRepository.calcularTotalPuntosEnSistema();
        return total != null ? total : 0L;
    }

    // ------------------ BUSCAR CLIENTES SIN PUNTOS ------------------
    public List<PuntosRecompensa> buscarClientesSinPuntos() {
        logger.info("Buscando clientes sin puntos");
        return puntosRepository.findByPuntosDisponiblesEquals(0);
    }
}