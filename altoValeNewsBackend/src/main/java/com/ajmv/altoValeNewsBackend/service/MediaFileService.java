package com.ajmv.altoValeNewsBackend.service;

import com.ajmv.altoValeNewsBackend.exception.FileNotFoundException;
import com.ajmv.altoValeNewsBackend.model.MediaFile;
import com.ajmv.altoValeNewsBackend.repository.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class MediaFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaFileService.class);

    private final MediaFileRepository repository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public MediaFileService(MediaFileRepository repository) {
        this.repository = repository;
    }

    private Path resolveUploadPath() {
        Path resolvedPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(resolvedPath)) {
            try {
                Files.createDirectories(resolvedPath);
                LOGGER.info("Created upload directory: " + resolvedPath);
            } catch (IOException e) {
                LOGGER.error("Failed to create upload directory: " + e.getMessage());
                throw new RuntimeException("Could not create upload directory", e);
            }
        }
        return resolvedPath;
    }

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


    public MediaFile getFile(Long id) throws FileNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Arquivo não encontrado com id: " + id));
    }

    @Transactional
    public void deleteMediaFile(Long id) throws IOException {
        MediaFile mediaFile = repository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found with id: " + id));

        Path basePath = resolveUploadPath();
        Path filePath = basePath.resolve(mediaFile.getPath()).normalize();

        LOGGER.info("Current working directory: " + System.getProperty("user.dir"));
        LOGGER.info("Resolved upload path: " + basePath);
        LOGGER.info("File path to delete: " + filePath);

        // Garante que o arquivo está dentro do diretório de upload
        if (!filePath.startsWith(basePath)) {
            throw new SecurityException("Access to file outside upload directory denied");
        }

        LOGGER.info("Attempting to delete file: " + filePath);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            LOGGER.info("File deleted successfully: " + filePath);
        } else {
            LOGGER.warn("File not found on disk: " + filePath);
        }

        // Deleta qualquer diretório pai vazio
        Path parentDir = filePath.getParent();
        while (!parentDir.equals(basePath)) {
            if (Files.isDirectory(parentDir) && Files.list(parentDir).findAny().isEmpty()) {
                Files.delete(parentDir);
                LOGGER.info("Deleted empty directory: " + parentDir);
                parentDir = parentDir.getParent();
            } else {
                break;
            }
        }

        // Deleta o registro no banco de dados
        repository.delete(mediaFile);
        LOGGER.info("Deleted MediaFile record from database: " + id);
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