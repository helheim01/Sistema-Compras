package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.*;
import sistema_compras.SistemaCompras.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PagoService implements ICrud<Pago> {

    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PuntosRecompensaService puntosRecompensaService;

    @Autowired
    private EmailService emailService;

    // ------------------ PROCESAR PAGO ------------------
    @Transactional
    public Pago procesarPago(Integer pedidoId, TipoPago tipoPago, MetodoPago metodoPago) {
        Pedido pedido = pedidoService.buscar(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no encontrado.");
        }

        // Validar que el pedido no esté pagado completamente
        BigDecimal totalPagado = calcularTotalPagado(pedidoId);
        BigDecimal totalPendiente = pedido.getTotal().subtract(totalPagado);

        if (totalPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El pedido ya está pagado completamente.");
        }

        // Crear pago
        Pago pago = new Pago();
        pago.setCodigoTransaccion(generarCodigoTransaccion());
        pago.setFechaPago(LocalDateTime.now());
        pago.setHoraPago(LocalTime.now());
        pago.setImporte(totalPendiente);
        pago.setTipoPago(tipoPago);
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setPedido(pedido);
        pago.setCuenta(pedido.getCuenta());
        pago.setMetodoPago(metodoPago);

        // Procesar según tipo de pago
        boolean exito = false;
        try {
            switch (tipoPago) {
                case TARJETA_CREDITO:
                case TARJETA_DEBITO:
                    if (!(metodoPago instanceof TarjetaCreditoDebito tarjeta)) {
                    throw new IllegalArgumentException("Método de pago inválido para tarjeta.");
                }
                exito = procesarPagoTarjeta(pago, tarjeta);
                    break;
                case PUNTOS_RECOMPENSA:
                    exito = procesarPagoPuntos(pago, (PuntosRecompensa) metodoPago);
                    break;
                case PAYPAL:
                case TRANSFERENCIA:
                    exito = procesarPagoExterno(pago);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de pago no soportado.");
            }

            if (exito) {
                pago.setEstado(EstadoPago.COMPLETADO);
                pago.setDetalle("Pago procesado exitosamente");

                // Actualizar estado del pedido si está completamente pagado
                if (calcularTotalPagado(pedidoId).add(pago.getImporte()).compareTo(pedido.getTotal()) >= 0) {
                    pedidoService.actualizarEstado(pedidoId, EstadoPedido.PROCESANDO);
                }
            } else {
                pago.setEstado(EstadoPago.FALLIDO);
                pago.setDetalle("Error al procesar el pago");
            }
        } catch (Exception e) {
            pago.setEstado(EstadoPago.FALLIDO);
            pago.setDetalle("Error: " + e.getMessage());
            logger.error("Error procesando pago: {}", e.getMessage());
        }

        Pago guardado = pagoRepository.save(pago);

        // Enviar confirmación si fue exitoso
        if (guardado.getEstado() == EstadoPago.COMPLETADO) {
            emailService.enviarConfirmacionPago(guardado);
        }

        logger.info("Pago procesado. Código: {}, Estado: {}", guardado.getCodigoTransaccion(), guardado.getEstado());
        return guardado;
    }

    // ------------------ PROCESAR PAGO CON TARJETA ------------------
    private boolean procesarPagoTarjeta(Pago pago, TarjetaCreditoDebito tarjeta) {
        // Aquí iría la integración con pasarela de pago (Stripe, MercadoPago, etc.)
        // Por ahora simulamos el proceso

        logger.info("Procesando pago con tarjeta terminada en: {}",
                tarjeta.getNumeroTarjeta().substring(tarjeta.getNumeroTarjeta().length() - 4));

        // Validar tarjeta
        if (!validarTarjeta(tarjeta)) {
            throw new IllegalArgumentException("Tarjeta inválida o vencida.");
        }

        // Simular llamada a API de pago
        // boolean resultado = stripeService.procesarPago(pago.getImporte(), tarjeta);
        boolean resultado = true; // Simulación

        return resultado;
    }

    // ------------------ PROCESAR PAGO CON PUNTOS ------------------
    private boolean procesarPagoPuntos(Pago pago, PuntosRecompensa puntosRecompensa) {
        // Calcular puntos necesarios
        BigDecimal tasaConversion = puntosRecompensa.getTasaConversion(); // ej: 0.01 (1 punto = $0.01)
        int puntosNecesarios = pago.getImporte().divide(tasaConversion, 0, RoundingMode.UP).intValue();

        if (puntosRecompensa.getPuntosDisponibles() < puntosNecesarios) {
            throw new IllegalArgumentException("Puntos insuficientes. Necesarios: " + puntosNecesarios
                    + ", Disponibles: " + puntosRecompensa.getPuntosDisponibles());
        }

        // Descontar puntos
        puntosRecompensaService.canjearPuntos(puntosRecompensa.getId(), puntosNecesarios);

        logger.info("Pago procesado con puntos. Puntos canjeados: {}", puntosNecesarios);
        return true;
    }

    // ------------------ PROCESAR PAGO EXTERNO ------------------
    private boolean procesarPagoExterno(Pago pago) {
        // Aquí iría integración con PayPal, transferencia bancaria, etc.
        logger.info("Procesando pago externo tipo: {}", pago.getTipoPago());
        // Simular proceso
        return true;
    }

    // ------------------ VALIDAR TARJETA ------------------
    private boolean validarTarjeta(TarjetaCreditoDebito tarjeta) {
        // Validar fecha de vencimiento
        if (tarjeta.getFechaVencimiento().isBefore(java.time.LocalDate.now())) {
            return false;
        }
        // Validar que esté activa
        return tarjeta.getActivo();
    }

    // ------------------ GENERAR CÓDIGO DE TRANSACCIÓN ------------------
    private String generarCodigoTransaccion() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ------------------ CALCULAR TOTAL PAGADO ------------------
    public BigDecimal calcularTotalPagado(Integer pedidoId) {
        BigDecimal total = pagoRepository.calcularTotalPagadoPorPedido(pedidoId);
        return total != null ? total : BigDecimal.ZERO; // SUM puede devolver null si no hay filas
    }

    // ------------------ REEMBOLSAR ------------------
    @Transactional
    public void reembolsar(Integer pagoId) {
        Pago pago = buscar(pagoId);
        if (pago == null) {
            throw new IllegalArgumentException("Pago no encontrado.");
        }

        if (pago.getEstado() != EstadoPago.COMPLETADO) {
            throw new IllegalArgumentException("Solo se pueden reembolsar pagos completados.");
        }

        // Procesar reembolso según tipo de pago
        switch (pago.getTipoPago()) {
            case PUNTOS_RECOMPENSA:
                // Devolver puntos
                PuntosRecompensa puntos = (PuntosRecompensa) pago.getMetodoPago();
                int puntosDevolver = pago.getImporte().divide(puntos.getTasaConversion(), 0, BigDecimal.ROUND_DOWN).intValue();
                puntosRecompensaService.agregarPuntos(puntos.getId(), puntosDevolver);
                break;
            default:
                // Aquí iría el proceso de reembolso con la pasarela
                logger.info("Procesando reembolso para pago: {}", pago.getCodigoTransaccion());
                break;
        }

        pago.setEstado(EstadoPago.REEMBOLSADO);
        pago.setDetalle("Pago reembolsado");
        pagoRepository.save(pago);

        logger.info("Pago reembolsado: {}", pago.getCodigoTransaccion());
    }

    // ------------------ BUSCAR POR PEDIDO ------------------
    public List<Pago> buscarPorPedido(Integer pedidoId) {
        return pagoRepository.findByPedidoId(pedidoId);
    }

    // ------------------ BUSCAR POR ESTADO ------------------
    public List<Pago> buscarPorEstado(EstadoPago estado) {
        return pagoRepository.findByEstado(estado);
    }

    // ------------------ VALIDAR PAGO ------------------
    public boolean validarPago(Integer pagoId) {
        Pago pago = buscar(pagoId);
        return pago != null && pago.getEstado() == EstadoPago.COMPLETADO;
    }

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public Pago agregar(Pago pago) {
        if (pago.getPedido() == null || pago.getImporte() == null) {
            throw new IllegalArgumentException("Pedido e importe son obligatorios.");
        }

        if (pago.getCodigoTransaccion() == null) {
            pago.setCodigoTransaccion(generarCodigoTransaccion());
        }

        pago.setFechaPago(LocalDateTime.now());
        pago.setHoraPago(LocalTime.now());

        Pago guardado = pagoRepository.save(pago);
        logger.info("Pago agregado con éxito. Código: {}", guardado.getCodigoTransaccion());
        return guardado;
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public Pago modificar(Pago pago) {
        if (pago.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar el pago.");
        }

        Pago existente = pagoRepository.findById(pago.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe un pago con ID " + pago.getId()));

        existente.setEstado(pago.getEstado());
        existente.setDetalle(pago.getDetalle());

        Pago actualizado = pagoRepository.save(existente);
        logger.info("Pago modificado con éxito. Código: {}", actualizado.getCodigoTransaccion());
        return actualizado;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public Pago buscar(Integer id) {
        Pago pago = pagoRepository.findById(id).orElse(null);
        if (pago == null) {
            logger.warn("No se encontró pago con ID: {}", id);
        }
        return pago;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!pagoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un pago con ID " + id);
        }
        pagoRepository.deleteById(id);
        logger.info("Pago eliminado con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<Pago> listar() {
        List<Pago> pagos = pagoRepository.findAll();
        logger.info("Se listaron {} pagos.", pagos.size());
        return pagos;
    }

    // ------------------ BUSCAR POR CÓDIGO DE TRANSACCIÓN ------------------
    public Optional<Pago> buscarPorCodigo(String codigoTransaccion) {
        return pagoRepository.findByCodigoTransaccion(codigoTransaccion);
    }

    // ------------------ BUSCAR POR CUENTA ------------------
    public List<Pago> buscarPorCuenta(Integer cuentaId) {
        return pagoRepository.findByCuentaId(cuentaId);
    }

    // ------------------ BUSCAR POR TIPO DE PAGO ------------------
    public List<Pago> buscarPorTipo(TipoPago tipoPago) {
        return pagoRepository.findByTipoPago(tipoPago);
    }

    // ------------------ BUSCAR POR RANGO DE FECHAS ------------------
    public List<Pago> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la de fin.");
        }
        return pagoRepository.findByFechaPagoBetween(inicio, fin);
    }

    // ------------------ BUSCAR POR ESTADOS ------------------
    public List<Pago> buscarPorEstados(List<EstadoPago> estados) {
        return pagoRepository.findByEstadoIn(estados);
    }

    // ------------------ BUSCAR POR PEDIDO Y ESTADO ------------------
    public Optional<Pago> buscarPorPedidoYEstado(Integer pedidoId, EstadoPago estado) {
        return pagoRepository.findByPedidoIdAndEstado(pedidoId, estado);
    }

    // ------------------ INGRESOS POR PERÍODO ------------------
    public BigDecimal calcularIngresosPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        BigDecimal total = pagoRepository.calcularIngresosPorPeriodo(inicio, fin);
        return total != null ? total : BigDecimal.ZERO;
    }

    // ------------------ ESTADÍSTICAS POR TIPO ------------------
    public List<Object[]> obtenerEstadisticasPorTipo() {
        return pagoRepository.estadisticasPorTipoPago();
    }

    // ------------------ PAGOS POR CLIENTE ------------------
    public List<Pago> buscarPorCliente(Integer clienteId) {
        return pagoRepository.findPagosPorCliente(clienteId);
    }
}