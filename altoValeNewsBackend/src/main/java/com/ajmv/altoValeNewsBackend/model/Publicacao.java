package com.ajmv.altoValeNewsBackend.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer publicacaoId;
    @ManyToOne
    @JoinColumn(name = "editor_id")
    private Usuario editor;
    private String titulo;
    private LocalDateTime data;
    private String texto;
    @OneToOne
    @JoinColumn(name = "imagem_id")
    private MediaFile imagem;
    @OneToOne
    @JoinColumn(name = "video_id")
    private MediaFile video;
    @ManyToMany
    @JoinTable(
            name = "categoria_publicacao",
            joinColumns = @JoinColumn(name = "publicacao_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias;
    private Boolean visibilidadeVip;
    @OneToMany(mappedBy = "publicacao")
    private List<Curtida> curtidas;
    @OneToMany(mappedBy = "publicacao")
    private List<Comentario> comentarios;

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

}
