package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<ImagenProducto>buscar(@PathVariable Integer id){
        ImagenProducto imagenProducto=imagenProductoService.buscar(id);
        return ResponseEntity.ok(imagenProducto);
    }

    @PostMapping("/agregar")
    public ResponseEntity<ImagenProducto>agregar(@RequestBody ImagenProducto imagenProducto){
        ImagenProducto imagenProductoAgregada=imagenProductoService.agregar(imagenProducto);
        return ResponseEntity.ok(imagenProductoAgregada);
    }

    @PutMapping("/modificiar")
    public ResponseEntity<ImagenProducto>modificar(@RequestBody ImagenProducto imagenProducto){
        ImagenProducto imagenProductoModificada=imagenProductoService.modificar(imagenProducto);
        return ResponseEntity.ok(imagenProductoModificada);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Integer id){
        imagenProductoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
