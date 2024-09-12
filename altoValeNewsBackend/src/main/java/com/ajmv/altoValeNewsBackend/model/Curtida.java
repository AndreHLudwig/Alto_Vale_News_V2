package com.ajmv.altoValeNewsBackend.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "curtida")
@Entity(name = "curtida")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="curtidaId")
public class Curtida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer curtidaId;

    @ManyToOne
    @JoinColumn(name = "comentario_id", referencedColumnName = "comentario_ID")
    private Comentario comentario;

    @ManyToOne
    @JoinColumn(name = "publicacao_id", referencedColumnName = "publicacao_ID")
    private Publicacao publicacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "user_ID")
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
}
