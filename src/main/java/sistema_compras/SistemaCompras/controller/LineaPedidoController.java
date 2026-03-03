package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.LineaPedido;
import sistema_compras.SistemaCompras.service.LineaPedidoService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/lineaPedido")
public class LineaPedidoController {
    @Autowired
    private LineaPedidoService lineaPedidoService;

    @GetMapping("/listar")
    public ResponseEntity<List<LineaPedido>> listar(){
        List<LineaPedido>listaLineaPedido=lineaPedidoService.listar();
        return ResponseEntity.ok(listaLineaPedido);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<LineaPedido>buscar(@PathVariable Integer id){
        LineaPedido lineaCarrito=lineaPedidoService.buscar(id);
        return ResponseEntity.ok(lineaCarrito);
    }

    @PostMapping("/agregar")
    public ResponseEntity<LineaPedido>agregar(@RequestBody LineaPedido lineaPedido){
        LineaPedido lineaPedidoAgreada=lineaPedidoService.agregar(lineaPedido);
        return ResponseEntity.ok(lineaPedidoAgreada);
    }

    @PutMapping("/modificar")
    public ResponseEntity<LineaPedido>modificar(@RequestBody LineaPedido lineaPedido){
        LineaPedido lineaPedidoModificada=lineaPedidoService.modificar(lineaPedido);
        return ResponseEntity.ok(lineaPedidoModificada);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Integer id){
        lineaPedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
