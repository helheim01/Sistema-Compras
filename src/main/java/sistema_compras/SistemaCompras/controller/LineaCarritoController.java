package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        if (lineaCarrito == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(lineaCarrito);
    }

    @PostMapping("/agregar")
    public ResponseEntity<LineaCarrito>agregar(@RequestBody LineaCarrito lineaCarrito){
        LineaCarrito lineaCarritoAgregada=lineaCarritoService.agregar(lineaCarrito);
        return ResponseEntity.status(HttpStatus.CREATED).body(lineaCarritoAgregada);
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

    @GetMapping("/carrito/{carritoId}")
    public ResponseEntity<List<LineaCarrito>> buscarPorCarrito(@PathVariable Integer carritoId) {
        return ResponseEntity.ok(lineaCarritoService.buscarPorCarrito(carritoId));
    }

    @PatchMapping("/actualizar-cantidad/{id}")
    public ResponseEntity<Void> actualizarCantidad(@PathVariable Integer id,
                                                   @RequestParam Integer nuevaCantidad) {
        lineaCarritoService.actualizarCantidad(id, nuevaCantidad);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<LineaCarrito>> buscarPorProducto(@PathVariable Integer productoId) {
        return ResponseEntity.ok(lineaCarritoService.buscarPorProducto(productoId));
    }

    @GetMapping("/buscar/carrito/{carritoId}/producto/{productoId}")
    public ResponseEntity<LineaCarrito> buscarPorCarritoYProducto(@PathVariable Integer carritoId,
                                                                  @PathVariable Integer productoId) {
        return lineaCarritoService.buscarPorCarritoYProducto(carritoId, productoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/eliminar/carrito/{carritoId}")
    public ResponseEntity<Void> eliminarPorCarrito(@PathVariable Integer carritoId) {
        lineaCarritoService.eliminarPorCarrito(carritoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/productos-mas-agregados")
    public ResponseEntity<List<Object[]>> obtenerProductosMasAgregados() {
        return ResponseEntity.ok(lineaCarritoService.obtenerProductosMasAgregados());
    }
}
