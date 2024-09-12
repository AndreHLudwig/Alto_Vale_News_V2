package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.Categoria;
import com.ajmv.altoValeNewsBackend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("categoria") // localhost:8080/categoria
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Categoria> getAll() {
        return categoriaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable Integer id) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isPresent()) {
            return ResponseEntity.ok(categoriaOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@RequestBody Categoria novaCategoria) {
        try {
            Categoria categoriaCriada = categoriaRepository.save(novaCategoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCriada);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategoria(@PathVariable Integer id) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isPresent()) {
            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(@PathVariable Integer id, @RequestBody Categoria categoriaAtualizada) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isPresent()) {
            Categoria categoriaExistente = categoriaOptional.get();
            categoriaExistente.setNome(categoriaAtualizada.getNome());

            Categoria categoriaAtualizadaSalva = categoriaRepository.save(categoriaExistente);
            return ResponseEntity.ok(categoriaAtualizadaSalva);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

   /* @PatchMapping("/{id}")
    public ResponseEntity<Categoria> partialUpdateCategoria(@PathVariable Integer id, @RequestBody Categoria categoriaAtualizada) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isPresent()) {
            Categoria categoriaExistente = categoriaOptional.get();
            if (categoriaAtualizada.getNome() != null) {
                categoriaExistente.setNome(categoriaAtualizada.getNome());
            }
            Categoria categoriaAtualizadaSalva = categoriaRepository.save(categoriaExistente);
            return ResponseEntity.ok(categoriaAtualizadaSalva);
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/
}
