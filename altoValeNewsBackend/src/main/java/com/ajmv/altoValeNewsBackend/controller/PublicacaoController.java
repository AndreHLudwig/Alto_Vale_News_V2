package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.*;
import com.ajmv.altoValeNewsBackend.service.PublicacaoService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("publicacao")
public class PublicacaoController {

    private final PublicacaoService publicacaoService;

    private static final Logger LOGGER = Logger.getLogger(PublicacaoController.class.getName());

    @Autowired
    public PublicacaoController(PublicacaoService publicacaoService) {
        this.publicacaoService = publicacaoService;
    }

    @GetMapping
    public List<Publicacao> getAll() {
        return publicacaoService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publicacao> getById(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer usuarioId
    ) {
        return publicacaoService.getPublicacao(id, usuarioId);
    }

    @PostMapping
    public ResponseEntity<Publicacao> savePublicacao(
            @RequestParam("editorId") Integer editorId,
            @RequestParam("titulo") String titulo,
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data,
            @RequestParam("texto") String texto,
            @RequestParam(value = "imagem", required = false) MultipartFile imageFile,
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @RequestParam("categorias") List<String> categoriasNomes,
            @RequestParam("visibilidadeVip") Boolean visibilidadeVip) {
        try {
            Publicacao publicacao = publicacaoService.savePublicacao(editorId, titulo, data, texto, imageFile, videoFile, categoriasNomes, visibilidadeVip);
            return ResponseEntity.ok(publicacao);
        } catch (IOException e) {
            LOGGER.severe("IOException while saving publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publicacao> updatePublicacao(
            @PathVariable Integer id,
            @RequestParam("publicacao") String publicacaoJsonString,
            @RequestParam(value = "imagem", required = false) MultipartFile imageFile,
            @RequestParam(value = "video", required = false) MultipartFile videoFile) {
        try {
            JSONObject publicacaoJson = new JSONObject(publicacaoJsonString);
            Publicacao publicacao = publicacaoService.updatePublicacao(id, publicacaoJson, imageFile, videoFile);
            return ResponseEntity.ok(publicacao);
        } catch (IOException e) {
            LOGGER.severe("IOException while updating publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Publicacao> partialUpdatePublicacao(
            @PathVariable Integer id,
            @RequestParam(required = false) Optional<Integer> editorId,
            @RequestParam(required = false) Optional<String> titulo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> data,
            @RequestParam(required = false) Optional<String> texto,
            @RequestParam(value = "imagem", required = false) Optional<MultipartFile> imageFile,
            @RequestParam(value = "video", required = false) Optional<MultipartFile> videoFile,
            @RequestParam(required = false) Optional<List<String>> categoriasNomes,
            @RequestParam(required = false) Optional<Boolean> visibilidadeVip) {
        try {
            Publicacao publicacao = publicacaoService.partialUpdatePublicacao(id, editorId, titulo, data, texto,
                    imageFile, videoFile, categoriasNomes, visibilidadeVip);
            return ResponseEntity.ok(publicacao);
        } catch (IOException e) {
            LOGGER.severe("IOException while partially updating publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePublicacao(@PathVariable Integer id) {
        try {
            boolean deleted = publicacaoService.deletePublicacao(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            LOGGER.severe("Error deleting publicacao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{publicacaoId}/like")
    public ResponseEntity<Publicacao> likePublicacao(@PathVariable("publicacaoId") Integer publicacaoId, @RequestParam Integer usuarioId) {
        return publicacaoService.like(publicacaoId, usuarioId);
    }

    @DeleteMapping("/{publicacaoId}/like")
    public ResponseEntity<Publicacao> unlikePublicacao(@PathVariable("publicacaoId") Integer publicacaoId, @RequestParam Integer usuarioId) {
        return publicacaoService.unlike(publicacaoId, usuarioId);
    }
}