package com.ajmv.altoValeNewsBackend.sistema;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PublicacaoSistemaTest {

    static {
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        System.setProperty("webdriver.chrome.silentOutput", "true");
    }


    @Test
    public void devePublicarNoticiaComSucesso() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            // 1. Acessa a home
            driver.get("http://localhost:5173/");

            // 2. Abre modal de login
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();

            // 3. Espera o campo de email estar visível
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

            // 4. Preenche o formulário
            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            // 5. Clica no botão "Entrar"
            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

// 6. Clica no botão "Painel do Editor"
            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

// 7. Aguarda a tela de publicação carregar e preenche título
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("titulo")));
            driver.findElement(By.name("titulo")).sendKeys("Notícia publicada por teste Selenium");

// 8. Preenche o editor de texto Quill.js
            WebElement campoTexto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ql-editor[contenteditable='true']")));
            campoTexto.sendKeys("Essa notícia foi publicada via teste de sistema com Selenium.");

            // 8. Upload de mídia
            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");
            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

            // 9. Categoria e VIP
            WebElement categoriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("categorias")));
            categoriaInput.clear();
            categoriaInput.sendKeys("Política");

// Marca a visibilidade VIP se ainda não estiver marcado
            WebElement vip = driver.findElement(By.name("visibilidadeVip"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vip);
            Thread.sleep(500); // dá um tempinho pro scroll
            if (!vip.isSelected()) vip.click();


            // 10. Publica a notícia
            WebElement btnPublicar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Publicar']")));
            btnPublicar.click();

            // 11. Verifica feedback de sucesso
            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.alert.alert-success")));
            assert msg.getText().contains("Publicação criada com sucesso!");

            System.out.println("✅ Teste de publicação pela interface passou com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            assert false : "❌ Falha no teste de sistema: " + e.getMessage();
        } finally {
            Thread.sleep(3000);
            driver.quit();
        }
    }

    @Test
    public void deveExibirErroAoDeixarTituloEmBranco() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 1. Acessa a home
            driver.get("http://localhost:5173/");

            // 2. Abre modal de login
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();

            // 3. Espera o campo de email estar visível
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

            // 4. Preenche o formulário
            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            // 5. Clica no botão "Entrar"
            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

// 6. Clica no botão "Painel do Editor"
            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

            // 5. Aguarda o campo de texto e preenche (título deixado em branco)
            WebElement campoTexto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ql-editor[contenteditable='true']")));
            campoTexto.sendKeys("Teste de titulo em branco via teste de sistema com Selenium.");

            // 6. Upload de imagem e vídeo
            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");

            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

            // 9. Categoria e VIP
            WebElement categoriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("categorias")));
            categoriaInput.clear();
            categoriaInput.sendKeys("Política");

