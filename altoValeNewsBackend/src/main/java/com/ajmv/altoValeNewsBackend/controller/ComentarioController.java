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

    @PostMapping("/{comentarioId}/like")
    public ResponseEntity<Comentario> likeComentario(@PathVariable("comentarioId") Integer comentarioId, @RequestParam Integer usuarioId) {
        return comentarioService.like(comentarioId, usuarioId);
    }

    @DeleteMapping("/{comentarioId}/like")
    public ResponseEntity<Comentario> unlikeComentario(@PathVariable("comentarioId") Integer comentarioId, @RequestParam Integer usuarioId) {
        return comentarioService.unlike(comentarioId, usuarioId);
    }
}
