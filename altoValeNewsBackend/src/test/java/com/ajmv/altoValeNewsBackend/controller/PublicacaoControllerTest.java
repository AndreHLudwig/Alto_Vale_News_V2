package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.Categoria;
import com.ajmv.altoValeNewsBackend.model.Publicacao;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.repository.CategoriaRepository;
import com.ajmv.altoValeNewsBackend.repository.PublicacaoRepository;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PublicacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublicacaoRepository publicacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private ObjectMapper objectMapper;
    private Usuario editor;
    private Usuario outroEditor;
    private Categoria esporte;
    private Categoria politica;
    private Categoria geral;

    private static final String LONG_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";

    @BeforeEach
    void setUp() throws IOException {
        // Limpa os dados antes de cada teste para garantir isolamento
        publicacaoRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Configura o ObjectMapper para lidar com LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Cria um editor para os testes
        editor = new Usuario();
        editor.setNome("Editor Teste");
        editor.setEmail("editor@teste.com");
        editor.setSenha("senha123");
        editor = usuarioRepository.save(editor);
        System.out.println("DEBUG: Editor salvo com ID: " + editor.getUserId());
        assertNotNull(editor.getUserId(), "Editor ID should not be null after saving.");

        // Outro editor
        outroEditor = new Usuario();
        outroEditor.setNome("Outro Editor");
        outroEditor.setEmail("outro@teste.com");
        outroEditor.setSenha("senha456");
        outroEditor = usuarioRepository.save(outroEditor);
        System.out.println("DEBUG: Outro Editor salvo com ID: " + outroEditor.getUserId());

        // Cria categorias para os testes
        esporte = new Categoria();
        esporte.setNome("Esporte");
        esporte = categoriaRepository.save(esporte);

        politica = new Categoria();
        politica.setNome("Política");
        politica = categoriaRepository.save(politica);

        geral = new Categoria();
        geral.setNome("Geral");
        geral = categoriaRepository.save(geral);

    }

    @AfterEach
    void tearDown() {
        // Limpa os dados após cada teste
        publicacaoRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }


    @Test
    void ct22_PublicacaoCompletaESalvaComSucesso() throws Exception {
        // Entradas
        String titulo = "Publicação Completa - Esporte e Política";
        LocalDateTime data = LocalDateTime.now();
        String texto = LONG_TEXT;
        List<String> categoriasNomes = Arrays.asList("Esporte", "Política");
        Boolean visibilidadeVip = false;

        // Ação e Verificação (MockMvc)
        mockMvc.perform(MockMvcRequestBuilders.multipart("/publicacao")
                        .param("editorId", editor.getUserId().toString())
                        .param("titulo", titulo)
                        .param("data", data.format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("texto", texto)
                        .param("categorias", categoriasNomes.toArray(new String[0]))
                        .param("visibilidadeVip", visibilidadeVip.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$.publicacaoId").exists()) // Verifica se o ID foi preenchido
                .andExpect(jsonPath("$.titulo").value(titulo))
                .andExpect(jsonPath("$.editor.userId").value(editor.getUserId()))
                .andExpect(jsonPath("$.categorias.length()").value(2))
                .andExpect(jsonPath("$.categorias[?(@.nome == 'Esporte')]").exists())
                .andExpect(jsonPath("$.categorias[?(@.nome == 'Política')]").exists())
                .andExpect(jsonPath("$.visibilidadeVip").value(visibilidadeVip));

        // Verificação (Banco de Dados)
        Optional<Publicacao> savedPublicacaoOpt = publicacaoRepository.findAll().stream().findFirst();
        assertTrue(savedPublicacaoOpt.isPresent(), "Publicação deve ser salva no banco de dados.");
        Publicacao savedPublicacao = savedPublicacaoOpt.get();
        assertNotNull(savedPublicacao.getPublicacaoId(), "ID da publicação não deve ser nulo.");
        assertEquals(titulo, savedPublicacao.getTitulo());
        assertEquals(editor.getUserId(), savedPublicacao.getEditor().getUserId());
        assertEquals(2, savedPublicacao.getCategorias().size());
        assertTrue(savedPublicacao.getCategorias().stream().anyMatch(c -> c.getNome().equals("Esporte")));
        assertTrue(savedPublicacao.getCategorias().stream().anyMatch(c -> c.getNome().equals("Política")));
        assertEquals(visibilidadeVip, savedPublicacao.isVisibilidadeVip());
    }




    @Test
    void ct23_NoticiaVIP() throws Exception {
        // Entradas
        String titulo = "Notícia VIP Exclusiva";
        LocalDateTime data = LocalDateTime.now();
        String texto = LONG_TEXT;

        List<String> categoriasNomes = Arrays.asList("Exclusivo");
        Boolean visibilidadeVip = true; // Definido como true

        // Ação e Verificação (MockMvc)
        mockMvc.perform(MockMvcRequestBuilders.multipart("/publicacao")
                        .param("editorId", editor.getUserId().toString())
                        .param("titulo", titulo)
                        .param("data", data.format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("texto", texto)
                        .param("categorias", categoriasNomes.toArray(new String[0]))
                        .param("visibilidadeVip", visibilidadeVip.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$.publicacaoId").exists())
                .andExpect(jsonPath("$.titulo").value(titulo))
                .andExpect(jsonPath("$.visibilidadeVip").value(true)); // Verifica que é VIP na resposta

        // Verificação (Banco de Dados)
        Optional<Publicacao> savedPublicacaoOpt = publicacaoRepository.findAll().stream().findFirst();
        assertTrue(savedPublicacaoOpt.isPresent(), "Publicação VIP deve ser salva no banco de dados.");
        Publicacao savedPublicacao = savedPublicacaoOpt.get();
        assertEquals(titulo, savedPublicacao.getTitulo());
        assertTrue(savedPublicacao.isVisibilidadeVip(), "A publicação deve ser marcada como VIP no banco de dados.");
        assertEquals(1, savedPublicacao.getCategorias().size());
        assertTrue(savedPublicacao.getCategorias().stream().anyMatch(c -> c.getNome().equals("Exclusivo")));
    }


    @Test
    void ct24_AtualizacaoTotalPublicacao() throws Exception {
        // 1. Preparar dados iniciais (Publicacao existente)
        String initialTitle = "Titulo Original";
        LocalDateTime initialDate = LocalDateTime.now().minusDays(1);
        String initialText = LONG_TEXT;
        List<String> initialCategories = Arrays.asList("Esporte");
        Boolean initialVip = false;

        // Criar e salvar a publicação inicial
        Publicacao initialPublicacao = new Publicacao();
        initialPublicacao.setEditor(editor);
        initialPublicacao.setTitulo(initialTitle);
        initialPublicacao.setData(initialDate);
        initialPublicacao.setTexto(initialText);
        initialPublicacao.setVisibilidadeVip(initialVip);
        initialPublicacao.setCategorias(initialCategories.stream()
                .map(categoriaRepository::findByNome)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
        initialPublicacao = publicacaoRepository.save(initialPublicacao);
        assertNotNull(initialPublicacao.getPublicacaoId(), "ID da publicação inicial deve ser gerado.");

        // 2. Definir os novos dados para atualização
        String updatedTitle = "Titulo Atualizado para Teste CT24";
        LocalDateTime updatedDate = LocalDateTime.now();
        String updatedText = LONG_TEXT + "ATT";
        List<String> updatedCategoriesNames = Arrays.asList("Política");
        Boolean updatedVip = true;

        // Construir o JSON de atualização
        JSONObject publicacaoJson = new JSONObject();
        // O PublicacaoService.updatePublicacao espera editor.userId dentro do JSON
        JSONObject editorJson = new JSONObject();
        editorJson.put("userId", editor.getUserId());
        publicacaoJson.put("editor", editorJson);

        publicacaoJson.put("titulo", updatedTitle);
        publicacaoJson.put("data", updatedDate.format(DateTimeFormatter.ISO_DATE_TIME));
        publicacaoJson.put("texto", updatedText);

        // As categorias devem ser enviadas como um JSONArray de strings
        JSONArray categoriasJsonArray = new JSONArray();
        for (String categoriaNome : updatedCategoriesNames) {
            categoriasJsonArray.put(categoriaNome);
        }
        publicacaoJson.put("categorias", categoriasJsonArray);

        publicacaoJson.put("visibilidadeVip", updatedVip);

        // 3. Realizar a requisição PUT para atualização
        mockMvc.perform(MockMvcRequestBuilders.put("/publicacao/{id}", initialPublicacao.getPublicacaoId())
                        .param("publicacao", publicacaoJson.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$.publicacaoId").value(initialPublicacao.getPublicacaoId()))
                .andExpect(jsonPath("$.titulo").value(updatedTitle))
                .andExpect(jsonPath("$.texto").value(updatedText))
                .andExpect(jsonPath("$.editor.userId").value(editor.getUserId()))
                .andExpect(jsonPath("$.visibilidadeVip").value(updatedVip))
                .andExpect(jsonPath("$.categorias.length()").value(1))
                .andExpect(jsonPath("$.categorias[?(@.nome == 'Política')]").exists());

        // 4. Verificar no banco de dados se os dados foram atualizados
        Optional<Publicacao> updatedPublicacaoOpt = publicacaoRepository.findById(initialPublicacao.getPublicacaoId());
        assertTrue(updatedPublicacaoOpt.isPresent(), "Publicação atualizada deve ser encontrada no banco de dados.");
        Publicacao updatedPublicacao = updatedPublicacaoOpt.get();

        assertEquals(updatedTitle, updatedPublicacao.getTitulo());
        assertEquals(updatedText, updatedPublicacao.getTexto());
        assertEquals(updatedVip, updatedPublicacao.isVisibilidadeVip());
        assertEquals(editor.getUserId(), updatedPublicacao.getEditor().getUserId());

        // Verificação das categorias após a atualização
        assertEquals(1, updatedPublicacao.getCategorias().size());
        assertTrue(updatedPublicacao.getCategorias().stream().anyMatch(c -> c.getNome().equals("Política")));

        // Verifica se a categoria "Esporte" original não está mais associada se o update substituiu
        assertFalse(updatedPublicacao.getCategorias().stream().anyMatch(c -> c.getNome().equals("Esporte")));
    }

    @Test
    void ct25_EditorTentaAlterarPublicacaoDeOutroEditor() throws Exception {
        // 1. Preparar dados iniciais: Publicação de um EDITOR_ORIGINAL e um EDITOR_MALICIOSO

        // Criar um segundo editor que tentará alterar a publicação do primeiro
        assertNotNull(outroEditor.getUserId(), "ID do outro editor não deve ser nulo.");


        String initialTitle = "Titulo do Editor Original";
        LocalDateTime initialDate = LocalDateTime.now().minusDays(2);
        String initialText = LONG_TEXT;
        List<String> initialCategories = Arrays.asList("Geral");
        Boolean initialVip = false;

        // Criar e salvar uma publicação pelo 'editor' padrão do teste (editor original)
        Publicacao publicacaoOriginal = new Publicacao();
        publicacaoOriginal.setEditor(editor); // Editor que criou a publicação
        publicacaoOriginal.setTitulo(initialTitle);
        publicacaoOriginal.setData(initialDate);
        publicacaoOriginal.setTexto(initialText);
        publicacaoOriginal.setVisibilidadeVip(initialVip);

        publicacaoOriginal.setCategorias(initialCategories.stream()
                .map(categoriaRepository::findByNome)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
        publicacaoOriginal = publicacaoRepository.save(publicacaoOriginal);
        assertNotNull(publicacaoOriginal.getPublicacaoId(), "ID da publicação original deve ser gerado.");
        System.out.println("DEBUG: Publicação original criada com ID: " + publicacaoOriginal.getPublicacaoId() + " pelo Editor ID: " + publicacaoOriginal.getEditor().getUserId());


        // 2. Definir os dados para a tentativa de atualização (por 'outroEditor')
        String updatedTitleAttempt = "Titulo Malicioso Tentando Alterar";
        LocalDateTime updatedDateAttempt = LocalDateTime.now();
        String updatedTextAttempt = LONG_TEXT + "ATT";
        List<String> updatedCategoriesAttempt = Arrays.asList("Política");
        Boolean updatedVipAttempt = true;

        // Construir o JSON de atualização, mas com o ID do 'outroEditor'
        JSONObject publicacaoJsonByOtherEditor = new JSONObject();
        JSONObject editorJson = new JSONObject();
        editorJson.put("userId", outroEditor.getUserId());
        publicacaoJsonByOtherEditor.put("editor", editorJson);

        publicacaoJsonByOtherEditor.put("titulo", updatedTitleAttempt);
        publicacaoJsonByOtherEditor.put("data", updatedDateAttempt.format(DateTimeFormatter.ISO_DATE_TIME));
        publicacaoJsonByOtherEditor.put("texto", updatedTextAttempt);

        // **IMPORTANTE: Usar JSONArray explicitamente para categorias**
        JSONArray categoriasJsonArray = new JSONArray();
        for (String categoriaNome : updatedCategoriesAttempt) {
            categoriasJsonArray.put(categoriaNome);
        }
        publicacaoJsonByOtherEditor.put("categorias", categoriasJsonArray);

        publicacaoJsonByOtherEditor.put("visibilidadeVip", updatedVipAttempt);

        // 3. Realizar a requisição PUT com o editor não autorizado
        mockMvc.perform(MockMvcRequestBuilders.put("/publicacao/{id}", publicacaoOriginal.getPublicacaoId())
                        .param("publicacao", publicacaoJsonByOtherEditor.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isForbidden());

        // 4. Verificar se nenhuma alteração foi feita no banco de dados
        Optional<Publicacao> publicacaoAfterAttemptOpt = publicacaoRepository.findById(publicacaoOriginal.getPublicacaoId());
        assertTrue(publicacaoAfterAttemptOpt.isPresent(), "A publicação original ainda deve existir.");
        Publicacao publicacaoAfterAttempt = publicacaoAfterAttemptOpt.get();

        // Assegurar que os campos permanecem com os valores originais
        assertEquals(publicacaoOriginal.getTitulo(), publicacaoAfterAttempt.getTitulo());
        assertEquals(publicacaoOriginal.getTexto(), publicacaoAfterAttempt.getTexto());
        assertEquals(publicacaoOriginal.isVisibilidadeVip(), publicacaoAfterAttempt.isVisibilidadeVip());
        assertEquals(publicacaoOriginal.getEditor().getUserId(), publicacaoAfterAttempt.getEditor().getUserId());

        assertEquals(1, publicacaoAfterAttempt.getCategorias().size());
        assertTrue(publicacaoAfterAttempt.getCategorias().stream().anyMatch(c -> c.getNome().equals("Geral")));
        assertFalse(publicacaoAfterAttempt.getCategorias().stream().anyMatch(c -> c.getNome().equals("Política")));
    }


    @Test
    void ct26_AtualizacaoParcialApenasTexto() throws Exception {
        // 1. Preparar dados iniciais para a publicação a ser atualizada
        String initialTitle = "Titulo Original CT26";
        LocalDateTime initialDate = LocalDateTime.now().minusDays(3);
        String initialText = LONG_TEXT;
        List<String> initialCategories = Arrays.asList("Geral");
        Boolean initialVip = false;

        Publicacao originalPublicacao = new Publicacao();
        originalPublicacao.setEditor(editor);
        originalPublicacao.setTitulo(initialTitle);
        originalPublicacao.setData(initialDate);
        originalPublicacao.setTexto(initialText);
        originalPublicacao.setVisibilidadeVip(initialVip);
        originalPublicacao.setCategorias(initialCategories.stream()
                .map(categoriaRepository::findByNome)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
        originalPublicacao = publicacaoRepository.save(originalPublicacao);
        assertNotNull(originalPublicacao.getPublicacaoId(), "ID da publicação original CT26 deve ser gerado.");
        System.out.println("DEBUG: Publicação original para CT26 criada com ID: " + originalPublicacao.getPublicacaoId());


        // 2. Definir o novo texto para atualização parcial
        String updatedPartialText = LONG_TEXT + "ATT";

        // 3. Realizar a requisição PATCH
        mockMvc.perform(MockMvcRequestBuilders.patch("/publicacao/{id}", originalPublicacao.getPublicacaoId())
                        .param("texto", updatedPartialText)
                        .param("editorId", editor.getUserId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$.publicacaoId").value(originalPublicacao.getPublicacaoId()))
                .andExpect(jsonPath("$.texto").value(updatedPartialText))
                .andExpect(jsonPath("$.titulo").value(initialTitle))
                .andExpect(jsonPath("$.visibilidadeVip").value(initialVip));


        // 4. Verificar no banco de dados se o campo "texto" foi alterado e outros permaneceram
        Optional<Publicacao> updatedPublicacaoOpt = publicacaoRepository.findById(originalPublicacao.getPublicacaoId());
        assertTrue(updatedPublicacaoOpt.isPresent(), "Publicação atualizada deve ser encontrada no banco de dados.");
        Publicacao updatedPublicacao = updatedPublicacaoOpt.get();

        assertEquals(updatedPartialText, updatedPublicacao.getTexto());
        assertEquals(initialTitle, updatedPublicacao.getTitulo());
        assertEquals(initialDate.withNano(0), updatedPublicacao.getData().withNano(0));
        assertEquals(initialVip, updatedPublicacao.isVisibilidadeVip());
        assertEquals(editor.getUserId(), updatedPublicacao.getEditor().getUserId());

        assertEquals(1, updatedPublicacao.getCategorias().size());
        assertTrue(updatedPublicacao.getCategorias().stream().anyMatch(c -> c.getNome().equals("Geral")));
    }

    @Test
    void ct27_AtualizacaoParcialApenasTitulo() throws Exception {
        // 1. Preparar dados iniciais para a publicação a ser atualizada
        String initialTitle = "Titulo Original CT27";
        LocalDateTime initialDate = LocalDateTime.now().minusDays(3);
        String initialText = LONG_TEXT;
        List<String> initialCategories = Arrays.asList("Geral");
        Boolean initialVip = false;

        Publicacao originalPublicacao = new Publicacao();
        originalPublicacao.setEditor(editor);
        originalPublicacao.setTitulo(initialTitle);
        originalPublicacao.setData(initialDate);
        originalPublicacao.setTexto(initialText);
        originalPublicacao.setVisibilidadeVip(initialVip);
        originalPublicacao.setCategorias(initialCategories.stream()
                .map(categoriaRepository::findByNome)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
        originalPublicacao = publicacaoRepository.save(originalPublicacao);
        assertNotNull(originalPublicacao.getPublicacaoId(), "ID da publicação original CT26 deve ser gerado.");
        System.out.println("DEBUG: Publicação original para CT27 criada com ID: " + originalPublicacao.getPublicacaoId());


        // 2. Definir o novo titulo para atualização parcial
        String updatedPartialTitulo = "Titulo ATUALIZADO CT27";

        // 3. Realizar a requisição PATCH
        mockMvc.perform(MockMvcRequestBuilders.patch("/publicacao/{id}", originalPublicacao.getPublicacaoId())
                        .param("titulo", updatedPartialTitulo)
                        .param("editorId", editor.getUserId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$.publicacaoId").value(originalPublicacao.getPublicacaoId()))
                .andExpect(jsonPath("$.texto").value(initialText))
                .andExpect(jsonPath("$.titulo").value(updatedPartialTitulo))
                .andExpect(jsonPath("$.visibilidadeVip").value(initialVip));


        // 4. Verificar no banco de dados se o campo "texto" foi alterado e outros permaneceram
        Optional<Publicacao> updatedPublicacaoOpt = publicacaoRepository.findById(originalPublicacao.getPublicacaoId());
        assertTrue(updatedPublicacaoOpt.isPresent(), "Publicação atualizada deve ser encontrada no banco de dados.");
        Publicacao updatedPublicacao = updatedPublicacaoOpt.get();

        assertEquals(initialText, updatedPublicacao.getTexto());
        assertEquals(updatedPartialTitulo, updatedPublicacao.getTitulo());
        assertEquals(initialDate.withNano(0), updatedPublicacao.getData().withNano(0));
        assertEquals(initialVip, updatedPublicacao.isVisibilidadeVip());
        assertEquals(editor.getUserId(), updatedPublicacao.getEditor().getUserId());

        assertEquals(1, updatedPublicacao.getCategorias().size());
        assertTrue(updatedPublicacao.getCategorias().stream().anyMatch(c -> c.getNome().equals("Geral")));
    }


    @Test
    void ct28_AtualizacaoParcialApenasCategoria() throws Exception {
        // 1. Preparar dados iniciais para a publicação a ser atualizada
        String initialTitle = "Titulo Original CT27";
        LocalDateTime initialDate = LocalDateTime.now().minusDays(3);
        String initialText = LONG_TEXT;
        List<String> initialCategories = Arrays.asList("Geral");
        Boolean initialVip = false;

        Publicacao originalPublicacao = new Publicacao();
        originalPublicacao.setEditor(editor);
        originalPublicacao.setTitulo(initialTitle);
        originalPublicacao.setData(initialDate);
        originalPublicacao.setTexto(initialText);
        originalPublicacao.setVisibilidadeVip(initialVip);
        originalPublicacao.setCategorias(initialCategories.stream()
                .map(categoriaRepository::findByNome)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
        originalPublicacao = publicacaoRepository.save(originalPublicacao);
        assertNotNull(originalPublicacao.getPublicacaoId(), "ID da publicação original CT26 deve ser gerado.");
        System.out.println("DEBUG: Publicação original para CT28 criada com ID: " + originalPublicacao.getPublicacaoId());


        // 2. Definir o nova categoria para atualização parcial
        List<String> updatedCategoria = Arrays.asList("Esportes");

        // 3. Realizar a requisição PATCH
        mockMvc.perform(MockMvcRequestBuilders.patch("/publicacao/{id}", originalPublicacao.getPublicacaoId())
                        .param("categoriasNomes", updatedCategoria.toArray(new String[0]))
                        .param("editorId", editor.getUserId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$.publicacaoId").value(originalPublicacao.getPublicacaoId()))
                .andExpect(jsonPath("$.texto").value(initialText))
                .andExpect(jsonPath("$.titulo").value(initialTitle))
                .andExpect(jsonPath("$.visibilidadeVip").value(initialVip));


        // 4. Verificar no banco de dados se o campo "texto" foi alterado e outros permaneceram
        Optional<Publicacao> updatedPublicacaoOpt = publicacaoRepository.findById(originalPublicacao.getPublicacaoId());
        assertTrue(updatedPublicacaoOpt.isPresent(), "Publicação atualizada deve ser encontrada no banco de dados.");
        Publicacao updatedPublicacao = updatedPublicacaoOpt.get();

        assertEquals(initialText, updatedPublicacao.getTexto());
        assertEquals(initialTitle, updatedPublicacao.getTitulo());
        assertEquals(initialDate.withNano(0), updatedPublicacao.getData().withNano(0));
        assertEquals(initialVip, updatedPublicacao.isVisibilidadeVip());
        assertEquals(editor.getUserId(), updatedPublicacao.getEditor().getUserId());

        assertEquals(1, updatedPublicacao.getCategorias().size());
        assertTrue(updatedPublicacao.getCategorias().stream().anyMatch(c -> c.getNome().equals("Esportes")));
        assertFalse(updatedPublicacao.getCategorias().stream().anyMatch(c -> c.getNome().equals("Geral")));
    }




    @Test
    void ct29_ExclusaoCompletaBemSucedida() throws Exception {
        // 1. Preparar dados iniciais: Criar uma publicação para ser excluída
        String initialTitle = "Publicação a ser Excluída CT29";
        LocalDateTime initialDate = LocalDateTime.now().minusDays(1);
        String initialText = LONG_TEXT;
        List<String> initialCategories = Arrays.asList("Geral");
        Boolean initialVip = false;

        Publicacao publicacaoToDelete = new Publicacao();
        publicacaoToDelete.setEditor(editor);
        publicacaoToDelete.setTitulo(initialTitle);
        publicacaoToDelete.setData(initialDate);
        publicacaoToDelete.setTexto(initialText);
        publicacaoToDelete.setVisibilidadeVip(initialVip);
        publicacaoToDelete.setCategorias(initialCategories.stream()
                .map(categoriaRepository::findByNome)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
        publicacaoToDelete = publicacaoRepository.save(publicacaoToDelete);
        assertNotNull(publicacaoToDelete.getPublicacaoId(), "ID da publicação a ser excluída deve ser gerado.");
        System.out.println("DEBUG: Publicação para CT29 criada com ID: " + publicacaoToDelete.getPublicacaoId());

        // 2. Realizar a requisição DELETE
        mockMvc.perform(MockMvcRequestBuilders.delete("/publicacao/{id}", publicacaoToDelete.getPublicacaoId())
                        .param("editorId", editor.getUserId().toString()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // 3. Verificar no banco de dados se a publicação foi realmente excluída
        Optional<Publicacao> deletedPublicacao = publicacaoRepository.findById(publicacaoToDelete.getPublicacaoId());
        assertFalse(deletedPublicacao.isPresent(), "Publicação deve ter sido excluída do banco de dados.");

        // Opcional: verificar se o count de publicações diminuiu
        assertEquals(0, publicacaoRepository.count(), "Não deve haver publicações restantes no banco.");
    }


    @Test
    void ct30_TentativaExclusaoPublicacaoInexistente() throws Exception {
        Integer nonExistentId = 999;

        mockMvc.perform(MockMvcRequestBuilders.delete("/publicacao/{id}", nonExistentId)
                        .param("editorId", editor.getUserId().toString()))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Garante que nenhuma publicação foi adicionada ou afetada (embora improvável para DELETE)
        assertEquals(0, publicacaoRepository.count());
    }

    @Test
    void ct31_BloqueioExclusaoAutoriaDivergente() throws Exception {
        // 1. Preparar dados iniciais: Publicação de um EDITOR_ORIGINAL
        String initialTitle = "Publicação do Editor Original CT31";
        LocalDateTime initialDate = LocalDateTime.now().minusDays(1);
        String initialText = LONG_TEXT;
        List<String> initialCategories = Arrays.asList("Esporte");
        Boolean initialVip = false;

        // Criar e salvar uma publicação pelo 'editor' padrão do teste (editor original)
        Publicacao publicacaoOriginal = new Publicacao();
        publicacaoOriginal.setEditor(editor);
        publicacaoOriginal.setTitulo(initialTitle);
        publicacaoOriginal.setData(initialDate);
        publicacaoOriginal.setTexto(initialText);
        publicacaoOriginal.setVisibilidadeVip(initialVip);
        publicacaoOriginal.setCategorias(initialCategories.stream()
                .map(categoriaRepository::findByNome)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
        publicacaoOriginal = publicacaoRepository.save(publicacaoOriginal);
        assertNotNull(publicacaoOriginal.getPublicacaoId(), "ID da publicação original CT31 deve ser gerado.");
        System.out.println("DEBUG: Publicação original para CT31 criada com ID: " + publicacaoOriginal.getPublicacaoId() + " pelo Editor ID: " + publicacaoOriginal.getEditor().getUserId());


        // 2. Realizar a requisição DELETE com o ID de um editor NÃO AUTORIZADO
        mockMvc.perform(MockMvcRequestBuilders.delete("/publicacao/{id}", publicacaoOriginal.getPublicacaoId())
                        .param("editorId", outroEditor.getUserId().toString()))
                .andDo(print())
                .andExpect(status().isForbidden());


        // 3. Verificar no banco de dados se a publicação NÃO foi excluída
        Optional<Publicacao> publicacaoAfterAttemptOpt = publicacaoRepository.findById(publicacaoOriginal.getPublicacaoId());
        assertTrue(publicacaoAfterAttemptOpt.isPresent(), "A publicação NÃO deve ter sido excluída do banco de dados.");

        // Opcional: verificar se os dados da publicação permaneceram inalterados
        Publicacao publicacaoAfterAttempt = publicacaoAfterAttemptOpt.get();
        assertEquals(initialTitle, publicacaoAfterAttempt.getTitulo());
        assertEquals(initialText, publicacaoAfterAttempt.getTexto());
        assertEquals(initialVip, publicacaoAfterAttempt.isVisibilidadeVip());
        assertEquals(editor.getUserId(), publicacaoAfterAttempt.getEditor().getUserId());
    }




}