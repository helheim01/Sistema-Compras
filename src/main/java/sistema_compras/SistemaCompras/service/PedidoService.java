package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.*;
import sistema_compras.SistemaCompras.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PedidoService implements ICrud<Pedido> {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarritoCompraService carritoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private EmailService emailService;

    // ------------------ CREAR PEDIDO DESDE CARRITO ------------------
    @Transactional
    public Pedido crearPedidoDesdeCarrito(Integer carritoId, String direccionEnvio) {
        CarritoCompra carrito = carritoService.buscar(carritoId);
        if (carrito == null || carrito.getLineas().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setCliente(carrito.getCliente());
        pedido.setCuenta(carrito.getCliente().getCuenta());
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.NUEVO);

        // Convertir líneas de carrito a líneas de pedido
        BigDecimal subtotal = BigDecimal.ZERO;
        for (LineaCarrito lineaCarrito : carrito.getLineas()) {
            // Verificar disponibilidad y actualizar stock
            Producto producto = lineaCarrito.getProducto();
            if (!productoService.verificarDisponibilidad(producto.getId(), lineaCarrito.getCantidad())) {
                throw new IllegalArgumentException("Stock insuficiente para: " + producto.getNombre());
            }

            LineaPedido lineaPedido = new LineaPedido();
            lineaPedido.setPedido(pedido);
            lineaPedido.setProducto(producto);
            lineaPedido.setCantidad(lineaCarrito.getCantidad());
            lineaPedido.setPrecioUnitario(lineaCarrito.getPrecioUnitario());
            lineaPedido.setDescuento(BigDecimal.ZERO);
            lineaPedido.setSubtotal(lineaCarrito.getSubtotal());

            pedido.getLineas().add(lineaPedido);
            subtotal = subtotal.add(lineaCarrito.getSubtotal());

            // Descontar stock
            productoService.actualizarStock(producto.getId(), -lineaCarrito.getCantidad());
        }

        // Calcular totales
        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(subtotal.multiply(BigDecimal.valueOf(0.21))); // 21% IVA
        pedido.setCostoEnvio(BigDecimal.valueOf(500)); // Fijo por ahora
        pedido.setTotal(pedido.getSubtotal().add(pedido.getImpuesto()).add(pedido.getCostoEnvio()));

        // Guardar pedido
        Pedido guardado = pedidoRepository.save(pedido);

        // Vaciar carrito
        carritoService.vaciarCarrito(carritoId);

        // Enviar confirmación por email
        emailService.enviarConfirmacionPedido(guardado);

        logger.info("Pedido creado con éxito. Número: {}", guardado.getNumeroPedido());
        return guardado;
    }

    // ------------------ GENERAR NÚMERO DE PEDIDO ------------------
    private String generarNumeroPedido() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = pedidoRepository.count() + 1;
        return String.format("PED-%s-%05d", fecha, count);
    }

    // ------------------ ACTUALIZAR ESTADO ------------------
    @Transactional
    public void actualizarEstado(Integer id, EstadoPedido nuevoEstado) {
        Pedido pedido = buscar(id);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no encontrado.");
        }

        EstadoPedido estadoAnterior = pedido.getEstado();
        pedido.setEstado(nuevoEstado);

        // Actualizar fechas según el estado
        if (nuevoEstado == EstadoPedido.ENVIADO && pedido.getFechaEnvio() == null) {
            pedido.setFechaEnvio(LocalDateTime.now());
        } else if (nuevoEstado == EstadoPedido.ENTREGADO && pedido.getFechaEntrega() == null) {
            pedido.setFechaEntrega(LocalDateTime.now());
        }

        pedidoRepository.save(pedido);

        // Enviar notificación
        emailService.enviarActualizacionEstado(pedido);

        logger.info("Estado de pedido {} actualizado de {} a {}",
                pedido.getNumeroPedido(), estadoAnterior, nuevoEstado);
    }

    // ------------------ CANCELAR PEDIDO ------------------
    @Transactional
    public void cancelarPedido(Integer id) {
        Pedido pedido = buscar(id);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no encontrado.");
        }

        if (pedido.getEstado() == EstadoPedido.ENVIADO || pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("No se puede cancelar un pedido que ya fue enviado o entregado.");
        }

        // Devolver stock
        for (LineaPedido linea : pedido.getLineas()) {
            productoService.actualizarStock(linea.getProducto().getId(), linea.getCantidad());
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        logger.warn("Pedido cancelado: {}", pedido.getNumeroPedido());
    }

    // ------------------ BUSCAR POR CLIENTE ------------------
    public List<Pedido> buscarPorCliente(Integer clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    // ------------------ BUSCAR POR ESTADO ------------------
    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    // ------------------ CALCULAR TOTAL ------------------
    public BigDecimal calcularTotal(Pedido pedido) {
        return pedido.getSubtotal()
                .add(pedido.getImpuesto())
                .add(pedido.getCostoEnvio());
    }

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public Pedido agregar(Pedido pedido) {
        if (pedido.getCliente() == null) {
            throw new IllegalArgumentException("El pedido debe tener un cliente asociado.");
        }

        pedido.setFechaPedido(LocalDateTime.now());
        if (pedido.getEstado() == null) {
            pedido.setEstado(EstadoPedido.NUEVO);
        }

        Pedido guardado = pedidoRepository.save(pedido);
        logger.info("Pedido agregado con éxito. Número: {}", guardado.getNumeroPedido());
        return guardado;
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public Pedido modificar(Pedido pedido) {
        if (pedido.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar el pedido.");
        }

        Pedido existente = pedidoRepository.findById(pedido.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe un pedido con ID " + pedido.getId()));

        existente.setDireccionEnvio(pedido.getDireccionEnvio());
        existente.setEstado(pedido.getEstado());
        existente.setNotasEspeciales(pedido.getNotasEspeciales());

        Pedido actualizado = pedidoRepository.save(existente);
        logger.info("Pedido modificado con éxito. Número: {}", actualizado.getNumeroPedido());
        return actualizado;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public Pedido buscar(Integer id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            logger.warn("No se encontró pedido con ID: {}", id);
        }
        return pedido;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un pedido con ID " + id);
        }
        pedidoRepository.deleteById(id);
        logger.info("Pedido eliminado con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<Pedido> listar() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        logger.info("Se listaron {} pedidos.", pedidos.size());
        return pedidos;
    }
}