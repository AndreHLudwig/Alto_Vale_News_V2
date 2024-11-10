package com.ajmv.altoValeNewsBackend.controller;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("usuario") // localhost:8080/usuario
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioService.getAllUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Integer id) {
        return usuarioService.getUsuarioById(id);
    }

    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody Usuario novoUsuario) {
        return usuarioService.createUsuario(novoUsuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Integer id) {
        return usuarioService.deleteUsuario(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioAtualizado) {
        return usuarioService.updateUsuario(id, usuarioAtualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Usuario> partialUpdateUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioAtualizado) {
        return usuarioService.partialUpdateUsuario(id, usuarioAtualizado);
    }

    @PutMapping("/{id}/tipo")
    public ResponseEntity<Usuario> setTipoUsuario(@PathVariable Integer id, @Valid @RequestParam @Min(0) @Max(3) Integer tipoUsuario, @RequestHeader("Admin-Id") Integer adm) {
        return usuarioService.setTipoUsuario(id, tipoUsuario, adm);
    }



}
