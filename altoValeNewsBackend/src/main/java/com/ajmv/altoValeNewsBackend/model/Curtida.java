package com.ajmv.altoValeNewsBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "curtida")
@Entity(name = "curtida")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "curtidaId")
public class Curtida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer curtidaId;
    @ManyToOne
    @JoinColumn(name = "comentario_id")
    @JsonIgnore
    private Comentario comentario;
    @ManyToOne
    @JoinColumn(name = "publicacao_id")
    @JsonIgnore
    private Publicacao publicacao;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @PrePersist
    @PreUpdate
    private void validateCurtida() {
        if ((comentario != null && publicacao != null) || (comentario == null && publicacao == null)) {
            throw new IllegalStateException("Curtida deve estar associada apenas a um comentário ou a uma publicação.");
        }
    }

    public Integer getCurtidaId() {
        return curtidaId;
    }

    public void setCurtidaId(Integer curtidaId) {
        this.curtidaId = curtidaId;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }

    public Publicacao getPublicacao() {
        return publicacao;
    }

    public void setPublicacao(Publicacao publicacao) {
        this.publicacao = publicacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // TODO - Implementar serialização dos objetos ignorados no Json com o Jackson para substituir os métodos abaixo
    /*
    // Método auxiliar para obter informações do usuário
    public Map<String, Object> getUsuarioInfo() {
        if (usuario != null) {
            Map<String, Object> usuarioInfo = new HashMap<>();
            usuarioInfo.put("id", usuario.getUserId());
            usuarioInfo.put("nome", usuario.getNome());
            // Adicionar outros campos necessários
            return usuarioInfo;
        }
        return null;
    }

    // Método auxiliar para obter informações do comentário
    public Map<String, Object> getComentarioInfo() {
        if (comentario != null) {
            Map<String, Object> comentarioInfo = new HashMap<>();
            comentarioInfo.put("id", comentario.getComentarioId());
            comentarioInfo.put("texto", comentario.getTexto());
            // Adicionar outros campos necessários
            return comentarioInfo;
        }
        return null;
    }

    // Método auxiliar para obter informações da publicação
    public Map<String, Object> getPublicacaoInfo() {
        if (publicacao != null) {
            Map<String, Object> publicacaoInfo = new HashMap<>();
            publicacaoInfo.put("id", publicacao.getPublicacaoId());
            publicacaoInfo.put("titulo", publicacao.getTitulo());
            // Adicionar outros campos necessários
            return publicacaoInfo;
        }
        return null;
    }
    */
}
