package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.exception.FileNotFoundException;
import com.ajmv.altoValeNewsBackend.exception.FileProcessingException;
import com.ajmv.altoValeNewsBackend.model.MediaFile;
import com.ajmv.altoValeNewsBackend.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/media/files")
public class MediaFileController {
    private static final Logger logger = LoggerFactory.getLogger(MediaFileController.class);

    private final MediaFileService fileService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public MediaFileController(MediaFileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<MediaFile> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            MediaFile uploadedFile = fileService.saveFile(file);
            logger.info("File uploaded successfully: {}", uploadedFile.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);
        } catch (IOException e) {
            logger.error("Error uploading file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Error uploading file", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Long id) {
        try {
            logger.info("Attempting to retrieve file with id: {}", id);
            logger.info("Upload directory: {}", uploadDir);

            MediaFile mediaFile = fileService.getFile(id);
            Path filePath = Paths.get(uploadDir).resolve(mediaFile.getPath()).normalize();
            Resource resource = new FileSystemResource(filePath.toFile());

            logger.info("Full file path: {}", filePath.toString());

            if (!resource.exists()) {
                logger.warn("File not found on disk: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            logger.info("File exists: {}", Files.exists(filePath));

            if (!resource.isReadable()) {
                logger.error("File is not readable: {}", filePath);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            String contentType = mediaFile.getType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            String sanitizedFileName = sanitizeFileName(mediaFile.getName());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sanitizedFileName + "\"")
                    .body(resource);

        } catch (FileNotFoundException e) {
            logger.warn("File not found in database for id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error processing file request for id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String sanitizeFileName(String fileName) {
        String sanitized = fileName.replaceAll("[^\\x00-\\x7F]", "").replaceAll("\\s+", "_");
        return URLEncoder.encode(sanitized, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        try {
            fileService.deleteMediaFile(id);
            logger.info("File deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (FileNotFoundException e) {
            logger.warn("File not found for deletion: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            logger.error("Error deleting file: {}", id, e);
            throw new FileProcessingException("Error deleting file", e);
        }
    }
}