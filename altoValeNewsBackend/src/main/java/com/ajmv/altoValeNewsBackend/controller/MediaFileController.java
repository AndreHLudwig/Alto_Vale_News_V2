package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.MediaFile;
import com.ajmv.altoValeNewsBackend.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<MediaFile> getFile(@PathVariable Long id) throws SQLException {
        MediaFile mediaFile = fileService.getFile(id);

        if (mediaFile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mediaFile);
    }
}
