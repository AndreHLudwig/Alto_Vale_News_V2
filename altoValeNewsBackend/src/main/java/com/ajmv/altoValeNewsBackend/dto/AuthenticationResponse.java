package com.ajmv.altoValeNewsBackend.dto;

import com.ajmv.altoValeNewsBackend.model.Usuario;

public class AuthenticationResponse {
    private final String jwt;
    private final Usuario usuario;

    public AuthenticationResponse(String jwt, Usuario usuario) {
        this.jwt = jwt;
        this.usuario = usuario;
    }

    public String getJwt() {
        return jwt;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
