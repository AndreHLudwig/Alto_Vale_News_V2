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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable Long id) {
        try {
            MediaFile mediaFile = fileService.getFile(id);
            Path filePath = Paths.get(uploadDir, mediaFile.getPath());
            Resource resource = new FileSystemResource(filePath.toFile());

            if (resource.exists() && resource.isReadable()) {
                StreamingResponseBody responseBody = outputStream -> {
                    try (InputStream inputStream = resource.getInputStream()) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        try {
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            outputStream.flush();
                        } catch (IOException e) {
                            if (e.getMessage().contains("Broken pipe")) {
                                logger.warn("Client disconnected prematurely while streaming file: {}", mediaFile.getName());
                            } else {
                                logger.error("Error streaming file: {}", mediaFile.getName(), e);
                                throw new FileProcessingException("Error streaming file", e);
                            }
                        }
                    } catch (IOException e) {
                        logger.error("Error opening input stream for file: {}", mediaFile.getName(), e);
                        throw new FileProcessingException("Error accessing file", e);
                    }
                };

                String sanitizedFileName = sanitizeFileName(mediaFile.getName());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(mediaFile.getType()));
                headers.setContentDispositionFormData("attachment", sanitizedFileName);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(responseBody);
            } else {
                throw new FileNotFoundException("File not found or not readable: " + mediaFile.getName());
            }
        } catch (Exception e) {
            logger.error("Error processing file request for id: {}", id, e);
            throw new FileProcessingException("Error processing file request", e);
        }
    }

    private String sanitizeFileName(String fileName) {
        // Remove caracteres não ASCII e substitui espaços por underscores
        String sanitized = fileName.replaceAll("[^\\x00-\\x7F]", "").replaceAll("\\s+", "_");
        // Codifica o nome do arquivo para URL
        return URLEncoder.encode(sanitized, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
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