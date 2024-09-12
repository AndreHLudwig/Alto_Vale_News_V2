package com.ajmv.altoValeNewsBackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "assinatura")
@Entity(name = "assinatura")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="assinaturaId")
public class Assinatura {

    @Id
    private Integer assinaturaId;

    private LocalDateTime vencimento;
    private boolean ativo;

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
