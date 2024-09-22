package com.ajmv.altoValeNewsBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Table(name = "comentario")
@Entity(name = "comentario")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="comentarioId")
public class Comentario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer comentarioId;
    @ManyToOne
    @JoinColumn(name = "publicacao_id")
    @JsonIgnore
    private Publicacao publicacao;
    @ManyToOne
    @JoinColumn(name = "user_id") // Nome da coluna que representa a chave estrangeira na tabela de Comentario
    private Usuario usuario;
    private Date data;

    private String texto;

    @OneToMany(mappedBy = "comentario", fetch = FetchType.LAZY)
//    @Transient //FIXME Hibernate está reclamando ao tentar curtir um comentario
    private List<Curtida> curtidas; //= new ArrayList<>();

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
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

}

