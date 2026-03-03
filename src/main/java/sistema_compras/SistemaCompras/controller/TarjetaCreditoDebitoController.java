package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.TarjetaCreditoDebito;
import sistema_compras.SistemaCompras.service.TarjetaCreditoDebitoService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/tarjetaCreditoDebito")
public class TarjetaCreditoDebitoController {

    @Autowired
    private TarjetaCreditoDebitoService tarjetaCreditoDebitoService;

    @GetMapping("/listar")
    public ResponseEntity<List<TarjetaCreditoDebito>>listar(){
        List <TarjetaCreditoDebito> listarTarjetaCreditoDebitos=tarjetaCreditoDebitoService.listar();
        return ResponseEntity.ok(listarTarjetaCreditoDebitos);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<TarjetaCreditoDebito>buscar(@PathVariable Integer id ){
        TarjetaCreditoDebito creditoDebito=tarjetaCreditoDebitoService.buscar(id);
        return ResponseEntity.ok(creditoDebito);
    }

    @PostMapping("/agregar")
    public ResponseEntity<TarjetaCreditoDebito>agregar(@RequestBody TarjetaCreditoDebito tarjetaCreditoDebito){
        TarjetaCreditoDebito tarjetaCreditoDebitoAgregada=tarjetaCreditoDebitoService.agregar(tarjetaCreditoDebito);
        return ResponseEntity.ok(tarjetaCreditoDebitoAgregada);
    }

    @PutMapping("/modificar")
    public ResponseEntity<TarjetaCreditoDebito>modificar(@RequestBody TarjetaCreditoDebito tarjetaCreditoDebito){
        TarjetaCreditoDebito tarjetaCreditoDebitoModificada=tarjetaCreditoDebitoService.modificar(tarjetaCreditoDebito);
        return ResponseEntity.ok(tarjetaCreditoDebitoModificada);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Integer id){
        tarjetaCreditoDebitoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
