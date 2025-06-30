package com.ajmv.altoValeNewsBackend.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class IntegratedSystemTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Jornada Completa: Cadastro -> Login -> Edição de Perfil -> Alteração de Senha")
    void testJornadaCompletaUsuario() {

        // ============================================================
        // ETAPA 1: CADASTRO DE USUÁRIO (CT01)
        // ============================================================

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

        String emailTeste = "usuario.jornada@mail.com";
        String senhaInicial = "senha@123";

        nomeField.sendKeys("João");
        sobrenomeField.sendKeys("Silva");
        emailField.sendKeys(emailTeste);
        cpfField.sendKeys("12345678909");
        enderecoField.sendKeys("Rua das Flores, 123");
        cidadeField.sendKeys("Rio do Sul");
        estadoField.sendKeys("SC");
        cepField.sendKeys("89160000");
        senhaField.sendKeys(senhaInicial);
        confirmarSenhaField.sendKeys(senhaInicial);

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Verificar redirecionamento para página inicial (sem login automático)
        wait.until(ExpectedConditions.urlToBe(frontendUrl + "/"));

        // ============================================================
        // ETAPA 2: LOGIN COM USUÁRIO RECÉM-CADASTRADO (CT02)
        // ============================================================

        WebElement loginButton = driver.findElement(By.linkText("Cadastro/Login"));
        loginButton.click();

        WebElement loginEmailField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("email"))
        );
        WebElement loginSenhaField = driver.findElement(By.name("senha"));

        loginEmailField.sendKeys(emailTeste);
        loginSenhaField.sendKeys(senhaInicial);

        WebElement loginSubmitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginSubmitButton.click();

        WebElement perfilLink = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Perfil"))
        );
        assertTrue(perfilLink.isDisplayed(), "Link do perfil deveria estar visível após login");

        WebElement logoutButton = driver.findElement(By.linkText("Sair"));
        assertTrue(logoutButton.isDisplayed(), "Botão de logout deveria estar visível");

        // ============================================================
        // ETAPA 3: EDIÇÃO DO PERFIL (CT04)
        // ============================================================

        navigateToEditarPerfil();

        WebElement editEnderecoField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("endereco"))
        );
        WebElement editCidadeField = driver.findElement(By.name("cidade"));
        WebElement editEstadoField = driver.findElement(By.name("estado"));
        WebElement editCepField = driver.findElement(By.name("cep"));

        editEnderecoField.clear();
        editEnderecoField.sendKeys("Rua Nova, 456");

        editCidadeField.clear();
        editCidadeField.sendKeys("Blumenau");

        editEstadoField.clear();
        editEstadoField.sendKeys("SC");

        editCepField.clear();
        editCepField.sendKeys("89010000");

        WebElement editSubmitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        editSubmitButton.click();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verificar se dados foram atualizados checando os valores dos campos
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

        System.out.println("✅ Perfil editado com sucesso");

        // ============================================================
        // ETAPA 4: ALTERAÇÃO DE SENHA (CT03)
        // ============================================================

        String novaSenha = "novaSenha@123";

        WebElement novaSenhaField = driver.findElement(By.name("senha"));
        WebElement confirmarNovaSenhaField = driver.findElement(By.name("confirmarSenha"));

        novaSenhaField.sendKeys(novaSenha);
        confirmarNovaSenhaField.sendKeys(novaSenha);

        WebElement senhaSubmitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        senhaSubmitButton.click();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verificar se os campos de senha foram limpos (indicando sucesso)
        WebElement novaSenhaFieldCheck = driver.findElement(By.name("senha"));
        WebElement confirmarNovaSenhaFieldCheck = driver.findElement(By.name("confirmarSenha"));

        assertTrue(novaSenhaFieldCheck.getAttribute("value").isEmpty() ||
                        confirmarNovaSenhaFieldCheck.getAttribute("value").isEmpty(),
                "Campos de senha deveriam ser limpos após alteração bem-sucedida");

        // ============================================================
        // ETAPA 5: TESTE DE LOGIN COM NOVA SENHA
        // ============================================================

        WebElement finalLogoutButton = driver.findElement(By.linkText("Sair"));
        finalLogoutButton.click();

        WebElement finalLoginButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Cadastro/Login"))
        );
        finalLoginButton.click();

        WebElement finalEmailField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("email"))
        );
        WebElement finalSenhaField = driver.findElement(By.name("senha"));

        finalEmailField.sendKeys(emailTeste);
        finalSenhaField.sendKeys(novaSenha);

        WebElement finalSubmitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        finalSubmitButton.click();

        WebElement finalPerfilLink = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Perfil"))
        );
        assertTrue(finalPerfilLink.isDisplayed(), "Login com nova senha deveria funcionar");

    }
}