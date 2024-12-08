package com.ajmv.altoValeNewsBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Table(name = "comentario")
@Entity(name = "comentario")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="comentarioId")
public class Comentario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer comentarioId;
    @ManyToOne @JoinColumn(name = "publicacao_id") @JsonIgnore
    private Publicacao publicacao;
    @Transient
    private Integer publicacaoId;
    @ManyToOne @JoinColumn(name = "user_id")
    private Usuario usuario;

    private LocalDate data;
    private String texto;

    @OneToMany(mappedBy = "comentario", fetch = FetchType.LAZY)
    private List<Curtida> curtidas;

    @Transient
    private boolean likedByUser;

    @PrePersist
    protected void onCreate() {
        this.data = LocalDate.now();
    }

    public Integer getComentarioId() {
        return comentarioId;
    }

    public void setComentarioId(Integer comentarioId) {
        this.comentarioId = comentarioId;
    }

    public Publicacao getPublicacao() {
        return publicacao;
    }

    public void setPublicacao(Publicacao publicacao) {
        this.publicacao = publicacao;
    }

    public Integer getPublicacaoId() {
        return publicacao != null ? publicacao.getPublicacaoId() : publicacaoId;
    }

    public void setPublicacaoId(Integer publicacaoId) {
        this.publicacaoId = publicacaoId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public List<Curtida> getCurtidas() {
        return curtidas;
    }

    public void setCurtidas(List<Curtida> curtidas) {
        this.curtidas = curtidas;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }
}

