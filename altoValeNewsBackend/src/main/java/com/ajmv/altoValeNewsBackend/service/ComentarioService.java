package com.ajmv.altoValeNewsBackend.service;

import com.ajmv.altoValeNewsBackend.model.Comentario;
import com.ajmv.altoValeNewsBackend.model.Curtida;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.repository.ComentarioRepository;
import com.ajmv.altoValeNewsBackend.repository.CurtidaRepository;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final CurtidaRepository curtidaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ComentarioService(ComentarioRepository comentarioRepository, CurtidaRepository curtidaRepository, UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.curtidaRepository = curtidaRepository;
        this.usuarioRepository = usuarioRepository;

    }

    // Buscar todos os comentários de uma publicação
    public List<Comentario> getAllComentariosFromPublicacao(Integer publicacaoId, Integer usuarioId) {
        List<Comentario> comentarios = publicacaoId != null
                ? comentarioRepository.findByPublicacao_PublicacaoId(publicacaoId)
                : comentarioRepository.findAll();

        if (usuarioId != null) {
            comentarios.forEach(comentario -> {
                boolean isLikedByUser = comentario.getCurtidas().stream()
                        .anyMatch(curtida -> curtida.getUsuario().getUserId().equals(usuarioId));
                comentario.setLikedByUser(isLikedByUser);
            });
        }

        return comentarios;
    }

    // Buscar comentário por ID
    public ResponseEntity<Comentario> getComentario(Integer id, Integer usuarioId) {
        Optional<Comentario> comentario = comentarioRepository.findById(id);
        if (comentario.isPresent()) {
            Comentario com = comentario.get();
            if (usuarioId != null) {
                boolean isLikedByUser = com.getCurtidas().stream()
                        .anyMatch(curtida -> curtida.getUsuario().getUserId().equals(usuarioId));
                com.setLikedByUser(isLikedByUser);
            }
            return ResponseEntity.ok(com);
        }
        return ResponseEntity.notFound().build();
    }

    // Criar novo comentário
    public ResponseEntity<Comentario> createComentario(Comentario comentario) {
        try {
            Comentario comentarioCriado = comentarioRepository.save(comentario);
            return ResponseEntity.status(HttpStatus.CREATED).body(comentarioCriado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Deletar comentário
    public ResponseEntity<?> deleteComentario(Integer id) {
        try {
            Optional<Comentario> comentarioOptional = comentarioRepository.findById(id);
            if (comentarioOptional.isPresent()) {
                comentarioRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Comentario> like(Integer comentarioId, Integer usuarioId) {
        try {
            Optional<Comentario> comentarioOptional = comentarioRepository.findById(comentarioId);
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

            if (comentarioOptional.isEmpty() || usuarioOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Comentario comentario = comentarioOptional.get();
            Usuario usuario = usuarioOptional.get();

            Optional<Curtida> curtidaOptional = curtidaRepository.findByComentarioAndUsuario(comentario, usuario);
            if (curtidaOptional.isPresent()) {
                comentario.setLikedByUser(true);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(comentario);
            }

            Curtida curtida = new Curtida();
            curtida.setComentario(comentario);
            curtida.setUsuario(usuario);
            curtidaRepository.save(curtida);

            comentario = comentarioRepository.findById(comentarioId).get();
            comentario.setLikedByUser(true);

            return ResponseEntity.ok(comentario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Comentario> unlike(Integer comentarioId, Integer usuarioId) {
        try {
            Optional<Comentario> comentarioOptional = comentarioRepository.findById(comentarioId);
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

            if (comentarioOptional.isEmpty() || usuarioOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Comentario comentario = comentarioOptional.get();
            Usuario usuario = usuarioOptional.get();

            Optional<Curtida> curtidaOptional = curtidaRepository.findByComentarioAndUsuario(comentario, usuario);
            if (curtidaOptional.isPresent()) {
                curtidaRepository.delete(curtidaOptional.get());

                comentario = comentarioRepository.findById(comentarioId).get();
                comentario.setLikedByUser(false);

                return ResponseEntity.ok(comentario);
            }

            comentario.setLikedByUser(false);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(comentario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
