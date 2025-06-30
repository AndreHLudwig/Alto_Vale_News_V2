package com.ajmv.altoValeNewsBackend.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "publicacao")
@Entity(name = "publicacao")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "publicacaoId")
public class Publicacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer publicacaoId;
    @ManyToOne
    @JoinColumn(name = "editor_id", nullable = false)
    @JsonIgnoreProperties("publicacoes")
    private Usuario editor;

    private String titulo;
    private LocalDateTime data;
    private String texto;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) @JoinColumn(name = "imagem_id")
    @JsonIgnoreProperties("publicacao")
    private MediaFile imagem;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) @JoinColumn(name = "video_id")
    @JsonIgnoreProperties("publicacao")
    private MediaFile video;

    @ManyToMany(fetch = FetchType.EAGER) @JoinTable(
            name = "categoria_publicacao",
            joinColumns = @JoinColumn(name = "publicacao_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @JsonIgnoreProperties("publicacoes")
    private List<Categoria> categorias;

    private Boolean visibilidadeVip;
    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Curtida> curtidas;

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    @Transient
    private boolean likedByUser;

    public Integer getPublicacaoId() {
        return publicacaoId;
    }

    public void setPublicacaoId(Integer publicacaoId) {
        this.publicacaoId = publicacaoId;
    }

    public Usuario getEditor() {
        return editor;
    }

    public void setEditor(Usuario editor) {
        this.editor = editor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode ser vazio.");
        }
        if (titulo.length() < 15 || titulo.length() > 150) {
            throw new IllegalArgumentException("Título deve ter entre 15 e 150 caracteres.");
        }
        this.titulo = titulo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Texto não pode ser vazio.");
        }
        if (texto.length() > 250) {
            throw new IllegalArgumentException("Texto deve ter no máximo 250 caracteres.");
        }
        this.texto = texto;
    }

    public MediaFile getImagem() {
        return imagem;
    }

    public void setImagem(MediaFile imagem) {
        this.imagem = imagem;
    }

    public MediaFile getVideo() {
        return video;
    }

    public void setVideo(MediaFile video) {
        this.video = video;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public Boolean isVisibilidadeVip() {
        return visibilidadeVip;
    }


    public Boolean getVisibilidadeVip() {
        return visibilidadeVip;
    }

    public void setVisibilidadeVip(Boolean visibilidadeVip) {
        this.visibilidadeVip = visibilidadeVip;
    }

    public List<Curtida> getCurtidas() {
        return curtidas;
    }

    public void setCurtidas(List<Curtida> curtidas) {
        this.curtidas = curtidas;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }







}
