package com.ajmv.altoValeNewsBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "assinatura")
@Entity(name = "assinatura")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "assinaturaId")
public class Assinatura {
    @Id
    @Column(name = "assinatura_id")
    private Integer assinaturaId;

    @OneToOne
    @MapsId //"Use o ID do Usuario como meu próprio ID"
    @JoinColumn(name = "assinatura_id")
    @JsonIgnore
    private Usuario usuario;

    private LocalDateTime vencimento;
    private boolean ativo;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDateTime vencimento) {
        this.vencimento = vencimento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setAssinaturaId(Integer assinaturaId) {
        this.assinaturaId = assinaturaId;
    }

    public Integer getAssinaturaId() {
        return assinaturaId;
    }

}
