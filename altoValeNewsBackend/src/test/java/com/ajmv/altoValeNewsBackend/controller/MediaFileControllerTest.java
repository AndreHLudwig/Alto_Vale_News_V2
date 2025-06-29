package com.ajmv.altoValeNewsBackend.controller;

import org.junit.jupiter.api.Test;

import static com.ajmv.altoValeNewsBackend.controller.MediaFileController.sanitizeFileName;
import static org.junit.jupiter.api.Assertions.*;

class MediaFileControllerTest {

//Testes Unitários
    @Test
    void ct10_sanitizerNulo() {
        assertEquals("", sanitizeFileName(null));
    }


    @Test
    void ct11_removerCaracteresNaoAscii() {
        String original = "João";
        String esperado = "Joao";
        String resultado = sanitizeFileName(original);
        assertEquals(esperado, resultado);
    }


    @Test
    void ct12_trocarEspacosPorUnderscore() {
        String original = "arquivo com    espacos";
        String esperado = "arquivo_com_espacos";
        String resultado = sanitizeFileName(original);
        assertEquals(esperado, resultado);
    }



    @Test
    void ct13_codificarCaracteresProibidos() {
        String original = "teste @#$% .formato";
        String esperado = "teste_%40%23%24%25_.formato";
        String resultado = sanitizeFileName(original);
        assertEquals(esperado, resultado);
    }

}