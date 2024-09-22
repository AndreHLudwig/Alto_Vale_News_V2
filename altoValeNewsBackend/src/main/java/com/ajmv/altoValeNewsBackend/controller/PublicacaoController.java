package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.Categoria;
import com.ajmv.altoValeNewsBackend.model.Publicacao;
import com.ajmv.altoValeNewsBackend.repository.PublicacaoRepository;
import com.ajmv.altoValeNewsBackend.service.PublicacaoService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("publicacao")
public class PublicacaoController {

    @Autowired
    private PublicacaoRepository publicacaoRepository;

    @Autowired
    private PublicacaoService publicacaoService;

    private static final Logger LOGGER = Logger.getLogger(PublicacaoController.class.getName());

    @GetMapping
    public List<Publicacao> getAll() {
        try {
            return publicacaoService.getAll();
        } catch (SQLException e) {
            LOGGER.severe("SQLException while retrieving publicacoes: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao recuperar publicações", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publicacao> getPublicacao(@PathVariable Integer id) {
        try {
            Publicacao publicacao = publicacaoService.getPublicacao(id);
            if (publicacao == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(publicacao);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Publicacao> uploadPublicacao(
            @RequestParam("editorId") Integer editorId,
            @RequestParam("titulo") String titulo,
            @RequestParam("data") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data,
            @RequestParam("texto") String texto,
            @RequestParam(value = "imagem", required = false) MultipartFile imageFile,
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @RequestParam("categorias") List<Categoria> categorias,
            @RequestParam("visibilidadeVip") Boolean visibilidadeVip/*,
            @RequestParam("curtidas") Integer curtidas*/) {
        try {
            Publicacao publicacao = publicacaoService.savePublicacao(editorId, titulo, data, texto, imageFile, videoFile, categorias, visibilidadeVip /*, curtidas*/);
            return ResponseEntity.ok(publicacao);
        } catch (IOException e) {
            LOGGER.severe("IOException while saving publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (SQLException e) {
            LOGGER.severe("SQLException while saving publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publicacao> updatePublicacao(
            @PathVariable Integer id,
            @RequestParam("publicacao") JSONObject publicacaoJson,
            @RequestParam(value = "imagem", required = false) MultipartFile imageFile,
            @RequestParam(value = "video", required = false) MultipartFile videoFile) {
        try {
            Publicacao publicacao = publicacaoService.updatePublicacao(id, publicacaoJson, imageFile, videoFile);
            return ResponseEntity.ok(publicacao);
        } catch (IOException e) {
            LOGGER.severe("IOException while updating publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (SQLException e) {
            LOGGER.severe("SQLException while updating publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Publicacao> partialUpdatePublicacao(
            @PathVariable Integer id,
            @RequestParam("editorId") Optional<Integer> editorId,
            @RequestParam("titulo") Optional<String> titulo,
            @RequestParam("data") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDateTime> data,
            @RequestParam("texto") Optional<String> texto,
            @RequestParam(value = "imagem", required = false) MultipartFile imageFile,
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @RequestParam("categorias") Optional<List<Categoria>> categorias,
            @RequestParam("visibilidadeVip") Optional<Boolean> visibilidadeVip/*,
            @RequestParam("curtidas") Optional<Integer> curtidas*/) {
        try {
            Publicacao publicacao = publicacaoService.partialUpdatePublicacao(id, editorId.orElse(null),
                    titulo.orElse(null), data.orElse(null), texto.orElse(null), imageFile,
                    videoFile, categorias.orElse(null), visibilidadeVip.orElse(null)/*, curtidas.orElse(null)*/);
            return ResponseEntity.ok(publicacao);
        } catch (IOException e) {
            LOGGER.severe("IOException while partially updating publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (SQLException e) {
            LOGGER.severe("SQLException while partially updating publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePublicacao(@PathVariable Integer id) {
        try {
            Optional<Publicacao> publicacaoOptional = publicacaoRepository.findById(id);
            if (publicacaoOptional.isPresent()) {
                publicacaoRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<Publicacao> likePublicacao(@PathVariable Integer id) {
        try {
            //TODO - refazer a lógica Service, verificando se o usuário já curtiu
            Publicacao publicacao = publicacaoService.likePublicacao(id);
            return ResponseEntity.ok(publicacao);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/{id}/dislike")
    public ResponseEntity<Publicacao> dislikePublicacao(@PathVariable Integer id) {
        try {
            //TODO - refazer a lógica no Service, verificando se o usuário já descurtiu
            Publicacao publicacao = publicacaoService.dislikePublicacao(id);
            return ResponseEntity.ok(publicacao);
        } catch (IllegalArgumentException | SQLException e) {
            LOGGER.warning("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}
