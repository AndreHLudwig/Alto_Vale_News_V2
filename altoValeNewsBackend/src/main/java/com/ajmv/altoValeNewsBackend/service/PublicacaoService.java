package com.ajmv.altoValeNewsBackend.service;

import com.ajmv.altoValeNewsBackend.model.*;
import com.ajmv.altoValeNewsBackend.repository.CategoriaRepository;
import com.ajmv.altoValeNewsBackend.repository.CurtidaRepository;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ajmv.altoValeNewsBackend.repository.PublicacaoRepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PublicacaoService {

    private final PublicacaoRepository publicacaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository editorRepository;
    private final MediaFileService fileService;
    private final ComentarioService comentarioService;
    private final CurtidaRepository curtidaRepository;

    @Autowired
    public PublicacaoService(PublicacaoRepository publicacaoRepository, CategoriaRepository categoriaRepository, UsuarioRepository editorRepository, MediaFileService fileService, ComentarioService comentarioService, CurtidaRepository curtidaRepository) {
        this.publicacaoRepository = publicacaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.editorRepository = editorRepository;
        this.fileService = fileService;
        this.comentarioService = comentarioService;
        this.curtidaRepository = curtidaRepository;
    }

    private static final Logger LOGGER = Logger.getLogger(PublicacaoService.class.getName());

    public List<Publicacao> getAll() {
        return publicacaoRepository.findAll();
    }

    public ResponseEntity<Publicacao> getPublicacao(Integer id) {
        Optional<Publicacao> publicacao = publicacaoRepository.findById(id);
        return publicacao.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /* Ao inserir publicações com mídia o problema anterior não acontece diretamente,
    mas sim no MediaFileService (usamos JDBC lá). */
    @Transactional
    public Publicacao savePublicacao(Integer editorId, String titulo, LocalDate data, String texto,
                                     MultipartFile imageFile, MultipartFile videoFile,
                                     List<Categoria> categorias, Boolean visibilidadeVip, List<Curtida> curtidas) throws IOException, SQLException {
        //TODO
//        Editor editor = editorRepository.findById(editorId)
//                .orElseThrow(() -> new IllegalArgumentException("Editor não encontrado: " + editorId));
//
//        MediaFile imagem = processMediaFile(imageFile);
//        MediaFile video = processMediaFile(videoFile);
//
//        Publicacao publicacao = new Publicacao();
//        publicacao.setEditor(editor);
//        publicacao.setTitulo(titulo);
//        publicacao.setData(data);
//        publicacao.setTexto(texto);
//        publicacao.setImagem(imagem);
//        publicacao.setVideo(video);
//        publicacao.setCategoria(categoria);
//        publicacao.setVisibilidadeVip(visibilidadeVip);
//        publicacao.setCurtidas(curtidas);

//        return publicacaoRepository.save(publicacao);
        return null;
    }

    private MediaFile processMediaFile(MultipartFile file) throws IOException, SQLException {
        if (file != null && !file.isEmpty()) {
            return fileService.saveFile(file);
        }
        return null;
    }

    public Publicacao updatePublicacao(Integer id, JSONObject publicacaoJson,
                                       MultipartFile imageFile, MultipartFile videoFile) throws IOException, SQLException {
        Publicacao publicacao = publicacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publicação não encontrada: " + id));

        Integer editorId = Optional.ofNullable(publicacaoJson.optJSONObject("editor"))
                .map(editorObj -> editorObj.getInt("userId"))
                .orElseThrow(() -> new IllegalArgumentException("Campo 'editor' é obrigatório"));
        String titulo = publicacaoJson.optString("titulo", null);
        LocalDateTime data = LocalDateTime.parse(publicacaoJson.optString("data", null));
        String texto = publicacaoJson.optString("texto", null);
        Boolean visibilidadeVip = publicacaoJson.optBoolean("visibilidadeVip", false);

        // Tratamento da lista de categorias
        List<Categoria> categorias = new ArrayList<>();
        JSONArray categoriasArray = publicacaoJson.optJSONArray("categorias");
        if (categoriasArray != null) {
            for (int i = 0; i < categoriasArray.length(); i++) {
                JSONObject categoriaObj = categoriasArray.getJSONObject(i);
                Integer categoriaId = categoriaObj.getInt("categoriaId");
                Categoria categoria = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada: " + categoriaId));
                categorias.add(categoria);
            }
        }

        Usuario editor = null;
        if (editorId != null) {
            editor = editorRepository.findById(editorId)
                    .orElseThrow(() -> new IllegalArgumentException("Editor não encontrado: " + editorId));
        }
        publicacao.setEditor(editor);
        publicacao.setTitulo(titulo);
        publicacao.setData(data);
        publicacao.setTexto(texto);
        publicacao.setCategorias(categorias);
        publicacao.setVisibilidadeVip(visibilidadeVip);

        // Apesar de ser um PUT, executamos o serviço de arquivos somente se houver algo a ser salvo
        MediaFile imagem = null;
        if (imageFile != null) {
            imagem = fileService.saveFile(imageFile);
        }
        publicacao.setImagem(imagem);

        MediaFile video = null;
        if (videoFile != null) {
            video = fileService.saveFile(videoFile);
        }
        publicacao.setVideo(video);

        return publicacaoRepository.save(publicacao);
    }

    public Publicacao partialUpdatePublicacao(Integer id, Integer editorId, String titulo, LocalDateTime data, String texto, MultipartFile imageFile, MultipartFile videoFile,
                                              List<Categoria> categorias, Boolean visibilidadeVip/*, Integer curtidas*/) throws IOException, SQLException {
        Publicacao publicacao = publicacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publicação não encontrada: " + id));

        if (editorId != null) {
            Usuario editor = editorRepository.findById(editorId)
                    .orElseThrow(() -> new IllegalArgumentException("Editor não encontrado: " + editorId));
            publicacao.setEditor(editor);
        }

        if (titulo != null) {
            publicacao.setTitulo(titulo);
        }
        if (data != null) {
            publicacao.setData(data);
        }
        if (texto != null) {
            publicacao.setTexto(texto);
        }
        if (categorias != null) {
            publicacao.setCategorias(categorias);
        }
        if (visibilidadeVip != null) {
            publicacao.setVisibilidadeVip(visibilidadeVip);
        }
//        if (curtidas != null) {
//            publicacao.setCurtidas(curtidas);
//        }

        if (imageFile != null) {
            MediaFile imagem = fileService.saveFile(imageFile);
            publicacao.setImagem(imagem);
        }

        if (videoFile != null) {
            MediaFile video = fileService.saveFile(videoFile);
            publicacao.setVideo(video);
        }

        return publicacaoRepository.save(publicacao);
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