// Marca a visibilidade VIP se ainda não estiver marcado
            WebElement vip = driver.findElement(By.name("visibilidadeVip"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vip);
            Thread.sleep(500); // dá um tempinho pro scroll
            if (!vip.isSelected()) vip.click();

            // 10. Clica no botão "Publicar"
            WebElement btnPublicar = driver.findElement(By.xpath("//button[text()='Publicar']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnPublicar);
            Thread.sleep(300);
            btnPublicar.click();

// 11. Verifica se o campo título continua com foco (não foi enviado)
            WebElement tituloInput = driver.findElement(By.name("titulo"));
            boolean temFoco = (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return document.activeElement === arguments[0]", tituloInput);

            assert temFoco : "❌ Campo 'Título' não ficou com foco após tentativa de envio inválido";

            System.out.println("✅ Validação de campo obrigatório funcionou: Título vazio bloqueou envio.");


            System.out.println("✅ Teste de campo obrigatório passou!");

        } catch (Exception e) {
            e.printStackTrace();
            assert false : "❌ Falha no teste de campo obrigatório: " + e.getMessage();
        } finally {
            Thread.sleep(3000);
            driver.quit();
        }
    }

    @Test
    public void deveExibirErroAoDeixarTextoEmBranco() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 1. Acessa a home
            driver.get("http://localhost:5173/");

            // 2. Abre modal de login
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();

            // 3. Espera o campo de email estar visível
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

            // 4. Preenche o formulário
            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            // 5. Clica no botão "Entrar"
            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

            // 6. Clica no botão "Painel do Editor"
            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

            // 7. Preenche o título, mas deixa o texto vazio
            WebElement campoTitulo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("titulo")));
            campoTitulo.sendKeys("Título preenchido mas texto deixado em branco");

            // 8. Upload de imagem e vídeo
            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");

            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

            // 9. Categoria e VIP
            WebElement categoriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("categorias")));
            categoriaInput.clear();
            categoriaInput.sendKeys("Política");

            WebElement vip = driver.findElement(By.name("visibilidadeVip"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vip);
            Thread.sleep(500);
            if (!vip.isSelected()) vip.click();

            // 10. Clica no botão "Publicar"
            WebElement btnPublicar = driver.findElement(By.xpath("//button[text()='Publicar']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnPublicar);
            Thread.sleep(300);
            btnPublicar.click();

            // 11. Verifica se a mensagem de erro está visível
            WebElement msgErro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.alert.alert-danger")));
            String mensagem = msgErro.getText();
            assert mensagem.contains("Erro ao criar a publicação")
                    : "❌ Mensagem de erro inesperada: " + mensagem;

            System.out.println("✅ Validação de campo obrigatório funcionou: Texto vazio bloqueou envio com mensagem de erro.");
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "❌ Falha no teste de campo obrigatório: " + e.getMessage();
        } finally {
            Thread.sleep(3000);
            driver.quit();
        }
    }

    @Test
    public void deveExibirErroAoDeixarCategoriaEmBranco() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 1. Acessa a home
            driver.get("http://localhost:5173/");

            // 2. Abre modal de login
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();

            // 3. Faz login
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

            // 4. Vai até o painel do editor
            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

            // 5. Preenche título e texto
            driver.findElement(By.name("titulo")).sendKeys("Teste com categoria vazia");

            WebElement campoTexto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ql-editor[contenteditable='true']")));
            campoTexto.sendKeys("Este teste verifica a validação do campo de categoria.");

            // 6. Upload de imagem e vídeo
            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");
            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

            // 7. Categoria deixada em branco — apenas dá foco
            WebElement categoriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("categorias")));
            categoriaInput.clear(); // já estará vazio, mas por via das dúvidas

            // Marca a visibilidade VIP se ainda não estiver marcada
            WebElement vip = driver.findElement(By.name("visibilidadeVip"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vip);
            wait.until(ExpectedConditions.elementToBeClickable(vip));
            if (!vip.isSelected()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", vip);
            }

            // 9. Clica em "Publicar"
            WebElement btnPublicar = driver.findElement(By.xpath("//button[text()='Publicar']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnPublicar);
            Thread.sleep(300);
            btnPublicar.click();

            // 10. Verifica se o campo de categoria recebeu o foco após falha
            boolean temFoco = (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return document.activeElement === arguments[0]", categoriaInput);

            assert temFoco : "❌ Campo 'Categoria' não recebeu foco após tentativa de envio inválido";
            System.out.println("✅ Validação de campo obrigatório funcionou: Categoria vazia bloqueou envio.");

        } catch (Exception e) {
            e.printStackTrace();
            assert false : "❌ Falha no teste de categoria obrigatória: " + e.getMessage();
        } finally {
            Thread.sleep(3000);
            driver.quit();
        }
    }

    @Test
    public void deveExibirErroAoPreencherTextoComMaisDe250Caracteres() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 1. Acessa a home
            driver.get("http://localhost:5173/");

            // 2. Abre modal de login
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();

            // 3. Espera o campo de email estar visível
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

            // 4. Preenche o formulário
            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            // 5. Clica no botão "Entrar"
            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

            // 6. Clica no botão "Painel do Editor"
            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

            // 7. Preenche o título normalmente
            WebElement campoTitulo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("titulo")));
            campoTitulo.sendKeys("Notícia com texto longo demais");

            // 8. Preenche o editor de texto com +250 caracteres
            WebElement campoTexto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ql-editor[contenteditable='true']")));
            campoTexto.sendKeys("Selenium teste ".repeat(30)); // 30 * 16 = 480+ chars

            // 9. Upload de imagem e vídeo
            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");

            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

            // 10. Categoria e VIP
            WebElement categoriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("categorias")));
            categoriaInput.clear();
            categoriaInput.sendKeys("Política");

            WebElement vip = driver.findElement(By.name("visibilidadeVip"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vip);
            wait.until(ExpectedConditions.elementToBeClickable(vip));
            if (!vip.isSelected()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", vip);
            }

            // 11. Clica no botão "Publicar"
            WebElement btnPublicar = driver.findElement(By.xpath("//button[text()='Publicar']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnPublicar);
            Thread.sleep(300);
            btnPublicar.click();

            // 12. Verifica se a mensagem de erro está visível
            WebElement msgErro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.alert.alert-danger")));
            String mensagem = msgErro.getText();
            assert mensagem.contains("Erro ao criar a publicação")
                    : "❌ Mensagem de erro inesperada: " + mensagem;

            System.out.println("✅ Texto com mais de 250 caracteres foi corretamente rejeitado com mensagem de erro.");
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "❌ Falha no teste de texto longo: " + e.getMessage();
        } finally {
            Thread.sleep(3000);
            driver.quit();
        }
    }

}
