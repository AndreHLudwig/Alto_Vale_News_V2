package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.TipoUsuario;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    // Método para limpar o banco após @Nested
    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Nested
    @DisplayName("Testes de CRUD de Usuário")
    class CrudUsuarioTests {
        @Test
        @DisplayName("RT01-CT01: Deve cadastrar um usuário com sucesso via POST")
        void createUsuario_whenValidUser_shouldReturn201AndUser() throws Exception {
            Usuario usuario = new Usuario();
            usuario.setEmail("crud.user@example.com");
            usuario.setCpf("11122233344");
            usuario.setSenha("senha123");

            mockMvc.perform(post("/usuario")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(usuario)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email", is("crud.user@example.com")));
        }

        @Test
        @DisplayName("RT02-CT02: Deve deletar um usuário existente via DELETE")
        void deleteUsuario_whenUserExists_shouldReturn204() throws Exception {
            Usuario usuario = new Usuario();
            usuario.setEmail("delete.user@example.com");
            usuario.setCpf("55566677788");
            usuario.setSenhahash(passwordEncoder.encode("senhaDelete"));
            usuario.setTipo(TipoUsuario.USUARIO);
            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            mockMvc.perform(delete("/usuario/" + usuarioSalvo.getUserId()))
                    .andExpect(status().isNoContent());

            assertFalse(usuarioRepository.existsById(usuarioSalvo.getUserId()));
        }

        @Test
        @DisplayName("RT03-CT03: Deve atualizar parcialmente um usuário via PATCH")
        void partialUpdateUsuario_whenUserExists_shouldReturn200AndUpdatedUser() throws Exception {
            Usuario usuario = new Usuario();
            usuario.setEmail("patch.user@example.com");
            usuario.setCpf("12312312312");
            usuario.setSenhahash(passwordEncoder.encode("senhaPatch"));
            usuario.setTipo(TipoUsuario.USUARIO);
            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            String jsonUpdate = "{\"endereco\":\"Avenida Nova, 456\", \"cidade\":\"Blumenau\"}";

            mockMvc.perform(patch("/usuario/" + usuarioSalvo.getUserId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonUpdate))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.endereco", is("Avenida Nova, 456")));
        }
    }

    @Nested
    @DisplayName("RT04: Testes de Integração de Login (RF02)")
    class LoginIntegrationTests {
        @BeforeEach
        void setUp() {
            Usuario usuarioLogin = new Usuario();
            usuarioLogin.setEmail("login.user@example.com");
            usuarioLogin.setCpf("98765432100");
            usuarioLogin.setSenhahash(passwordEncoder.encode("senhaLogin"));
            usuarioLogin.setTipo(TipoUsuario.USUARIO);
            usuarioRepository.save(usuarioLogin);
        }

        @Test
        @DisplayName("CT04: Deve efetuar login com sucesso com credenciais válidas")
        void login_withValidCredentials_shouldReturn200AndToken() throws Exception {
            String loginRequestBody = "{\"email\":\"login.user@example.com\", \"senha\":\"senhaLogin\"}";
            mockMvc.perform(post("/usuario/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.jwt", notNullValue()));
        }
        
        @Test
        @DisplayName("CT05: Deve impedir login com senha errada")
        void login_withInvalidPassword_shouldReturn401() throws Exception {
            String loginRequestBody = "{\"email\":\"login.user@example.com\", \"senha\":\"senhaErrada\"}";
            mockMvc.perform(post("/usuario/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestBody))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("CT06: Deve impedir login com e-mail não cadastrado")
        void login_withNonExistentEmail_shouldReturn401() throws Exception {
            String loginRequestBody = "{\"email\":\"naoexiste@example.com\", \"senha\":\"qualquersenha\"}";

            mockMvc.perform(post("/usuario/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestBody))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("RT05: Testes de Integração de Funções de Admin (RF05, RF06, RF07)")
    class AdminFunctionsIntegrationTests {
        private Usuario admin;
        private Usuario targetUser;

        @BeforeEach
        void setUp() {
            admin = new Usuario();
            admin.setEmail("admin@example.com");
            admin.setCpf("00000000011");
            admin.setSenhahash(passwordEncoder.encode("admin123"));
            admin.setTipo(TipoUsuario.ADMINISTRADOR);
            admin = usuarioRepository.save(admin);
            
            targetUser = new Usuario();
            targetUser.setEmail("target@example.com");
            targetUser.setCpf("00000000022");
            targetUser.setSenhahash(passwordEncoder.encode("target123"));
            targetUser.setTipo(TipoUsuario.USUARIO);
            targetUser = usuarioRepository.save(targetUser);
        }

        @Test
        @DisplayName("CT07: Admin deve conseguir alterar tipo de usuário para Administrador")
        void setTipoUsuario_byAdmin_toAdmin_shouldReturn200() throws Exception {
            mockMvc.perform(put("/usuario/" + targetUser.getUserId() + "/tipo")
                    .header("Admin-Id", admin.getUserId())
                    .param("tipoUsuario", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tipo", is("ADMINISTRADOR")));
            
            Usuario updatedUser = usuarioRepository.findById(targetUser.getUserId()).get();
            assertEquals(TipoUsuario.ADMINISTRADOR, updatedUser.getTipo());
        }

        @Test
        @DisplayName("Não deve alterar tipo se ID do Admin não existir")
        void setTipoUsuario_withNonExistentAdminId_shouldReturn404() throws Exception {
            mockMvc.perform(put("/usuario/" + targetUser.getUserId() + "/tipo")
                    .header("Admin-Id", 999)
                    .param("tipoUsuario", "3"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Usuário não-admin não deve conseguir alterar tipo de usuário")
        void setTipoUsuario_byNonAdmin_shouldReturn401() throws Exception {
            mockMvc.perform(put("/usuario/" + admin.getUserId() + "/tipo")
                    .header("Admin-Id", targetUser.getUserId())
                    .param("tipoUsuario", "3"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("CT08: Admin deve conseguir alterar tipo de usuário para Editor")
        void setTipoUsuario_byAdmin_toEditor_shouldReturn200() throws Exception {
            mockMvc.perform(put("/usuario/" + targetUser.getUserId() + "/tipo")
                    .header("Admin-Id", admin.getUserId())
                    .param("tipoUsuario", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tipo", is("EDITOR")));

            Usuario updatedUser = usuarioRepository.findById(targetUser.getUserId()).get();
            assertEquals(TipoUsuario.EDITOR, updatedUser.getTipo());
        }
    }
}