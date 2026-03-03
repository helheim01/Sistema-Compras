package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.Producto;
import sistema_compras.SistemaCompras.service.ProductoService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>>listar(){
        List<Producto> listaProducto=productoService.listar();
        return ResponseEntity.ok(listaProducto);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Producto>buscar(@PathVariable Integer id){
        Producto producto=productoService.buscar(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Producto>agregar(@RequestBody Producto producto){
        Producto productoAgregado=productoService.agregar(producto);
        return ResponseEntity.ok(productoAgregado);
    }

    @PutMapping("/modificar")
    public ResponseEntity<Producto>modificar(@RequestBody Producto producto){
        Producto productoModificado=productoService.modificar(producto);
        return ResponseEntity.ok(productoModificado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Integer id){
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
