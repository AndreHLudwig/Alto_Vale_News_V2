package com.ajmv.altoValeNewsBackend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PublicacaoTest {

//Testes Unitários

//Testes título
    @Test
    void setTituloValido() {
        Publicacao pub = new Publicacao();
        String titulo = "Título válido com mais de 15";
        assertDoesNotThrow(() -> pub.setTitulo(titulo));
        assertEquals(titulo, pub.getTitulo());
    }

    @Test
    void setTituloNulo() {
        Publicacao pub = new Publicacao();
        assertThrows(IllegalArgumentException.class, () -> pub.setTitulo(null));
    }

    @Test
    void setTituloVazioOuEspaco() {
        Publicacao pub = new Publicacao();
        assertThrows(IllegalArgumentException.class, () -> pub.setTitulo(" "));
    }

    @Test
    void setTituloSeMenorQue15() {
        Publicacao pub = new Publicacao();
        assertThrows(IllegalArgumentException.class, () -> pub.setTitulo("Titulo curto"));
    }

    @Test
    void setTituloDeveMaiorQue150() {
        Publicacao pub = new Publicacao();
        String longTitulo = "a".repeat(151);
        assertThrows(IllegalArgumentException.class, () -> pub.setTitulo(longTitulo));
    }

    //Teste Texto
    @Test
    void setTextoValido() {
        Publicacao pub = new Publicacao();
        String textoValido = "a".repeat(500);
        assertDoesNotThrow(() -> pub.setTexto(textoValido));
        assertEquals(textoValido, pub.getTexto());
    }

    @Test
    void setTextoNulo() {
        Publicacao pub = new Publicacao();
        assertThrows(IllegalArgumentException.class, () -> pub.setTexto(null));
    }

    @Test
    void setTextoVazioOuEspaco() {
        Publicacao pub = new Publicacao();
        assertThrows(IllegalArgumentException.class, () -> pub.setTexto("   "));
    }

    @Test
    void setTextoMenorQue500() {
        Publicacao pub = new Publicacao();
        String textoCurto = "a".repeat(499);
        assertThrows(IllegalArgumentException.class, () -> pub.setTexto(textoCurto));
    }


    //Teste visibilidade VIP
    @Test
    void setVisibilidadeVipComoTrue() {
        Publicacao publicacao = new Publicacao();
        publicacao.setVisibilidadeVip(true);

        assertTrue(publicacao.getVisibilidadeVip());
    }

    @Test
    void setVisibilidadeVipComoFalse() {
        Publicacao publicacao = new Publicacao();
        publicacao.setVisibilidadeVip(false);

        assertFalse(publicacao.getVisibilidadeVip());
    }




}