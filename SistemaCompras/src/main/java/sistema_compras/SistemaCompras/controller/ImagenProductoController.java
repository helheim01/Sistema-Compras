package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.ImagenProducto;
import sistema_compras.SistemaCompras.service.ImagenProductoService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/imagenProducto")
public class ImagenProductoController {

    @Autowired
    private ImagenProductoService imagenProductoService;

    @GetMapping("/listar")
    public ResponseEntity<List<ImagenProducto>>listar(){
        List<ImagenProducto> listarImagenProductos=imagenProductoService.listar();
        return ResponseEntity.ok(listarImagenProductos);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ImagenProducto> buscar(@PathVariable Integer id) {
        ImagenProducto imagen = imagenProductoService.buscar(id);
        if (imagen == null) {
            return ResponseEntity.notFound().build(); // 404 en vez de 200 con null
        }
        return ResponseEntity.ok(imagen);
    }

    @PostMapping("/agregar")
    public ResponseEntity<ImagenProducto> agregar(@RequestBody ImagenProducto imagenProducto) {
        ImagenProducto agregada = imagenProductoService.agregar(imagenProducto);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregada); // 201 en vez de 200
    }

    @PutMapping("/modificar")
    public ResponseEntity<ImagenProducto>modificar(@RequestBody ImagenProducto imagenProducto){
        ImagenProducto imagenProductoModificada=imagenProductoService.modificar(imagenProducto);
        return ResponseEntity.ok(imagenProductoModificada);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Integer id){
        imagenProductoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ImagenProducto>> buscarPorProducto(@PathVariable Integer productoId) {
        return ResponseEntity.ok(imagenProductoService.buscarPorProducto(productoId));
    }

    @GetMapping("/producto/{productoId}/principal")
    public ResponseEntity<ImagenProducto> buscarPrincipal(@PathVariable Integer productoId) {
        ImagenProducto principal = imagenProductoService.buscarImagenPrincipal(productoId);
        return principal != null ? ResponseEntity.ok(principal) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/reordenar/{id}")
    public ResponseEntity<Void> reordenar(@PathVariable Integer id, @RequestParam Integer nuevoOrden) {
        imagenProductoService.reordenar(id, nuevoOrden);
        return ResponseEntity.noContent().build();
    }

}
