package com.ajmv.altoValeNewsBackend.controller;

import com.ajmv.altoValeNewsBackend.model.TipoUsuario;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UsuarioControllerIntegrationTest {

    @Autowired private UsuarioController usuarioController;
    @Autowired private LoginController loginController;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Nested
    @DisplayName("Testes de CRUD de Usuário")
    class CrudUsuarioTests {

        @Test
        @DisplayName("RT01-CT01: Deve cadastrar um usuário com sucesso via POST")
        void createUsuario_whenValidUser_shouldReturn201AndUser() {
            Usuario usuario = new Usuario();
            usuario.setEmail("crud.user@example.com");
            usuario.setCpf("11122233344");
            usuario.setSenha("senha@123");

            ResponseEntity<?> response = usuarioController.createUsuario(usuario);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertInstanceOf(Usuario.class, response.getBody());

            Usuario usuarioCriado = (Usuario) response.getBody();
            assertEquals("crud.user@example.com", usuarioCriado.getEmail());
            assertNotNull(usuarioCriado.getUserId());
        }

        @Test
        @DisplayName("RT02-CT02: Deve deletar um usuário existente via DELETE")
        void deleteUsuario_whenUserExists_shouldReturn204() {
            // Setup: criar usuário direto no repository
            Usuario usuario = new Usuario();
            usuario.setEmail("delete.user@example.com");
            usuario.setCpf("55566677788");
            usuario.setSenhahash(passwordEncoder.encode("senhaDelete"));
            usuario.setTipo(TipoUsuario.USUARIO);
            usuario.criarAssinatura();
            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            // Ação: chamar método do controller diretamente
            ResponseEntity<?> response = usuarioController.deleteUsuario(usuarioSalvo.getUserId());

            // Verificação
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertFalse(usuarioRepository.existsById(usuarioSalvo.getUserId()));
        }

        @Test
        @DisplayName("RT03-CT03: Deve atualizar parcialmente um usuário via PATCH")
        void partialUpdateUsuario_whenUserExists_shouldReturn200AndUpdatedUser() {
            // Setup
            Usuario usuario = new Usuario();
            usuario.setEmail("patch.user@example.com");
            usuario.setCpf("12312312312");
            usuario.setSenhahash(passwordEncoder.encode("senhaPatch"));
            usuario.setTipo(TipoUsuario.USUARIO);
            usuario.criarAssinatura();
            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            // Dados para atualização
            Usuario dadosAtualizacao = new Usuario();
            dadosAtualizacao.setEndereco("Avenida Nova, 456");
            dadosAtualizacao.setCidade("Blumenau");

            // Ação: chamar método do controller diretamente
            ResponseEntity<Usuario> response = usuarioController.partialUpdateUsuario(
                    usuarioSalvo.getUserId(),
                    dadosAtualizacao
            );

            // Verificação
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Avenida Nova, 456", response.getBody().getEndereco());
            assertEquals("Blumenau", response.getBody().getCidade());
        }
    }

    @Nested
    @DisplayName("RT04: Testes de Integração de Login (RF02)")
    class LoginIntegrationTests {
        private Usuario usuarioLogin;

        @BeforeEach
        void setUp() {
            usuarioLogin = new Usuario();
            usuarioLogin.setEmail("login.user@example.com");
            usuarioLogin.setCpf("98765432100");
            usuarioLogin.setSenhahash(passwordEncoder.encode("senhaLogin"));
            usuarioLogin.setTipo(TipoUsuario.USUARIO);
            usuarioLogin.criarAssinatura();
            usuarioRepository.save(usuarioLogin);
        }

        @Test
        @DisplayName("CT04: Deve efetuar login com sucesso com credenciais válidas")
        void login_withValidCredentials_shouldReturn200AndToken() {
            // Criar LoginRequest
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("login.user@example.com");
            loginRequest.setSenha("senhaLogin");

            // Ação: chamar método do controller diretamente
            ResponseEntity<?> response = loginController.login(loginRequest);

            // Verificação
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            // Verificar se a resposta contém JWT (assumindo que retorna AuthenticationResponse)
        }

        @Test
        @DisplayName("CT05: Deve impedir login com senha errada")
        void login_withInvalidPassword_shouldReturn401() {
            // Criar LoginRequest com senha errada
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("login.user@example.com");
            loginRequest.setSenha("senhaErrada");

            // Ação: chamar método do controller diretamente
            ResponseEntity<?> response = loginController.login(loginRequest);

            // Verificação
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        @Test
        @DisplayName("CT06: Deve impedir login com e-mail não cadastrado")
        void login_withNonExistentEmail_shouldReturn401() {
            // Criar LoginRequest com email inexistente
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("naoexiste@example.com");
            loginRequest.setSenha("qualquersenha");

            // Ação: chamar método do controller diretamente
            ResponseEntity<?> response = loginController.login(loginRequest);

            // Verificação
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
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
            admin.criarAssinatura();
            admin = usuarioRepository.save(admin);

            targetUser = new Usuario();
            targetUser.setEmail("target@example.com");
            targetUser.setCpf("00000000022");
            targetUser.setSenhahash(passwordEncoder.encode("target123"));
            targetUser.setTipo(TipoUsuario.USUARIO);
            targetUser.criarAssinatura();
            targetUser = usuarioRepository.save(targetUser);
        }

        @Test
        @DisplayName("CT07: Admin deve conseguir alterar tipo de usuário para Administrador")
        void setTipoUsuario_byAdmin_toAdmin_shouldReturn200() {
            // Ação: chamar método do controller diretamente
            ResponseEntity<Usuario> response = usuarioController.setTipoUsuario(
                    targetUser.getUserId(),
                    3, // TipoUsuario.ADMINISTRADOR
                    admin.getUserId()
            );

            // Verificação
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(TipoUsuario.ADMINISTRADOR, response.getBody().getTipo());

            // Verificar no banco também
            Usuario updatedUser = usuarioRepository.findById(targetUser.getUserId()).get();
            assertEquals(TipoUsuario.ADMINISTRADOR, updatedUser.getTipo());
        }

        @Test
        @DisplayName("CT08: Admin deve conseguir alterar tipo de usuário para Editor")
        void setTipoUsuario_byAdmin_toEditor_shouldReturn200() {
            // Ação: chamar método do controller diretamente
            ResponseEntity<Usuario> response = usuarioController.setTipoUsuario(
                    targetUser.getUserId(),
                    2, // TipoUsuario.EDITOR
                    admin.getUserId()
            );

            // Verificação
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(TipoUsuario.EDITOR, response.getBody().getTipo());

            // Verificar no banco também
            Usuario updatedUser = usuarioRepository.findById(targetUser.getUserId()).get();
            assertEquals(TipoUsuario.EDITOR, updatedUser.getTipo());
        }

        @Test
        @DisplayName("CT09: Não deve alterar tipo se ID do Admin não existir")
        void setTipoUsuario_withNonExistentAdminId_shouldReturn404() {
            // Ação: chamar método do controller com admin inexistente
            ResponseEntity<Usuario> response = usuarioController.setTipoUsuario(
                    targetUser.getUserId(),
                    3, // TipoUsuario.ADMINISTRADOR
                    999 // Admin ID inexistente
            );

            // Verificação
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("CT10: Usuário não-admin não deve conseguir alterar tipo de usuário")
        void setTipoUsuario_byNonAdmin_shouldReturn401() {
            // Ação: tentar alterar usando um usuário comum como "admin"
            ResponseEntity<Usuario> response = usuarioController.setTipoUsuario(
                    admin.getUserId(),
                    3, // TipoUsuario.ADMINISTRADOR
                    targetUser.getUserId() // Usuário comum tentando agir como admin
            );

            // Verificação
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }
}