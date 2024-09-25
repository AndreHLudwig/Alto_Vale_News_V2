package com.ajmv.altoValeNewsBackend.repository;

import com.ajmv.altoValeNewsBackend.model.Comentario;
import com.ajmv.altoValeNewsBackend.model.Curtida;
import com.ajmv.altoValeNewsBackend.model.Publicacao;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurtidaRepository extends JpaRepository<Curtida, Integer> {

    Optional<Curtida> findByComentarioAndUsuario(Comentario comentario, Usuario usuarioLogado);
    Optional<Curtida> findByPublicacaoAndUsuario(Publicacao publicacao, Usuario usuarioLogado);
}
