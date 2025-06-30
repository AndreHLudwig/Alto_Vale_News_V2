package com.ajmv.altoValeNewsBackend.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class AdminSystemTest extends BaseSeleniumTest {

    @Test
    @DisplayName("CT05: Verificar se o Administrador consegue realizar alteração de tipo de usuário com sucesso")
    void ct05_AlteracaoTipoUsuario() {
        System.out.println("=== CT05: ALTERAÇÃO DE TIPO DE USUÁRIO ===");

        navigateToHome();

        WebElement loginButton = driver.findElement(By.linkText("Cadastro/Login"));
        loginButton.click();

        WebElement emailField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("email"))
        );
        WebElement senhaField = driver.findElement(By.name("senha"));

        emailField.sendKeys("admin@mail.com");
        senhaField.sendKeys("senha@123");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Painel do Admin")));

        navigateToPainelAdmin();

        WebElement tabelaUsuarios = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("usuariosTable"))
        );
        assertTrue(tabelaUsuarios.isDisplayed(), "Tabela de usuários deveria estar visível");

        WebElement primeiraLinhaUsuario = driver.findElement(
                By.cssSelector("#usuariosTable tbody tr:first-child")
        );

        WebElement radioAdmin = primeiraLinhaUsuario.findElement(
                By.cssSelector("input[type='radio'][value='3']")
        );
        radioAdmin.click();

        WebElement botaoSalvar = primeiraLinhaUsuario.findElement(
                By.className("btn-primary")
        );
        botaoSalvar.click();

        wait.until(ExpectedConditions.alertIsPresent());
        String alertText = driver.switchTo().alert().getText();
        assertTrue(alertText.contains("sucesso") || alertText.contains("atualizado"),
                "Deveria aparecer mensagem de confirmação");
        driver.switchTo().alert().accept();

        WebElement tabelaAtualizada = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("usuariosTable"))
        );

        WebElement primeiraLinhaAtualizada = tabelaAtualizada.findElement(
                By.cssSelector("tbody tr:first-child")
        );

        WebElement radioAdminAtualizado = primeiraLinhaAtualizada.findElement(
                By.cssSelector("input[type='radio'][value='3']")
        );

        assertTrue(radioAdminAtualizado.isSelected(),
                "Radio button de Administrador deveria permanecer selecionado");

        System.out.println("CT05: Tipo de usuário alterado com sucesso");
        System.out.println("TESTE DE ADMINISTRAÇÃO CONCLUÍDO COM SUCESSO!");
    }
}