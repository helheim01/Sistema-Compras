package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.ImagenProducto;
import sistema_compras.SistemaCompras.entity.Producto;
import sistema_compras.SistemaCompras.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductoService implements ICrud<Producto> {

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ImagenProductoService imagenProductoService;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public Producto agregar(Producto producto) {
        if (producto.getNombre() == null || producto.getPrecio() == null) {
            throw new IllegalArgumentException("Nombre y precio son obligatorios.");
        }

        // Validar código único
        if (productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con ese código.");
        }

        producto.setFechaCreacion(LocalDateTime.now());
        producto.setActivo(true);

        Producto guardado = productoRepository.save(producto);
        logger.info("Producto agregado con éxito: {}", guardado.getNombre());
        return guardado;
    }

    // ------------------ AGREGAR IMÁGENES ------------------
    @Transactional
    public Producto agregarImagenes(Integer idProducto, List<ImagenProducto> nuevasImagenes) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + idProducto));

        for (ImagenProducto imagen : nuevasImagenes) {
            imagen.setProducto(producto);
            imagenProductoService.agregar(imagen);
        }

        logger.info("Imágenes agregadas al producto: {}", producto.getNombre());
        return productoRepository.findById(idProducto).get();
    }

    // ------------------ ACTUALIZAR STOCK ------------------
    @Transactional
    public void actualizarStock(Integer id, Integer cantidad) {
        Producto producto = buscar(id);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado.");
        }

        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + producto.getStock());
        }

        producto.setStock(nuevoStock);
        productoRepository.save(producto);
        logger.info("Stock actualizado para producto {}: nuevo stock = {}", producto.getNombre(), nuevoStock);
    }

    // ------------------ VERIFICAR DISPONIBILIDAD ------------------
    public boolean verificarDisponibilidad(Integer id, Integer cantidadRequerida) {
        Producto producto = buscar(id);
        if (producto == null || !producto.getActivo()) {
            return false;
        }
        return producto.getStock() >= cantidadRequerida;
    }

    // ------------------ APLICAR DESCUENTO ------------------
    @Transactional
    public void aplicarDescuento(Integer id, BigDecimal porcentaje) {
        Producto producto = buscar(id);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado.");
        }

        BigDecimal descuento = producto.getPrecio().multiply(porcentaje).divide(BigDecimal.valueOf(100));
        BigDecimal nuevoPrecio = producto.getPrecio().subtract(descuento);

        producto.setPrecio(nuevoPrecio);
        productoRepository.save(producto);
        logger.info("Descuento aplicado a producto {}: nuevo precio = {}", producto.getNombre(), nuevoPrecio);
    }

    // ------------------ DESACTIVAR ------------------
    @Transactional
    public void desactivar(Integer id) {
        Producto producto = buscar(id);
        if (producto != null) {
            producto.setActivo(false);
            productoRepository.save(producto);
            logger.info("Producto desactivado: {}", producto.getNombre());
        }
    }

    // ------------------ BUSCAR POR CÓDIGO ------------------
    public Producto buscarPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo).orElse(null);
    }

    // ------------------ BUSCAR POR CATEGORÍA ------------------
    public List<Producto> buscarPorCategoria(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    // ------------------ BUSCAR PRODUCTOS ACTIVOS ------------------
    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public Producto modificar(Producto producto) {
        if (producto.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar el producto.");
        }

        Producto existente = productoRepository.findById(producto.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe un producto con ID " + producto.getId()));

        existente.setNombre(producto.getNombre());
        existente.setDescripcion(producto.getDescripcion());
        existente.setPrecio(producto.getPrecio());
        existente.setStock(producto.getStock());
        existente.setProveedor(producto.getProveedor());
        existente.setCategoria(producto.getCategoria());
        existente.setImagenUrl(producto.getImagenUrl());
        existente.setActivo(producto.getActivo());

        // Gestionar imágenes
        if (producto.getImagenes() != null) {
            existente.getImagenes().clear();
            producto.getImagenes().forEach(imagen -> {
                imagen.setProducto(existente);
                existente.getImagenes().add(imagen);
            });
        }

        Producto actualizado = productoRepository.save(existente);
        logger.info("Producto modificado con éxito: {}", actualizado.getNombre());
        return actualizado;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public Producto buscar(Integer id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            logger.warn("No se encontró producto con ID: {}", id);
        }
        return producto;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un producto con ID " + id);
        }
        productoRepository.deleteById(id);
        logger.info("Producto eliminado con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<Producto> listar() {
        List<Producto> productos = productoRepository.findAll();
        logger.info("Se listaron {} productos.", productos.size());
        return productos;
    }
}