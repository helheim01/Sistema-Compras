package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.EstadoPago;
import sistema_compras.SistemaCompras.entity.MetodoPago;
import sistema_compras.SistemaCompras.entity.Pago;
import sistema_compras.SistemaCompras.entity.TipoPago;
import sistema_compras.SistemaCompras.service.PagoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequestMapping("/pago")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/listar")
    public ResponseEntity<List<Pago>>listar(){
        List <Pago> listaPago=pagoService.listar();
        return ResponseEntity.ok(listaPago);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Pago> buscar(@PathVariable Integer id) {
        Pago pago = pagoService.buscar(id);
        if (pago == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(pago);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Pago>agregar(@RequestBody Pago pago){
        Pago pagoAgregado=pagoService.agregar(pago);
        return ResponseEntity.ok(pagoAgregado);
    }

    @PutMapping("/modificar")
    public ResponseEntity<Pago>modificar(@RequestBody Pago pago){
        Pago pagoModificado=pagoService.modificar(pago);
        return ResponseEntity.ok(pagoModificado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id){
        pagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar/codigo/{codigo}")
    public ResponseEntity<Pago> buscarPorCodigo(@PathVariable String codigo) {
        return pagoService.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<Pago>> buscarPorCuenta(@PathVariable Integer cuentaId) {
        return ResponseEntity.ok(pagoService.buscarPorCuenta(cuentaId));
    }

    @GetMapping("/tipo/{tipoPago}")
    public ResponseEntity<List<Pago>> buscarPorTipo(@PathVariable TipoPago tipoPago) {
        return ResponseEntity.ok(pagoService.buscarPorTipo(tipoPago));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pago>> buscarPorEstado(@PathVariable EstadoPago estado) {
        return ResponseEntity.ok(pagoService.buscarPorEstado(estado));
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<Pago>> buscarPorRangoFechas(@RequestParam LocalDateTime inicio,
                                                           @RequestParam LocalDateTime fin) {
        return ResponseEntity.ok(pagoService.buscarPorRangoFechas(inicio, fin));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pago>> buscarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(pagoService.buscarPorCliente(clienteId));
    }

    @GetMapping("/ingresos")
    public ResponseEntity<BigDecimal> calcularIngresos(@RequestParam LocalDateTime inicio,
                                                       @RequestParam LocalDateTime fin) {
        return ResponseEntity.ok(pagoService.calcularIngresosPorPeriodo(inicio, fin));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<List<Object[]>> obtenerEstadisticas() {
        return ResponseEntity.ok(pagoService.obtenerEstadisticasPorTipo());
    }

    @PostMapping("/procesar/{pedidoId}")
    public ResponseEntity<Pago> procesarPago(@PathVariable Integer pedidoId,
                                             @RequestParam TipoPago tipoPago,
                                             @RequestBody MetodoPago metodoPago) {
        return ResponseEntity.ok(pagoService.procesarPago(pedidoId, tipoPago, metodoPago));
    }

    @PostMapping("/reembolsar/{id}")
    public ResponseEntity<Void> reembolsar(@PathVariable Integer id) {
        pagoService.reembolsar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validar/{id}")
    public ResponseEntity<Boolean> validarPago(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoService.validarPago(id));
    }
}
