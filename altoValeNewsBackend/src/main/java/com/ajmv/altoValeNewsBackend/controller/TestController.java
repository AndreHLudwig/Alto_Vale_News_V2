package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.scheduler.AssinaturaVerificador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private AssinaturaVerificador assinaturaVerificador;

    @PostMapping("/verificar-assinaturas")
    public ResponseEntity<String> testarVerificacaoAssinaturas() {
        assinaturaVerificador.executarVerificacaoManual();
        return ResponseEntity.ok("Verificação de assinaturas executada manualmente");
    }
}