package com.ajmv.altoValeNewsBackend.service;

import com.ajmv.altoValeNewsBackend.exception.FileNotFoundException;
import com.ajmv.altoValeNewsBackend.model.MediaFile;
import com.ajmv.altoValeNewsBackend.repository.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class MediaFileService {
    private static final Logger logger = LoggerFactory.getLogger(MediaFileService.class);

    @Autowired
    private MediaFileRepository repository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public MediaFile saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser null ou vazio");
        }

        String fileType = getFileType(file.getContentType());
        LocalDate today = LocalDate.now();

        Path relativePath = Path.of(fileType,
                String.valueOf(today.getYear()),
                String.format("%02d", today.getMonthValue()),
                String.format("%02d", today.getDayOfMonth()));

        Path uploadPath = Path.of(uploadDir).resolve(relativePath);
        Files.createDirectories(uploadPath);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(originalFilename);
        mediaFile.setType(file.getContentType());
        mediaFile.setPath(relativePath.resolve(fileName).toString());

        return repository.save(mediaFile);
    }


    public MediaFile getFile(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found with id: " + id));
    }

    public void deleteFile(Long id) throws IOException {
        MediaFile mediaFile = repository.findById(id).orElse(null);
        if (mediaFile != null) {
            Path filePath = Paths.get(uploadDir, mediaFile.getPath());
            Files.deleteIfExists(filePath);
            repository.deleteById(id);
        }
    }

    private String getFileType(String contentType) {
        if (contentType == null) {
            return "others";
        }

        contentType = contentType.toLowerCase();
        if (contentType.startsWith("image/")) {
            return "images";
        } else if (contentType.startsWith("video/")) {
            return "videos";
        } else if (contentType.startsWith("audio/")) {
            return "audios";
        } else if (contentType.equals("application/pdf")) {
            return "pdfs";
        } else {
            return "others";
        }
    }
}