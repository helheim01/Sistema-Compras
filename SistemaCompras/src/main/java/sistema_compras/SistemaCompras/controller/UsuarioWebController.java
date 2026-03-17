package sistema_compras.SistemaCompras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sistema_compras.SistemaCompras.entity.EstadoUsuario;
import sistema_compras.SistemaCompras.entity.UsuarioWeb;
import sistema_compras.SistemaCompras.service.UsuarioWebService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/usuarioWeb")
public class UsuarioWebController {

    @Autowired
    private UsuarioWebService usuarioWebService;

    // ------------------ LISTAR ------------------
    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioWeb>> listar() {
        return ResponseEntity.ok(usuarioWebService.listar());
    }

    // ------------------ BUSCAR ------------------
    @GetMapping("/buscar/{id}")
    public ResponseEntity<UsuarioWeb> buscar(@PathVariable Integer id) {
        UsuarioWeb usuario = usuarioWebService.buscar(id);
        if (usuario == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(usuario);
    }

    // ------------------ BUSCAR POR EMAIL ------------------
    @GetMapping("/email")
    public ResponseEntity<UsuarioWeb> buscarPorEmail(@RequestParam String email) {
        return usuarioWebService.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------ BUSCAR POR ESTADO ------------------
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<UsuarioWeb>> buscarPorEstado(@PathVariable EstadoUsuario estado) {
        return ResponseEntity.ok(usuarioWebService.buscarPorEstado(estado));
    }

    // ------------------ BUSCAR EXCLUYENDO ESTADO ------------------
    @GetMapping("/excluir-estado/{estado}")
    public ResponseEntity<List<UsuarioWeb>> buscarExcluyendoEstado(@PathVariable EstadoUsuario estado) {
        return ResponseEntity.ok(usuarioWebService.buscarExcluyendoEstado(estado));
    }

    // ------------------ AGREGAR ------------------
    @PostMapping("/agregar")
    public ResponseEntity<UsuarioWeb> agregar(@RequestBody UsuarioWeb usuarioWeb) {
        UsuarioWeb agregado = usuarioWebService.agregar(usuarioWeb);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregado);
    }

    // ------------------ MODIFICAR ------------------
    @PutMapping("/modificar")
    public ResponseEntity<UsuarioWeb> modificar(@RequestBody UsuarioWeb usuarioWeb) {
        UsuarioWeb modificado = usuarioWebService.modificar(usuarioWeb);
        return ResponseEntity.ok(modificado);
    }

    // ------------------ ELIMINAR ------------------
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioWebService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ ACTIVAR CUENTA ------------------
    @PatchMapping("/activar/{id}")
    public ResponseEntity<Void> activarCuenta(@PathVariable Integer id) {
        usuarioWebService.activarCuenta(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ BLOQUEAR CUENTA ------------------
    @PatchMapping("/bloquear/{id}")
    public ResponseEntity<Void> bloquearCuenta(@PathVariable Integer id) {
        usuarioWebService.bloquearCuenta(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ CAMBIAR CONTRASEÑA ------------------
    @PatchMapping("/cambiar-contrasena/{id}")
    public ResponseEntity<Void> cambiarContrasena(@PathVariable Integer id,
                                                  @RequestParam String antigua,
                                                  @RequestParam String nueva) {
        usuarioWebService.cambiarContrasena(id, antigua, nueva);
        return ResponseEntity.noContent().build();
    }
}