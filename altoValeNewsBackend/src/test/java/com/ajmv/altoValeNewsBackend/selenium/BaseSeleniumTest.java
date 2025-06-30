package com.ajmv.altoValeNewsBackend.selenium;

import com.ajmv.altoValeNewsBackend.model.TipoUsuario;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    protected final String frontendUrl = "http://localhost:5173";

    @BeforeEach
    void setUp() {
        createTestUsers();
        waitForBackendReady();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.manage().window().maximize();
    }


    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();

        if (driver != null) {
            driver.quit();
        }
    }

    private void waitForBackendReady() {
        System.out.println("Aguardando backend estar pronto...");
        for (int i = 0; i < 30; i++) {
            try {
                java.net.URL url = new java.net.URL("http://localhost:8080/publicacao");
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(1000);
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == 200) {
                    System.out.println("Backend pronto!");
                    return;
                }
            } catch (Exception e) {
                // Continuar tentando
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        throw new RuntimeException("Backend não ficou pronto em 30 segundos");
    }

    protected void createTestUsers() {
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setSobrenome("Silva");
        usuario.setEmail("joao.silva@mail.com");
        usuario.setCpf("08140508080");
        usuario.setEndereco("Rua das Flores, 123");
        usuario.setCidade("Rio do Sul");
        usuario.setEstado("SC");
        usuario.setCep("89160000");
        usuario.setSenhahash(passwordEncoder.encode("senha@123"));
        usuario.setTipo(TipoUsuario.USUARIO);
        usuario.criarAssinatura();
        usuarioRepository.save(usuario);

        Usuario admin = new Usuario();
        admin.setNome("Admin");
        admin.setSobrenome("Sistema");
        admin.setEmail("admin@mail.com");
        admin.setCpf("57266056918");
        admin.setEndereco("Rua dos Admins");
        admin.setCidade("Ibirama");
        admin.setEstado("SC");
        admin.setCep("89140000");
        admin.setSenhahash(passwordEncoder.encode("senha@123"));
        admin.setTipo(TipoUsuario.ADMINISTRADOR);
        admin.criarAssinatura();
        usuarioRepository.save(admin);
    }

    protected void navigateToHome() {
        driver.get(frontendUrl);
        sleep(200);
    }

    protected void navigateToCadastro() {
        driver.get(frontendUrl + "/cadastro");
        sleep(200);
    }

    protected void navigateToEditarPerfil() {
        driver.get(frontendUrl + "/editar-perfil");
        sleep(200);
    }

    protected void navigateToPainelAdmin() {
        driver.get(frontendUrl + "/painel-admin");
        sleep(200);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}