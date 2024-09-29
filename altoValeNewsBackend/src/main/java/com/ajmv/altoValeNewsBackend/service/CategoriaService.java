package com.ajmv.altoValeNewsBackend.service;

import com.ajmv.altoValeNewsBackend.model.Categoria;
import com.ajmv.altoValeNewsBackend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Buscar categorias
    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }

    // Buscar categoria por ID
    public ResponseEntity<Categoria> getCategoriaById(Integer id) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        return categoria.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Criar nova categoria
    public ResponseEntity<Categoria> createCategoria(Categoria categoria) {
        try {
            Categoria categoriaCriada = categoriaRepository.save(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCriada);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Deletar categoria
    public ResponseEntity<?> deleteCategoria(Integer id) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isPresent()) {
            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Atualizar categoria por ID
    public ResponseEntity<Categoria> updateCategoria(Integer id, Categoria categoriaAtualizada) {
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

    // Atualização parcial da categoria
    public ResponseEntity<Categoria> partialUpdateCategoria(Integer id, Categoria categoriaAtualizada) {
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
    }
}
