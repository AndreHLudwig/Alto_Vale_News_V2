package com.ajmv.altoValeNewsBackend.service;

import org.junit.jupiter.api.Test;

import static com.ajmv.altoValeNewsBackend.service.MediaFileService.getFileType;
import static org.junit.jupiter.api.Assertions.*;

class MediaFileServiceTest {



    @Test
    void testeImageType() {
        assertEquals("images", getFileType("image/jpeg"));
    }

    @Test
    void testeVideoType() {
        assertEquals("videos", getFileType("video/mp4"));
    }

    @Test
    void testeAudioType() {
        assertEquals("audios", getFileType("audio/mpeg"));
    }

    @Test
    void testePdfType() {
        assertEquals("pdfs", getFileType("application/pdf"));
    }

    @Test
    void testeNullType() {
        assertEquals("others", getFileType(null));
    }

    @Test
    void testeDesconhecidoType() {
        assertEquals("others", getFileType("application/zip"));
    }

}