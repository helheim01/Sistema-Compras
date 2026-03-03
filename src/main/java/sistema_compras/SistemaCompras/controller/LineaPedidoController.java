package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.LineaPedido;
import sistema_compras.SistemaCompras.service.LineaPedidoService;

import java.math.BigDecimal;
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
    public ResponseEntity<LineaPedido> buscar(@PathVariable Integer id) {
        LineaPedido lineaPedido = lineaPedidoService.buscar(id);
        if (lineaPedido == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(lineaPedido);
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

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<LineaPedido>> buscarPorProducto(@PathVariable Integer productoId) {
        return ResponseEntity.ok(lineaPedidoService.buscarPorProducto(productoId));
    }

//    @GetMapping("/productos-mas-vendidos")
//    public ResponseEntity<List<ProductoVendidoDTO>> obtenerProductosMasVendidos() {
//        return ResponseEntity.ok(lineaPedidoService.obtenerProductosMasVendidos());
//    }

    @GetMapping("/unidades-vendidas/{productoId}")
    public ResponseEntity<Long> contarUnidadesVendidas(@PathVariable Integer productoId) {
        return ResponseEntity.ok(lineaPedidoService.contarUnidadesVendidas(productoId));
    }

    @GetMapping("/productos-comprados-juntos")
    public ResponseEntity<List<Object[]>> obtenerProductosCompradosJuntos() {
        return ResponseEntity.ok(lineaPedidoService.obtenerProductosCompradosJuntos());
    }

    @PatchMapping("/aplicar-descuento/{id}")
    public ResponseEntity<Void> aplicarDescuento(@PathVariable Integer id,
                                                 @RequestParam BigDecimal porcentaje) {
        lineaPedidoService.aplicarDescuento(id, porcentaje);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<LineaPedido>> buscarPorPedido(@PathVariable Integer pedidoId) {
        return ResponseEntity.ok(lineaPedidoService.buscarPorPedido(pedidoId));
    }
}
