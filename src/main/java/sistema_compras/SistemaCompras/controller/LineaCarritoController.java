package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.LineaCarrito;
import sistema_compras.SistemaCompras.service.LineaCarritoService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/lineaCarrito")
public class LineaCarritoController {

    @Autowired
    private LineaCarritoService lineaCarritoService;

    @GetMapping("/listar")
    public ResponseEntity<List<LineaCarrito>>listar(){
        List<LineaCarrito>listaLineaCarrito=lineaCarritoService.listar();
        return ResponseEntity.ok(listaLineaCarrito);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<LineaCarrito>buscar(@PathVariable Integer id){
        LineaCarrito lineaCarrito=lineaCarritoService.buscar(id);
        return ResponseEntity.ok(lineaCarrito);
    }

    @PostMapping("/agregar")
    public ResponseEntity<LineaCarrito>agregar(@RequestBody LineaCarrito lineaCarrito){
        LineaCarrito lineaCarritoAgreada=lineaCarritoService.agregar(lineaCarrito);
        return ResponseEntity.ok(lineaCarritoAgreada);
    }

    @PutMapping("/modificar")
    public ResponseEntity<LineaCarrito>modificar(@RequestBody LineaCarrito lineaCarrito){
        LineaCarrito lineaCarritoModificada=lineaCarritoService.modificar(lineaCarrito);
        return ResponseEntity.ok(lineaCarritoModificada);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Integer id){
        lineaCarritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
