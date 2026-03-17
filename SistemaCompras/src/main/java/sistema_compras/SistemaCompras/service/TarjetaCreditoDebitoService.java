package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.TarjetaCreditoDebito;
import sistema_compras.SistemaCompras.entity.TipoTarjeta;
import sistema_compras.SistemaCompras.repository.TarjetaCreditoDebitoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TarjetaCreditoDebitoService implements ICrud<TarjetaCreditoDebito> {

    private static final Logger logger = LoggerFactory.getLogger(TarjetaCreditoDebitoService.class);

    @Autowired
    private TarjetaCreditoDebitoRepository tarjetaRepository;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public TarjetaCreditoDebito agregar(TarjetaCreditoDebito tarjeta) {
        if (tarjeta.getNumeroTarjeta() == null || tarjeta.getCliente() == null) {
            throw new IllegalArgumentException("Número de tarjeta y cliente son obligatorios.");
        }

        // Verificar duplicado ANTES de encriptar
        Optional<TarjetaCreditoDebito> existente = tarjetaRepository
                .findByClienteIdAndNumeroTarjeta(tarjeta.getCliente().getId(), tarjeta.getNumeroTarjeta());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("El cliente ya tiene registrada esa tarjeta.");
        }

        tarjeta.setNumeroTarjeta(encriptarNumeroTarjeta(tarjeta.getNumeroTarjeta()));
        tarjeta.setActivo(true);

        TarjetaCreditoDebito guardada = tarjetaRepository.save(tarjeta);
        logger.info("Tarjeta agregada con éxito para cliente ID: {}", tarjeta.getCliente().getId());
        return guardada;
    }

    // ------------------ ENCRIPTAR NÚMERO DE TARJETA ------------------
    private String encriptarNumeroTarjeta(String numeroCompleto) {
        if (numeroCompleto.length() < 4) {
            throw new IllegalArgumentException("Número de tarjeta inválido.");
        }
        String ultimos4 = numeroCompleto.substring(numeroCompleto.length() - 4);
        return "************" + ultimos4;
    }

    // ------------------ ACTIVAR ------------------
    @Transactional
    public void activar(Integer id) {
        TarjetaCreditoDebito tarjeta = tarjetaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una tarjeta con ID " + id));
        tarjeta.setActivo(true);
        tarjetaRepository.save(tarjeta);
        logger.info("Tarjeta activada. ID: {}", id);
    }

    // ------------------ DESACTIVAR ------------------
    @Transactional
    public void desactivar(Integer id) {
        TarjetaCreditoDebito tarjeta = tarjetaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una tarjeta con ID " + id));
        tarjeta.setActivo(false);
        tarjetaRepository.save(tarjeta);
        logger.info("Tarjeta desactivada. ID: {}", id);
    }

    // ------------------ VERIFICAR VENCIMIENTO ------------------
    public boolean verificarVencimiento(Integer id) {
        TarjetaCreditoDebito tarjeta = buscar(id);
        if (tarjeta == null) return false;
        return tarjeta.getFechaVencimiento().isAfter(LocalDate.now());
    }

    // ------------------ BUSCAR POR CLIENTE ------------------
    public List<TarjetaCreditoDebito> buscarPorCliente(Integer clienteId) {
        return tarjetaRepository.findByClienteId(clienteId);
    }

    // ------------------ BUSCAR TARJETAS ACTIVAS DEL CLIENTE ------------------
    public List<TarjetaCreditoDebito> buscarActivasPorCliente(Integer clienteId) {
        return tarjetaRepository.findByClienteIdAndActivoTrue(clienteId);
    }

    // ------------------ BUSCAR POR TIPO ------------------
    public List<TarjetaCreditoDebito> buscarPorTipo(TipoTarjeta tipo) {
        return tarjetaRepository.findByTipoTarjeta(tipo);
    }

    // ------------------ BUSCAR POR ÚLTIMOS DÍGITOS ------------------
    public List<TarjetaCreditoDebito> buscarPorUltimosDigitos(String ultimosDigitos) {
        if (ultimosDigitos == null || ultimosDigitos.length() != 4) {
            throw new IllegalArgumentException("Debe proporcionar exactamente 4 dígitos.");
        }
        return tarjetaRepository.findByUltimosDigitos(ultimosDigitos);
    }

    // ------------------ TARJETAS PRÓXIMAS A VENCER ------------------
    public List<TarjetaCreditoDebito> buscarProximasAVencer(int diasLimite) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(diasLimite);
        return tarjetaRepository.findTarjetasProximasAVencer(hoy, fechaLimite);
    }

    // ------------------ TARJETAS VENCIDAS ------------------
    public List<TarjetaCreditoDebito> buscarVencidas() {
        return tarjetaRepository.findByFechaVencimientoBefore(LocalDate.now());
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public TarjetaCreditoDebito modificar(TarjetaCreditoDebito tarjeta) {
        if (tarjeta.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar la tarjeta.");
        }

        TarjetaCreditoDebito existente = tarjetaRepository.findById(tarjeta.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe una tarjeta con ID " + tarjeta.getId()));

        existente.setNombreTitular(tarjeta.getNombreTitular());
        existente.setFechaVencimiento(tarjeta.getFechaVencimiento());
        existente.setActivo(tarjeta.getActivo());

        TarjetaCreditoDebito actualizada = tarjetaRepository.save(existente);
        logger.info("Tarjeta modificada con éxito. ID: {}", actualizada.getId());
        return actualizada;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public TarjetaCreditoDebito buscar(Integer id) {
        TarjetaCreditoDebito tarjeta = tarjetaRepository.findById(id).orElse(null);
        if (tarjeta == null) {
            logger.warn("No se encontró tarjeta con ID: {}", id);
        }
        return tarjeta;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!tarjetaRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una tarjeta con ID " + id);
        }
        tarjetaRepository.deleteById(id);
        logger.info("Tarjeta eliminada con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<TarjetaCreditoDebito> listar() {
        List<TarjetaCreditoDebito> tarjetas = tarjetaRepository.findAll();
        logger.info("Se listaron {} tarjetas.", tarjetas.size());
        return tarjetas;
    }
}