package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.PuntosRecompensa;
import sistema_compras.SistemaCompras.service.PuntosRecompensaService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/puntosRecompensa")
public class PuntosRecompensaController {

    @Autowired
    private PuntosRecompensaService puntosRecompensaService;

    @GetMapping("/listar")
    public ResponseEntity<List<PuntosRecompensa>>listar(){
        List<PuntosRecompensa>listarPuntosRecompensa=puntosRecompensaService.listar();
        return ResponseEntity.ok(listarPuntosRecompensa);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<PuntosRecompensa>buscar(@PathVariable Integer id){
        PuntosRecompensa puntosRecompensa=puntosRecompensaService.buscar(id);
        return ResponseEntity.ok(puntosRecompensa);
    }

    @PostMapping("/agregar")
    public ResponseEntity<PuntosRecompensa>agregar(@RequestBody PuntosRecompensa puntosRecompensa){
        PuntosRecompensa puntosRecompensaAgregado=puntosRecompensaService.agregar(puntosRecompensa);
        return ResponseEntity.ok(puntosRecompensaAgregado);
    }

    @PutMapping("/modificar")
    public ResponseEntity<PuntosRecompensa>modificar(@RequestBody PuntosRecompensa puntosRecompensa){
        PuntosRecompensa puntosRecompensaModificado=puntosRecompensaService.modificar(puntosRecompensa);
        return ResponseEntity.ok(puntosRecompensaModificado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Integer id){
        puntosRecompensaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
