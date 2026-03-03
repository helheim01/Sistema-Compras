package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.Pedido;
import sistema_compras.SistemaCompras.service.PedidoService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/listar")
    public ResponseEntity<List<Pedido>>lista(){
        List<Pedido> listaPedido=pedidoService.listar();
        return ResponseEntity.ok(listaPedido);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Pedido>buscar(@PathVariable Integer id){
        Pedido pedido=pedidoService.buscar(id);
        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Pedido>agregar(@RequestBody Pedido pedido){
        Pedido pedidoAgregado=pedidoService.agregar(pedido);
        return ResponseEntity.ok(pedidoAgregado);
    }

    @PutMapping("/modificar")
    public ResponseEntity<Pedido>modificar(@RequestBody Pedido pedido){
        Pedido pedidoModificado=pedidoService.modificar(pedido);
        return ResponseEntity.ok(pedidoModificado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Integer id){
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
