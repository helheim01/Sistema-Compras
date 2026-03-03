package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.UsuarioWeb;
import sistema_compras.SistemaCompras.service.UsuarioWebService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/usuarioWeb")
public class UsuarioWebController {

    @Autowired
    private UsuarioWebService usuarioWebService;

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioWeb>>listar(){
        List<UsuarioWeb>listaUsuarioWeb=usuarioWebService.listar();
        return ResponseEntity.ok(listaUsuarioWeb);
    }

    @GetMapping("/buscar")
    public ResponseEntity<UsuarioWeb>buscar(@PathVariable Integer id){
        UsuarioWeb usuarioWeb=usuarioWebService.buscar(id);
        return ResponseEntity.ok(usuarioWeb);
    }

    @PostMapping("/agregar")
    public ResponseEntity<UsuarioWeb>agregar(@RequestBody UsuarioWeb usuarioWeb){
        UsuarioWeb usuarioWebAgregado=usuarioWebService.agregar(usuarioWeb);
        return ResponseEntity.ok(usuarioWebAgregado);
    }

    @PutMapping("/modificar")
    public ResponseEntity<UsuarioWeb>modificar(@RequestBody UsuarioWeb usuarioWeb){
        UsuarioWeb usuarioWebModificado=usuarioWebService.modificar(usuarioWeb);
        return ResponseEntity.ok(usuarioWebModificado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<UsuarioWeb>eliminar(@PathVariable Integer id){
        usuarioWebService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
