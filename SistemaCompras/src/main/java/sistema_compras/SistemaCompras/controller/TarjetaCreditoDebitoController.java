package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.TarjetaCreditoDebito;
import sistema_compras.SistemaCompras.entity.TipoTarjeta;
import sistema_compras.SistemaCompras.service.TarjetaCreditoDebitoService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/tarjetaCreditoDebito")
public class TarjetaCreditoDebitoController {

    @Autowired
    private TarjetaCreditoDebitoService tarjetaCreditoDebitoService;

    // ------------------ LISTAR ------------------
    @GetMapping("/listar")
    public ResponseEntity<List<TarjetaCreditoDebito>> listar() {
        List<TarjetaCreditoDebito> lista = tarjetaCreditoDebitoService.listar();
        return ResponseEntity.ok(lista);
    }

    // ------------------ BUSCAR ------------------
    @GetMapping("/buscar/{id}")
    public ResponseEntity<TarjetaCreditoDebito> buscar(@PathVariable Integer id) {
        TarjetaCreditoDebito tarjeta = tarjetaCreditoDebitoService.buscar(id);
        if (tarjeta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(tarjeta);
    }

    // ------------------ AGREGAR ------------------
    @PostMapping("/agregar")
    public ResponseEntity<TarjetaCreditoDebito> agregar(@RequestBody TarjetaCreditoDebito tarjetaCreditoDebito) {
        TarjetaCreditoDebito agregada = tarjetaCreditoDebitoService.agregar(tarjetaCreditoDebito);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregada);
    }

    // ------------------ MODIFICAR ------------------
    @PutMapping("/modificar")
    public ResponseEntity<TarjetaCreditoDebito> modificar(@RequestBody TarjetaCreditoDebito tarjetaCreditoDebito) {
        TarjetaCreditoDebito modificada = tarjetaCreditoDebitoService.modificar(tarjetaCreditoDebito);
        return ResponseEntity.ok(modificada);
    }

    // ------------------ ELIMINAR ------------------
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        tarjetaCreditoDebitoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ ACTIVAR ------------------
    @PatchMapping("/activar/{id}")
    public ResponseEntity<Void> activar(@PathVariable Integer id) {
        tarjetaCreditoDebitoService.activar(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ DESACTIVAR ------------------
    @PatchMapping("/desactivar/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Integer id) {
        tarjetaCreditoDebitoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ VERIFICAR VENCIMIENTO ------------------
    @GetMapping("/verificar-vencimiento/{id}")
    public ResponseEntity<Boolean> verificarVencimiento(@PathVariable Integer id) {
        return ResponseEntity.ok(tarjetaCreditoDebitoService.verificarVencimiento(id));
    }

    // ------------------ BUSCAR POR CLIENTE ------------------
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<TarjetaCreditoDebito>> buscarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(tarjetaCreditoDebitoService.buscarPorCliente(clienteId));
    }

    // ------------------ BUSCAR ACTIVAS POR CLIENTE ------------------
    @GetMapping("/cliente/{clienteId}/activas")
    public ResponseEntity<List<TarjetaCreditoDebito>> buscarActivasPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(tarjetaCreditoDebitoService.buscarActivasPorCliente(clienteId));
    }

    // ------------------ BUSCAR POR TIPO ------------------
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<TarjetaCreditoDebito>> buscarPorTipo(@PathVariable TipoTarjeta tipo) {
        return ResponseEntity.ok(tarjetaCreditoDebitoService.buscarPorTipo(tipo));
    }

    // ------------------ BUSCAR POR ÚLTIMOS DÍGITOS ------------------
    @GetMapping("/digitos/{ultimos4}")
    public ResponseEntity<List<TarjetaCreditoDebito>> buscarPorDigitos(@PathVariable String ultimos4) {
        return ResponseEntity.ok(tarjetaCreditoDebitoService.buscarPorUltimosDigitos(ultimos4));
    }

    // ------------------ PRÓXIMAS A VENCER ------------------
    @GetMapping("/proximas-a-vencer")
    public ResponseEntity<List<TarjetaCreditoDebito>> buscarProximasAVencer(
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(tarjetaCreditoDebitoService.buscarProximasAVencer(dias));
    }

    // ------------------ VENCIDAS ------------------
    @GetMapping("/vencidas")
    public ResponseEntity<List<TarjetaCreditoDebito>> buscarVencidas() {
        return ResponseEntity.ok(tarjetaCreditoDebitoService.buscarVencidas());
    }
}