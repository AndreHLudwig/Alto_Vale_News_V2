package com.ajmv.altoValeNewsBackend.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserSystemTest extends BaseSeleniumTest {

    private static final String EMAIL_TESTE = "usuario.system.test@mail.com";
    private static final String SENHA_INICIAL = "senha@123";
    private static final String NOVA_SENHA = "novaSenha@123";

    @Override
    @BeforeEach
    void setUp() {
        long userCount = usuarioRepository.count();
        System.out.println("=== SETUP: Usuários no banco: " + userCount + " ===");

//        // Debug: verificar se nosso usuário de teste existe
        boolean testUserExists = usuarioRepository.findByEmail(EMAIL_TESTE) != null;
//        System.out.println("Usuário de teste existe: " + testUserExists);

        if (userCount == 0) {
            System.out.println("Criando usuários padrão...");
            createTestUsers();
        }

//        // Debug: listar usuários
//        usuarioRepository.findAll().forEach(u ->
//                System.out.println("Usuário: " + u.getEmail())
//        );

        waitForBackendReady();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.manage().window().maximize();
    }

    @Override
    @AfterEach
    void tearDown() {
//        // Debug: verificar usuários após cada teste
//        System.out.println("=== TEARDOWN: Usuários no banco: " + usuarioRepository.count() + " ===");

        if (driver != null) {
            driver.quit();
        }
    }

    private void fazerLogin(String email, String senha) {
        System.out.println("Fazendo login com: " + email);
        navigateToHome();

        WebElement loginButton = driver.findElement(By.linkText("Cadastro/Login"));
        loginButton.click();

        WebElement emailField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("email"))
        );
        WebElement senhaField = driver.findElement(By.name("senha"));

        emailField.sendKeys(email);
        senhaField.sendKeys(senha);

        WebElement loginSubmitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginSubmitButton.click();

        WebElement perfilLink = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Perfil"))
        );
        assertTrue(perfilLink.isDisplayed(), "Login deveria ter funcionado");
    }

    @Test
    @Order(1)
    @DisplayName("CT01: Verificar se o usuário consegue realizar cadastro com sucesso")
    void ct01_CadastroUsuario() {
        System.out.println("=== CT01: CADASTRO DE USUÁRIO ===");

        navigateToCadastro();

        WebElement nomeField = driver.findElement(By.name("nome"));
        WebElement sobrenomeField = driver.findElement(By.name("sobrenome"));
        WebElement emailField = driver.findElement(By.name("email"));
        WebElement cpfField = driver.findElement(By.name("cpf"));
        WebElement enderecoField = driver.findElement(By.name("endereco"));
        WebElement cidadeField = driver.findElement(By.name("cidade"));
        WebElement estadoField = driver.findElement(By.name("estado"));
        WebElement cepField = driver.findElement(By.name("cep"));
        WebElement senhaField = driver.findElement(By.name("senha"));
        WebElement confirmarSenhaField = driver.findElement(By.name("confirmarSenha"));

        nomeField.sendKeys("João");
        sobrenomeField.sendKeys("Silva");
        emailField.sendKeys(EMAIL_TESTE);
        cpfField.sendKeys("98765432109");
        enderecoField.sendKeys("Rua das Flores, 123");
        cidadeField.sendKeys("Rio do Sul");
        estadoField.sendKeys("SC");
        cepField.sendKeys("89160000");
        senhaField.sendKeys(SENHA_INICIAL);
        confirmarSenhaField.sendKeys(SENHA_INICIAL);

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        wait.until(ExpectedConditions.urlToBe(frontendUrl + "/"));
        assertEquals(frontendUrl + "/", driver.getCurrentUrl(),
                "Deveria redirecionar para a página inicial após cadastro");

//        // Debug: verificar se usuário foi criado no banco
//        try {
//            Thread.sleep(2000); // Aguardar um pouco para garantir que foi salvo
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        boolean userExists = usuarioRepository.findByEmail(EMAIL_TESTE) != null;
        assertTrue(userExists, "Usuário deveria ter sido criado no banco após cadastro");
    }

    @Test
    @Order(2)
    @DisplayName("CT02: Verificar se o usuário consegue realizar login com sucesso")
    void ct02_LoginUsuario() {
        System.out.println("=== CT02: LOGIN COM SUCESSO ===");

        boolean userExists = usuarioRepository.findByEmail(EMAIL_TESTE) != null;
        assertTrue(userExists, "Usuário deveria existir no banco antes do login");

        navigateToHome();

        WebElement loginButton = driver.findElement(By.linkText("Cadastro/Login"));
        loginButton.click();

        WebElement emailField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("email"))
        );
        WebElement senhaField = driver.findElement(By.name("senha"));

        emailField.sendKeys(EMAIL_TESTE);
        senhaField.sendKeys(SENHA_INICIAL);

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Aguardar e verificar onde fomos parar
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        WebElement perfilLink = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Perfil"))
        );
        assertTrue(perfilLink.isDisplayed(), "Link do perfil deveria estar visível após login");

        WebElement logoutButton = driver.findElement(By.linkText("Sair"));
        assertTrue(logoutButton.isDisplayed(), "Botão de logout deveria estar visível");
    }

    @Test
    @Order(3)
    @DisplayName("CT03: Verificar se o usuário consegue realizar alteração de senha com sucesso")
    void ct03_AlteracaoSenha() {
        System.out.println("=== CT03: ALTERAÇÃO DE SENHA ===");

        boolean userExists = usuarioRepository.findByEmail(EMAIL_TESTE) != null;
        assertTrue(userExists, "Usuário deveria existir no banco antes da alteração de senha");

        fazerLogin(EMAIL_TESTE, SENHA_INICIAL);

        navigateToEditarPerfil();

        WebElement novaSenhaField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("senha"))
        );
        WebElement confirmarNovaSenhaField = driver.findElement(By.name("confirmarSenha"));

        novaSenhaField.sendKeys(NOVA_SENHA);
        confirmarNovaSenhaField.sendKeys(NOVA_SENHA);

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement novaSenhaFieldCheck = driver.findElement(By.name("senha"));
        WebElement confirmarNovaSenhaFieldCheck = driver.findElement(By.name("confirmarSenha"));

        assertTrue(novaSenhaFieldCheck.getAttribute("value").isEmpty() ||
                        confirmarNovaSenhaFieldCheck.getAttribute("value").isEmpty(),
                "Campos de senha deveriam ser limpos após alteração bem-sucedida");

        WebElement logoutButton = driver.findElement(By.linkText("Sair"));
        logoutButton.click();

        WebElement loginButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Cadastro/Login"))
        );
        loginButton.click();

        WebElement emailField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("email"))
        );
        WebElement senhaField = driver.findElement(By.name("senha"));

        emailField.sendKeys(EMAIL_TESTE);
        senhaField.sendKeys(NOVA_SENHA); // Agora usa a nova senha

        WebElement loginSubmitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginSubmitButton.click();

        // Debug: verificar onde estamos após login
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        WebElement perfilLinkFinal = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Perfil"))
        );
        assertTrue(perfilLinkFinal.isDisplayed(), "Login com nova senha deveria funcionar");
    }

    @Test
    @Order(4)
    @DisplayName("CT04: Verificar se os dados cadastrais são atualizados com sucesso")
    void ct04_AtualizacaoDadosCadastrais() {
        System.out.println("=== CT04: ATUALIZAÇÃO DE DADOS CADASTRAIS ===");

        boolean userExists = usuarioRepository.findByEmail(EMAIL_TESTE) != null;
        assertTrue(userExists, "Usuário deveria existir no banco antes da atualização de dados");

        fazerLogin(EMAIL_TESTE, NOVA_SENHA);

        navigateToEditarPerfil();

        WebElement enderecoField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("endereco"))
        );
        WebElement cidadeField = driver.findElement(By.name("cidade"));
        WebElement estadoField = driver.findElement(By.name("estado"));
        WebElement cepField = driver.findElement(By.name("cep"));

        enderecoField.clear();
        enderecoField.sendKeys("Rua Nova, 456");

        cidadeField.clear();
        cidadeField.sendKeys("Blumenau");

        estadoField.clear();
        estadoField.sendKeys("SC");

        cepField.clear();
        cepField.sendKeys("89010000");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        WebElement enderecoAtualizado = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("endereco"))
        );

        // Se ainda não atualizou, refresh da página para garantir
        if (!"Rua Nova, 456".equals(enderecoAtualizado.getAttribute("value"))) {
            driver.navigate().refresh();
            enderecoAtualizado = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.name("endereco"))
            );
        }

        assertEquals("Rua Nova, 456", enderecoAtualizado.getAttribute("value"),
                "Endereço deveria estar atualizado");

        WebElement cidadeAtualizada = driver.findElement(By.name("cidade"));
        assertEquals("Blumenau", cidadeAtualizada.getAttribute("value"),
                "Cidade deveria estar atualizada");

    }
}