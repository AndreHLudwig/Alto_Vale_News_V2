package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.MediaFile;
import com.ajmv.altoValeNewsBackend.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/media/files")
public class MediaFileController {

    @Autowired
    private MediaFileService fileService;

    //TODO - Testar depois de Reescrever o Service
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) throws SQLException {
        MediaFile mediaFile = fileService.getFile(id);
        if (mediaFile == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(mediaFile.getType()));
        headers.setContentDispositionFormData("attachment", mediaFile.getName());

        return ResponseEntity.ok().headers(headers).body(mediaFile.getData());
    }
}
