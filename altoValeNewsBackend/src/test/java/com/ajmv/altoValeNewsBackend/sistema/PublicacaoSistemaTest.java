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
            driver.get("http://localhost:5173/");
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("titulo")));
            driver.findElement(By.name("titulo")).sendKeys("Notícia publicada por teste Selenium");

            WebElement campoTexto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ql-editor[contenteditable='true']")));
            campoTexto.sendKeys("Essa notícia foi publicada via teste de sistema com Selenium.");

            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");
            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

            WebElement categoriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("categorias")));
            categoriaInput.clear();
            categoriaInput.sendKeys("Política");

            WebElement vip = driver.findElement(By.name("visibilidadeVip"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vip);
            Thread.sleep(500);
            if (!vip.isSelected()) vip.click();


            WebElement btnPublicar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Publicar']")));
            btnPublicar.click();

            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.alert.alert-success")));
            assert msg.getText().contains("Publicação criada com sucesso!");

            System.out.println("Teste de publicação pela interface passou com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Falha no teste de sistema: " + e.getMessage();
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
            driver.get("http://localhost:5173/");

            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

            WebElement campoTexto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ql-editor[contenteditable='true']")));
            campoTexto.sendKeys("Teste de titulo em branco via teste de sistema com Selenium.");

            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");

            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

            WebElement categoriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("categorias")));
            categoriaInput.clear();
            categoriaInput.sendKeys("Política");

            WebElement vip = driver.findElement(By.name("visibilidadeVip"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vip);
            Thread.sleep(500); // dá um tempinho pro scroll
            if (!vip.isSelected()) vip.click();

            WebElement btnPublicar = driver.findElement(By.xpath("//button[text()='Publicar']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnPublicar);
            Thread.sleep(300);
            btnPublicar.click();

            WebElement tituloInput = driver.findElement(By.name("titulo"));
            boolean temFoco = (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return document.activeElement === arguments[0]", tituloInput);

            assert temFoco : "Campo 'Título' não ficou com foco após tentativa de envio inválido";

            System.out.println("Validação de campo obrigatório funcionou: Título vazio bloqueou envio.");


            System.out.println("Teste de campo obrigatório passou!");

        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Falha no teste de campo obrigatório: " + e.getMessage();
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
            driver.get("http://localhost:5173/");

            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

            WebElement campoTitulo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("titulo")));
            campoTitulo.sendKeys("Título preenchido mas texto deixado em branco");

            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");

            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

            WebElement categoriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("categorias")));
            categoriaInput.clear();
            categoriaInput.sendKeys("Política");

            WebElement vip = driver.findElement(By.name("visibilidadeVip"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vip);
            Thread.sleep(500);
            if (!vip.isSelected()) vip.click();

            WebElement btnPublicar = driver.findElement(By.xpath("//button[text()='Publicar']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnPublicar);
            Thread.sleep(300);
            btnPublicar.click();

            WebElement msgErro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.alert.alert-danger")));
            String mensagem = msgErro.getText();
            assert mensagem.contains("Erro ao criar a publicação")
                    : "Mensagem de erro inesperada: " + mensagem;

            System.out.println("Validação de campo obrigatório funcionou: Texto vazio bloqueou envio com mensagem de erro.");
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Falha no teste de campo obrigatório: " + e.getMessage();
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
            driver.get("http://localhost:5173/");

            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

            driver.findElement(By.name("titulo")).sendKeys("Teste com categoria vazia");

            WebElement campoTexto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ql-editor[contenteditable='true']")));
            campoTexto.sendKeys("Este teste verifica a validação do campo de categoria.");

            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");
            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

            WebElement categoriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("categorias")));
            categoriaInput.clear();

            WebElement vip = driver.findElement(By.name("visibilidadeVip"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vip);
            wait.until(ExpectedConditions.elementToBeClickable(vip));
            if (!vip.isSelected()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", vip);
            }

            WebElement btnPublicar = driver.findElement(By.xpath("//button[text()='Publicar']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnPublicar);
            Thread.sleep(300);
            btnPublicar.click();

            boolean temFoco = (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return document.activeElement === arguments[0]", categoriaInput);

            assert temFoco : "Campo 'Categoria' não recebeu foco após tentativa de envio inválido";
            System.out.println("Validação de campo obrigatório funcionou: Categoria vazia bloqueou envio.");

        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Falha no teste de categoria obrigatória: " + e.getMessage();
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
            driver.get("http://localhost:5173/");

            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cadastro/Login")));
            loginBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

            driver.findElement(By.name("email")).sendKeys("admin@mail.com");
            driver.findElement(By.name("senha")).sendKeys("teste");

            WebElement entrarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form button[type='submit']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", entrarBtn);

            WebElement painelEditor = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Painel do Editor")));
            painelEditor.click();

            WebElement campoTitulo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("titulo")));
            campoTitulo.sendKeys("Notícia com texto longo demais");

            WebElement campoTexto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ql-editor[contenteditable='true']")));
            campoTexto.sendKeys("Selenium teste ".repeat(30)); // 30 * 16 = 480+ chars

            File imagem = new File("drivers/imagem_teste.png");
            File video = new File("drivers/video_teste.mp4");

            driver.findElement(By.name("imagem")).sendKeys(imagem.getAbsolutePath());
            driver.findElement(By.name("video")).sendKeys(video.getAbsolutePath());

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

            WebElement btnPublicar = driver.findElement(By.xpath("//button[text()='Publicar']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnPublicar);
            Thread.sleep(300);
            btnPublicar.click();

            WebElement msgErro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.alert.alert-danger")));
            String mensagem = msgErro.getText();
            assert mensagem.contains("Erro ao criar a publicação")
                    : "Mensagem de erro inesperada: " + mensagem;

            System.out.println("Texto com mais de 250 caracteres foi corretamente rejeitado com mensagem de erro.");
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Falha no teste de texto longo: " + e.getMessage();
        } finally {
            Thread.sleep(3000);
            driver.quit();
        }
    }

}
