package com.ajmv.altoValeNewsBackend.service;

import com.ajmv.altoValeNewsBackend.model.*;
import com.ajmv.altoValeNewsBackend.repository.CategoriaRepository;
import com.ajmv.altoValeNewsBackend.repository.CurtidaRepository;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import com.ajmv.altoValeNewsBackend.repository.PublicacaoRepository;

import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PublicacaoService {

    private final PublicacaoRepository publicacaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository editorRepository;
    private final MediaFileService fileService;
    private final CurtidaRepository curtidaRepository;
    private final CategoriaService categoriaService;

    private static final Logger LOGGER = Logger.getLogger(PublicacaoService.class.getName());

    @Autowired
    public PublicacaoService(PublicacaoRepository publicacaoRepository, CategoriaRepository categoriaRepository,
                             UsuarioRepository editorRepository, MediaFileService fileService,
                             CurtidaRepository curtidaRepository, CategoriaService categoriaService) {
        this.publicacaoRepository = publicacaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.editorRepository = editorRepository;
        this.fileService = fileService;
        this.curtidaRepository = curtidaRepository;
        this.categoriaService = categoriaService;
    }

    public List<Publicacao> getAll() {
        return publicacaoRepository.findAll();
    }

    public ResponseEntity<Publicacao> getPublicacao(Integer id) {
        Optional<Publicacao> publicacao = publicacaoRepository.findById(id);
        return publicacao.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public Publicacao savePublicacao(Integer editorId, String titulo, LocalDateTime data, String texto,
                                     MultipartFile imageFile, MultipartFile videoFile,
                                     List<String> categoriasNomes, Boolean visibilidadeVip) throws IOException {
        Usuario editor = editorRepository.findById(editorId)
                .orElseThrow(() -> new IllegalArgumentException("Editor não encontrado: " + editorId));

        Publicacao publicacao = new Publicacao();
        publicacao.setEditor(editor);
        publicacao.setTitulo(titulo);
        publicacao.setData(data);
        publicacao.setTexto(texto);
        publicacao.setVisibilidadeVip(visibilidadeVip);

        // Processar categorias
        List<Categoria> categorias = categoriasNomes.stream()
                .map(categoriaService::findOrCreateCategoria)
                .collect(Collectors.toList());
        publicacao.setCategorias(categorias);

        if (imageFile != null && !imageFile.isEmpty()) {
            MediaFile imagem = fileService.saveFile(imageFile);
            publicacao.setImagem(imagem);
        }

        if (videoFile != null && !videoFile.isEmpty()) {
            MediaFile video = fileService.saveFile(videoFile);
            publicacao.setVideo(video);
        }

        return publicacaoRepository.save(publicacao);
    }

    @Transactional
    public Publicacao updatePublicacao(Integer id, JSONObject publicacaoJson,
                                       MultipartFile imageFile, MultipartFile videoFile) throws IOException {
        Publicacao publicacao = publicacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publicação não encontrada: " + id));

        Integer editorId = Optional.ofNullable(publicacaoJson.optJSONObject("editor"))
                .map(editorObj -> editorObj.getInt("userId"))
                .orElseThrow(() -> new IllegalArgumentException("Campo 'editor' é obrigatório"));

        Usuario editor = editorRepository.findById(editorId)
                .orElseThrow(() -> new IllegalArgumentException("Editor não encontrado: " + editorId));

        publicacao.setEditor(editor);
        publicacao.setTitulo(publicacaoJson.optString("titulo", publicacao.getTitulo()));
        publicacao.setData(LocalDateTime.parse(publicacaoJson.optString("data", publicacao.getData().toString())));
        publicacao.setTexto(publicacaoJson.optString("texto", publicacao.getTexto()));
        publicacao.setVisibilidadeVip(publicacaoJson.optBoolean("visibilidadeVip", publicacao.isVisibilidadeVip()));

        // Atualizar categorias
        JSONArray categoriasArray = publicacaoJson.optJSONArray("categorias");
        if (categoriasArray != null) {
            List<Categoria> categorias = new ArrayList<>();
            for (int i = 0; i < categoriasArray.length(); i++) {
                String categoriaNome = categoriasArray.getString(i);
                Categoria categoria = categoriaService.findOrCreateCategoria(categoriaNome);
                categorias.add(categoria);
            }
            publicacao.setCategorias(categorias);
        }

        // Atualizar imagem
        if (imageFile != null && !imageFile.isEmpty()) {
            MediaFile imagem = fileService.saveFile(imageFile);
            publicacao.setImagem(imagem);
        }

        // Atualizar vídeo
        if (videoFile != null && !videoFile.isEmpty()) {
            MediaFile video = fileService.saveFile(videoFile);
            publicacao.setVideo(video);
        }

        return publicacaoRepository.save(publicacao);
    }

    @Transactional
    public Publicacao partialUpdatePublicacao(Integer id, Optional<Integer> editorId, Optional<String> titulo,
                                              Optional<LocalDateTime> data, Optional<String> texto,
                                              Optional<MultipartFile> imageFile, Optional<MultipartFile> videoFile,
                                              Optional<List<String>> categoriasNomes, Optional<Boolean> visibilidadeVip)
            throws IOException {
        Publicacao publicacao = publicacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publicação não encontrada: " + id));

        editorId.ifPresent(eId -> {
            Usuario editor = editorRepository.findById(eId)
                    .orElseThrow(() -> new IllegalArgumentException("Editor não encontrado: " + eId));
            publicacao.setEditor(editor);
        });

        titulo.ifPresent(publicacao::setTitulo);
        data.ifPresent(publicacao::setData);
        texto.ifPresent(publicacao::setTexto);
        visibilidadeVip.ifPresent(publicacao::setVisibilidadeVip);

        categoriasNomes.ifPresent(nomes -> {
            List<Categoria> categorias = new ArrayList<>();
            for (String nome : nomes) {
                Categoria categoria = categoriaService.findOrCreateCategoria(nome);
                categorias.add(categoria);
            }
            publicacao.setCategorias(categorias);
        });

        imageFile.ifPresent(file -> {
            try {
                if (!file.isEmpty()) {
                    MediaFile imagem = fileService.saveFile(file);
                    publicacao.setImagem(imagem);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar arquivo de imagem", e);
            }
        });

        videoFile.ifPresent(file -> {
            try {
                if (!file.isEmpty()) {
                    MediaFile video = fileService.saveFile(file);
                    publicacao.setVideo(video);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar arquivo de vídeo", e);
            }
        });

        return publicacaoRepository.save(publicacao);
    }

    @Transactional
    public boolean deletePublicacao(Integer id) {
        Publicacao publicacao = publicacaoRepository.findById(id)
                .orElse(null);

        if (publicacao != null) {
            boolean deletedSuccessfully = true;

            // Deleta arquivos de imagem/vídeo associados
            if (publicacao.getImagem() != null) {
                try {
                    fileService.deleteMediaFile(publicacao.getImagem().getId());
                    LOGGER.info("Deleted image file for publicacao: " + id);
                } catch (Exception e) {
                    LOGGER.warning("Error deleting image file for publicacao " + id + ": " + e.getMessage());
                    deletedSuccessfully = false;
                }
            }
            if (publicacao.getVideo() != null) {
                try {
                    fileService.deleteMediaFile(publicacao.getVideo().getId());
                    LOGGER.info("Deleted video file for publicacao: " + id);
                } catch (Exception e) {
                    LOGGER.warning("Error deleting video file for publicacao " + id + ": " + e.getMessage());
                    deletedSuccessfully = false;
                }
            }

            try {
                publicacaoRepository.delete(publicacao);
                LOGGER.info("Deleted publicacao: " + id);
            } catch (Exception e) {
                LOGGER.severe("Error deleting publicacao " + id + " from database: " + e.getMessage());
                deletedSuccessfully = false;
            }

            return deletedSuccessfully;
        }

        return false;
    }

    public ResponseEntity<Publicacao> like(Integer id, Usuario usuarioLogado) {
        try {
            Optional<Publicacao> publicacaoOptional = publicacaoRepository.findById(id);
            if (publicacaoOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Publicacao publicacao = publicacaoOptional.get();

            Optional<Curtida> curtidaOptional = curtidaRepository.findByPublicacaoAndUsuario(publicacao, usuarioLogado);
            if (curtidaOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(publicacao);
            }

            Curtida curtida = new Curtida();
            curtida.setPublicacao(publicacao);
            curtida.setUsuario(usuarioLogado);
            curtidaRepository.save(curtida);

            return ResponseEntity.ok(publicacao);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Publicacao> unlike(Integer id, Usuario usuarioLogado) {
        try {
            // Busca publicaçao a ter seu like deletado
            Optional<Publicacao> publicacaoOptional = publicacaoRepository.findById(id);
            if (publicacaoOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Publicacao publicacao = publicacaoOptional.get();

            // Encontra a curtida pela publicação e usuário logado
            Optional<Curtida> curtidaOptional = curtidaRepository.findByPublicacaoAndUsuario(publicacao, usuarioLogado);
            if (curtidaOptional.isPresent()) {
                curtidaRepository.delete(curtidaOptional.get());
                return ResponseEntity.ok(publicacao);
            }

            // Último fallback em caso de conflito -- como um usuário tentando descurtir uma curtida que não é sua
            return ResponseEntity.status(HttpStatus.CONFLICT).body(publicacao);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
