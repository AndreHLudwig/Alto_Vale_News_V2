package com.ajmv.altoValeNewsBackend.repository;

import com.ajmv.altoValeNewsBackend.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {
    List<Comentario> findByPublicacao_PublicacaoId(Integer publicacaoId);
}