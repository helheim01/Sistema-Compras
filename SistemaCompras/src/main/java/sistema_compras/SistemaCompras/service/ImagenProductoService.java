package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.ImagenProducto;
import sistema_compras.SistemaCompras.repository.ImagenProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImagenProductoService implements ICrud<ImagenProducto> {

    private static final Logger logger = LoggerFactory.getLogger(ImagenProductoService.class);

    @Autowired
    private ImagenProductoRepository imagenRepository;

    // ------------------ AGREGAR ------------------
    @Transactional
    @Override
    public ImagenProducto agregar(ImagenProducto imagen) {
        if (imagen.getUrlImagen() == null || imagen.getProducto() == null) {
            throw new IllegalArgumentException("URL y producto son obligatorios.");
        }

        if (imagen.getOrden() == null) {
            // Asignar el siguiente orden disponible
            Integer maxOrden = imagenRepository.findMaxOrdenByProductoId(imagen.getProducto().getId());
            imagen.setOrden(maxOrden != null ? maxOrden + 1 : 0);
        }

        ImagenProducto guardada = imagenRepository.save(imagen);
        logger.info("Imagen agregada con éxito. Producto ID: {}", imagen.getProducto().getId());
        return guardada;
    }

    // ------------------ REORDENAR ------------------
    @Transactional
    public void reordenar(Integer id, Integer nuevoOrden) {
        ImagenProducto imagen = buscar(id);
        if (imagen == null) {
            throw new IllegalArgumentException("Imagen no encontrada.");
        }

        imagen.setOrden(nuevoOrden);
        imagenRepository.save(imagen);
        logger.info("Orden de imagen actualizado. ID: {}, Nuevo orden: {}", id, nuevoOrden);
    }

    // ------------------ BUSCAR POR PRODUCTO ------------------
    public List<ImagenProducto> buscarPorProducto(Integer productoId) {
        return imagenRepository.findByProductoIdOrderByOrdenAsc(productoId);
    }

    // ------------------ BUSCAR IMAGEN PRINCIPAL ------------------
    public ImagenProducto buscarImagenPrincipal(Integer productoId) {
        List<ImagenProducto> imagenes = buscarPorProducto(productoId);
        return imagenes.isEmpty() ? null : imagenes.get(0); // La primera es la principal
    }

    // ------------------ MODIFICAR ------------------
    @Transactional
    @Override
    public ImagenProducto modificar(ImagenProducto imagen) {
        if (imagen.getId() == null) {
            throw new IllegalArgumentException("Se requiere un ID para modificar la imagen.");
        }

        ImagenProducto existente = imagenRepository.findById(imagen.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe una imagen con ID " + imagen.getId()));

        existente.setUrlImagen(imagen.getUrlImagen());
        existente.setOrden(imagen.getOrden());

        ImagenProducto actualizada = imagenRepository.save(existente);
        logger.info("Imagen modificada con éxito. ID: {}", actualizada.getId());
        return actualizada;
    }

    // ------------------ BUSCAR ------------------
    @Override
    public ImagenProducto buscar(Integer id) {
        ImagenProducto imagen = imagenRepository.findById(id).orElse(null);
        if (imagen == null) {
            logger.warn("No se encontró imagen con ID: {}", id);
        }
        return imagen;
    }

    // ------------------ ELIMINAR ------------------
    @Transactional
    @Override
    public void eliminar(Integer id) {
        if (!imagenRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una imagen con ID " + id);
        }
        imagenRepository.deleteById(id);
        logger.info("Imagen eliminada con ID: {}", id);
    }

    // ------------------ LISTAR ------------------
    @Override
    public List<ImagenProducto> listar() {
        List<ImagenProducto> imagenes = imagenRepository.findAll();
        logger.info("Se listaron {} imágenes.", imagenes.size());
        return imagenes;
    }
}