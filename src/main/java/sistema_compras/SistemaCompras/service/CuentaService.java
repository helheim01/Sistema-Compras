package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.Cuenta;
import sistema_compras.SistemaCompras.repository.CuentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CuentaService implements ICrud<Cuenta> {

    private static final Logger logger = LoggerFactory.getLogger(CuentaService.class);

    @Autowired
    private CuentaRepository cuentaRepository;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public Cuenta agregar(Cuenta cuenta) {
        if (cuenta.getCliente() == null) {
            throw new IllegalArgumentException("La cuenta debe tener un cliente asociado.");
        }

        cuenta.setFechaAbierta(LocalDateTime.now());
        cuenta.setEstaCerrada(false);
        cuenta.setSaldoPendiente(BigDecimal.ZERO);

        Cuenta guardada = cuentaRepository.save(cuenta);
        logger.info("Cuenta agregada con éxito para cliente ID: {}", cuenta.getCliente().getId());
        return guardada;
    }

    // ------------------ ABRIR CUENTA ------------------
    @Transactional
    public void abrirCuenta(Integer id) {
        Cuenta cuenta = buscar(id);
        if (cuenta == null) {
            throw new IllegalArgumentException("Cuenta no encontrada.");
        }

        if (!cuenta.getEstaCerrada()) {
            throw new IllegalArgumentException("La cuenta ya está abierta.");
        }

        cuenta.setEstaCerrada(false);
        cuenta.setFechaCerrada(null);

        cuentaRepository.save(cuenta);
        logger.info("Cuenta reabierta. ID: {}", id);
    }

    // ------------------ CERRAR CUENTA ------------------
    @Transactional
    public void cerrarCuenta(Integer id) {
        Cuenta cuenta = buscar(id);
        if (cuenta == null) {
            throw new IllegalArgumentException("Cuenta no encontrada.");
        }

        if (cuenta.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("No se puede cerrar una cuenta con saldo pendiente.");
        }

        cuenta.setEstaCerrada(true);
        cuenta.setFechaCerrada(LocalDateTime.now());

        cuentaRepository.save(cuenta);
        logger.info("Cuenta cerrada. ID: {}", id);
    }

    // ------------------ ACTUALIZAR DIRECCIÓN FACTURACIÓN ------------------
    @Transactional
    public void actualizarDireccionFacturacion(Integer id, String nuevaDireccion) {
        Cuenta cuenta = buscar(id);
        if (cuenta == null) {
            throw new IllegalArgumentException("Cuenta no encontrada.");
        }

        cuenta.setDireccionFacturacion(nuevaDireccion);
        cuentaRepository.save(cuenta);
        logger.info("Dirección de facturación actualizada para cuenta ID: {}", id);
    }

    // ------------------ ACTUALIZAR SALDO PENDIENTE ------------------
    @Transactional
    public void actualizarSaldoPendiente(Integer id, BigDecimal monto) {
        Cuenta cuenta = buscar(id);
        if (cuenta == null) {
            throw new IllegalArgumentException("Cuenta no encontrada.");
        }

        BigDecimal nuevoSaldo = cuenta.getSaldoPendiente().add(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            nuevoSaldo = BigDecimal.ZERO;
        }

        cuenta.setSaldoPendiente(nuevoSaldo);
        cuentaRepository.save(cuenta);
        logger.info("Saldo pendiente actualizado para cuenta ID: {}. Nuevo saldo: {}", id, nuevoSaldo);
    }

    // ------------------ CONTAR PEDIDOS ------------------
    public Integer contarPedidos(Integer cuentaId) {
        Cuenta cuenta = buscar(cuentaId);
        if (cuenta == null) {
            return 0;
        }
        return cuenta.getPedidos().size();
    }

    // ------------------ BUSCAR POR CLIENTE ------------------
    public Cuenta buscarPorCliente(Integer clienteId) {
        return cuentaRepository.findByClienteId(clienteId).orElse(null);
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public Cuenta modificar(Cuenta cuenta) {
        if (cuenta.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar la cuenta.");
        }

        Cuenta existente = cuentaRepository.findById(cuenta.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con ID " + cuenta.getId()));

        existente.setDireccionFacturacion(cuenta.getDireccionFacturacion());
        existente.setSaldoPendiente(cuenta.getSaldoPendiente());

        Cuenta actualizada = cuentaRepository.save(existente);
        logger.info("Cuenta modificada con éxito. ID: {}", actualizada.getId());
        return actualizada;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public Cuenta buscar(Integer id) {
        Cuenta cuenta = cuentaRepository.findById(id).orElse(null);
        if (cuenta == null) {
            logger.warn("No se encontró cuenta con ID: {}", id);
        }
        return cuenta;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!cuentaRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una cuenta con ID " + id);
        }
        cuentaRepository.deleteById(id);
        logger.info("Cuenta eliminada con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<Cuenta> listar() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        logger.info("Se listaron {} cuentas.", cuentas.size());
        return cuentas;
    }
}