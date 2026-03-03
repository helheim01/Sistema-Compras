package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.Pago;
import sistema_compras.SistemaCompras.service.PagoService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/listar")
    public ResponseEntity<List<Pago>>listar(){
        List <Pago> listaPago=pagoService.listar();
        return ResponseEntity.ok(listaPago);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Pago>buscar(@PathVariable Integer id){
        Pago pago=pagoService.buscar(id);
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
}
