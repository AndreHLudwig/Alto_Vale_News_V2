package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.Comentario;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comentario")
public class ComentarioController {

    private final ComentarioService comentarioService;

    @Autowired
    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public List<Comentario> getAll() {
        return comentarioService.getAllComentarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> getById(@PathVariable Integer id) {
        return comentarioService.getComentarioById(id);
    }

    @PostMapping
    public ResponseEntity<Comentario> create(@RequestBody Comentario comentario) {
        return comentarioService.createComentario(comentario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComentario(@PathVariable Integer id) {
        return comentarioService.deleteComentario(id);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Comentario> likeComentario(@PathVariable Integer id, @RequestBody Usuario usuarioLogado) {
        return comentarioService.like(id, usuarioLogado);
    }

    @DeleteMapping("/{id}/unlike")
    public ResponseEntity<Comentario> unlikeComentario(@PathVariable Integer id, @RequestBody Usuario usuarioLogado) {
        return comentarioService.unlike(id, usuarioLogado);
    }
}
