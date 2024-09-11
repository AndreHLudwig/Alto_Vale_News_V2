package com.ajmv.altoValeNewsBackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "assinatura")
@Entity(name = "assinatura")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="assinaturaId")
public class Assinatura {
    @Id
    private Integer assinaturaId;
    //TODO - atributos vencimento e ativo

    public void setAssinaturaId(Integer assinaturaId) {
        this.assinaturaId = assinaturaId;
    }

    public Integer getAssinaturaId() {
        return assinaturaId;
    }
    //TODO

}
