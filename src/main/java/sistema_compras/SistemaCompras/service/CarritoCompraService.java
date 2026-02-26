package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.CarritoCompra;
import sistema_compras.SistemaCompras.entity.LineaCarrito;
import sistema_compras.SistemaCompras.entity.Producto;
import sistema_compras.SistemaCompras.repository.CarritoCompraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarritoCompraService implements ICrud<CarritoCompra> {

    private static final Logger logger = LoggerFactory.getLogger(CarritoCompraService.class);

    @Autowired
    private CarritoCompraRepository carritoRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private LineaCarritoService lineaCarritoService;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public CarritoCompra agregar(CarritoCompra carrito) {
        carrito.setFechaCreacion(LocalDateTime.now());
        carrito.setFechaActualizacion(LocalDateTime.now());
        carrito.setSubtotal(BigDecimal.ZERO);

        CarritoCompra guardado = carritoRepository.save(carrito);
        logger.info("Carrito creado con éxito para cliente ID: {}", carrito.getCliente().getId());
        return guardado;
    }

    // ------------------ AGREGAR PRODUCTO ------------------
    @Transactional
    public CarritoCompra agregarProducto(Integer carritoId, Integer productoId, Integer cantidad) {
        CarritoCompra carrito = buscar(carritoId);
        if (carrito == null) {
            throw new IllegalArgumentException("Carrito no encontrado.");
        }

        Producto producto = productoService.buscar(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado.");
        }

        // Verificar disponibilidad
        if (!productoService.verificarDisponibilidad(productoId, cantidad)) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        // Verificar si el producto ya está en el carrito
        LineaCarrito lineaExistente = carrito.getLineas().stream()
                .filter(linea -> linea.getProducto().getId().equals(productoId))
                .findFirst()
                .orElse(null);

        if (lineaExistente != null) {
            // Actualizar cantidad
            lineaExistente.setCantidad(lineaExistente.getCantidad() + cantidad);
            lineaExistente.setSubtotal(lineaExistente.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(lineaExistente.getCantidad())));
            lineaCarritoService.modificar(lineaExistente);
        } else {
            // Crear nueva línea
            LineaCarrito nuevaLinea = new LineaCarrito();
            nuevaLinea.setCarritoCompra(carrito);
            nuevaLinea.setProducto(producto);
            nuevaLinea.setCantidad(cantidad);
            nuevaLinea.setPrecioUnitario(producto.getPrecio());
            nuevaLinea.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));

            carrito.getLineas().add(nuevaLinea);
        }

        carrito.setFechaActualizacion(LocalDateTime.now());
        calcularSubtotal(carrito);

        CarritoCompra actualizado = carritoRepository.save(carrito);
        logger.info("Producto agregado al carrito. Producto: {}, Cantidad: {}", producto.getNombre(), cantidad);
        return actualizado;
    }

    // ------------------ ELIMINAR PRODUCTO ------------------
    @Transactional
    public CarritoCompra eliminarProducto(Integer carritoId, Integer productoId) {
        CarritoCompra carrito = buscar(carritoId);
        if (carrito == null) {
            throw new IllegalArgumentException("Carrito no encontrado.");
        }

        carrito.getLineas().removeIf(linea -> linea.getProducto().getId().equals(productoId));
        carrito.setFechaActualizacion(LocalDateTime.now());
        calcularSubtotal(carrito);

        CarritoCompra actualizado = carritoRepository.save(carrito);
        logger.info("Producto eliminado del carrito. Producto ID: {}", productoId);
        return actualizado;
    }

    // ------------------ ACTUALIZAR CANTIDAD ------------------
    @Transactional
    public CarritoCompra actualizarCantidad(Integer carritoId, Integer productoId, Integer nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            return eliminarProducto(carritoId, productoId);
        }

        CarritoCompra carrito = buscar(carritoId);
        if (carrito == null) {
            throw new IllegalArgumentException("Carrito no encontrado.");
        }

        LineaCarrito linea = carrito.getLineas().stream()
                .filter(l -> l.getProducto().getId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el carrito."));

        // Verificar disponibilidad
        if (!productoService.verificarDisponibilidad(productoId, nuevaCantidad)) {
            throw new IllegalArgumentException("Stock insuficiente.");
        }

        linea.setCantidad(nuevaCantidad);
        linea.setSubtotal(linea.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevaCantidad)));

        carrito.setFechaActualizacion(LocalDateTime.now());
        calcularSubtotal(carrito);

        CarritoCompra actualizado = carritoRepository.save(carrito);
        logger.info("Cantidad actualizada en carrito. Producto ID: {}, Nueva cantidad: {}", productoId, nuevaCantidad);
        return actualizado;
    }

    // ------------------ VACIAR CARRITO ------------------
    @Transactional
    public void vaciarCarrito(Integer carritoId) {
        CarritoCompra carrito = buscar(carritoId);
        if (carrito != null) {
            carrito.getLineas().clear();
            carrito.setSubtotal(BigDecimal.ZERO);
            carrito.setFechaActualizacion(LocalDateTime.now());
            carritoRepository.save(carrito);
            logger.info("Carrito vaciado. ID: {}", carritoId);
        }
    }

    // ------------------ CALCULAR SUBTOTAL ------------------
    private void calcularSubtotal(CarritoCompra carrito) {
        BigDecimal subtotal = carrito.getLineas().stream()
                .map(LineaCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        carrito.setSubtotal(subtotal);
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public CarritoCompra modificar(CarritoCompra carrito) {
        if (carrito.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar el carrito.");
        }

        CarritoCompra existente = carritoRepository.findById(carrito.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe un carrito con ID " + carrito.getId()));

        existente.setFechaActualizacion(LocalDateTime.now());
        calcularSubtotal(existente);

        CarritoCompra actualizado = carritoRepository.save(existente);
        logger.info("Carrito modificado con éxito. ID: {}", actualizado.getId());
        return actualizado;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public CarritoCompra buscar(Integer id) {
        CarritoCompra carrito = carritoRepository.findById(id).orElse(null);
        if (carrito == null) {
            logger.warn("No se encontró carrito con ID: {}", id);
        }
        return carrito;
    }

    // ------------------ BUSCAR POR CLIENTE ------------------
    public CarritoCompra buscarPorCliente(Integer clienteId) {
        return carritoRepository.findByClienteId(clienteId).orElse(null);
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!carritoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un carrito con ID " + id);
        }
        carritoRepository.deleteById(id);
        logger.info("Carrito eliminado con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<CarritoCompra> listar() {
        List<CarritoCompra> carritos = carritoRepository.findAll();
        logger.info("Se listaron {} carritos.", carritos.size());
        return carritos;
    }
}