package com.ajmv.altoValeNewsBackend.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoUsuario {
    USUARIO(0),
    USUARIO_VIP(1),
    EDITOR(2),
    ADMINISTRADOR(3);

    private final Integer codigo;

    TipoUsuario(Integer codigo) {
        this.codigo = codigo;
    }

    public static TipoUsuario getTipoUsuario(Integer codigo) {
        for (TipoUsuario tipo : values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Invalid code for TipoUsuario: " + codigo);
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getNome() {
        return this.name();
    }

    @JsonValue
    public Integer toJson() {
        return codigo;
    }

}