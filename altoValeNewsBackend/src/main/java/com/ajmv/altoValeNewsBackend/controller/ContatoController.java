package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.Contato;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("contato")
public class ContatoController {
    @PostMapping
    public ResponseEntity<Contato> enviarContato(@RequestBody Contato dadosContato) throws Exception{
        try {
            //TODO -logica email
            return ResponseEntity.ok(dadosContato);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
