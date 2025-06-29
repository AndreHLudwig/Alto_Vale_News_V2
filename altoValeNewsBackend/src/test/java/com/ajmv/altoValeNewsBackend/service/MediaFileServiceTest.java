package com.ajmv.altoValeNewsBackend.service;

import org.junit.jupiter.api.Test;

import static com.ajmv.altoValeNewsBackend.service.MediaFileService.getFileType;
import static org.junit.jupiter.api.Assertions.*;

class MediaFileServiceTest {



    @Test
    void ct14_testeImageType() {
        assertEquals("images", getFileType("image/jpeg"));
    }

    @Test
    void ct15_testeVideoType() {
        assertEquals("videos", getFileType("video/mp4"));
    }

    @Test
    void ct16_testeAudioType() {
        assertEquals("audios", getFileType("audio/mpeg"));
    }

    @Test
    void ct17_testePdfType() {
        assertEquals("pdfs", getFileType("application/pdf"));
    }

    @Test
    void ct18_testeNullType() {
        assertEquals("others", getFileType(null));
    }

    @Test
    void ct19_testeDesconhecidoType() {
        assertEquals("others", getFileType("application/zip"));
    }

}