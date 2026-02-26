package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.LineaPedido;
import sistema_compras.SistemaCompras.repository.LineaPedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LineaPedidoService implements ICrud<LineaPedido> {

    private static final Logger logger = LoggerFactory.getLogger(LineaPedidoService.class);

    @Autowired
    private LineaPedidoRepository lineaPedidoRepository;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public LineaPedido agregar(LineaPedido lineaPedido) {
        if (lineaPedido.getPedido() == null || lineaPedido.getProducto() == null) {
            throw new IllegalArgumentException("Pedido y producto son obligatorios.");
        }

        calcularSubtotal(lineaPedido);

        LineaPedido guardada = lineaPedidoRepository.save(lineaPedido);
        logger.info("Línea de pedido agregada con éxito. Producto: {}",
                guardada.getProducto().getNombre());
        return guardada;
    }

    // ------------------ APLICAR DESCUENTO ------------------
    @Transactional
    public void aplicarDescuento(Integer id, BigDecimal porcentaje) {
        LineaPedido linea = buscar(id);
        if (linea == null) {
            throw new IllegalArgumentException("Línea de pedido no encontrada.");
        }

        BigDecimal descuento = linea.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(linea.getCantidad()))
                .multiply(porcentaje)
                .divide(BigDecimal.valueOf(100));

        linea.setDescuento(descuento);
        calcularSubtotal(linea);

        lineaPedidoRepository.save(linea);
        logger.info("Descuento aplicado a línea de pedido ID: {}. Descuento: {}", id, descuento);
    }

    // ------------------ CALCULAR SUBTOTAL ------------------
    private void calcularSubtotal(LineaPedido linea) {
        BigDecimal subtotalSinDescuento = linea.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(linea.getCantidad()));
        BigDecimal subtotal = subtotalSinDescuento.subtract(linea.getDescuento());
        linea.setSubtotal(subtotal);
    }

    // ------------------ BUSCAR POR PEDIDO ------------------
    public List<LineaPedido> buscarPorPedido(Integer pedidoId) {
        return lineaPedidoRepository.findByPedidoId(pedidoId);
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public LineaPedido modificar(LineaPedido lineaPedido) {
        if (lineaPedido.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar la línea.");
        }

        LineaPedido existente = lineaPedidoRepository.findById(lineaPedido.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe una línea con ID " + lineaPedido.getId()));

        existente.setCantidad(lineaPedido.getCantidad());
        existente.setPrecioUnitario(lineaPedido.getPrecioUnitario());
        existente.setDescuento(lineaPedido.getDescuento());
        calcularSubtotal(existente);

        LineaPedido actualizada = lineaPedidoRepository.save(existente);
        logger.info("Línea de pedido modificada con éxito. ID: {}", actualizada.getId());
        return actualizada;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public LineaPedido buscar(Integer id) {
        LineaPedido linea = lineaPedidoRepository.findById(id).orElse(null);
        if (linea == null) {
            logger.warn("No se encontró línea de pedido con ID: {}", id);
        }
        return linea;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!lineaPedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una línea de pedido con ID " + id);
        }
        lineaPedidoRepository.deleteById(id);
        logger.info("Línea de pedido eliminada con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<LineaPedido> listar() {
        List<LineaPedido> lineas = lineaPedidoRepository.findAll();
        logger.info("Se listaron {} líneas de pedido.", lineas.size());
        return lineas;
    }
}