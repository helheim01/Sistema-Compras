package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.LineaCarrito;
import sistema_compras.SistemaCompras.repository.LineaCarritoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LineaCarritoService implements ICrud<LineaCarrito> {

    private static final Logger logger = LoggerFactory.getLogger(LineaCarritoService.class);

    @Autowired
    private LineaCarritoRepository lineaCarritoRepository;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public LineaCarrito agregar(LineaCarrito lineaCarrito) {
        if (lineaCarrito.getCarritoCompra() == null || lineaCarrito.getProducto() == null) {
            throw new IllegalArgumentException("Carrito y producto son obligatorios.");
        }

        calcularSubtotal(lineaCarrito);

        LineaCarrito guardada = lineaCarritoRepository.save(lineaCarrito);
        logger.info("Línea de carrito agregada con éxito. Producto: {}",
                guardada.getProducto().getNombre());
        return guardada;
    }

    // ------------------ ACTUALIZAR CANTIDAD ------------------
    @Transactional
    public void actualizarCantidad(Integer id, Integer nuevaCantidad) {
        LineaCarrito linea = buscar(id);
        if (linea == null) {
            throw new IllegalArgumentException("Línea de carrito no encontrada.");
        }

        linea.setCantidad(nuevaCantidad);
        calcularSubtotal(linea);

        lineaCarritoRepository.save(linea);
        logger.info("Cantidad actualizada en línea de carrito ID: {}", id);
    }

    // ------------------ CALCULAR SUBTOTAL ------------------
    private void calcularSubtotal(LineaCarrito linea) {
        BigDecimal subtotal = linea.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(linea.getCantidad()));
        linea.setSubtotal(subtotal);
    }

    // ------------------ BUSCAR POR CARRITO ------------------
    public List<LineaCarrito> buscarPorCarrito(Integer carritoId) {
        return lineaCarritoRepository.findByCarritoCompraId(carritoId);
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public LineaCarrito modificar(LineaCarrito lineaCarrito) {
        if (lineaCarrito.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar la línea.");
        }

        LineaCarrito existente = lineaCarritoRepository.findById(lineaCarrito.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe una línea con ID " + lineaCarrito.getId()));

        existente.setCantidad(lineaCarrito.getCantidad());
        existente.setPrecioUnitario(lineaCarrito.getPrecioUnitario());
        calcularSubtotal(existente);

        LineaCarrito actualizada = lineaCarritoRepository.save(existente);
        logger.info("Línea de carrito modificada con éxito. ID: {}", actualizada.getId());
        return actualizada;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public LineaCarrito buscar(Integer id) {
        LineaCarrito linea = lineaCarritoRepository.findById(id).orElse(null);
        if (linea == null) {
            logger.warn("No se encontró línea de carrito con ID: {}", id);
        }
        return linea;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!lineaCarritoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una línea de carrito con ID " + id);
        }
        lineaCarritoRepository.deleteById(id);
        logger.info("Línea de carrito eliminada con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<LineaCarrito> listar() {
        List<LineaCarrito> lineas = lineaCarritoRepository.findAll();
        logger.info("Se listaron {} líneas de carrito.", lineas.size());
        return lineas;
    }
}