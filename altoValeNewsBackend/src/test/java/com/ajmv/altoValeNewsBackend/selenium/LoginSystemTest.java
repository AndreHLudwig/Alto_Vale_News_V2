package com.ajmv.altoValeNewsBackend.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class LoginSystemTest extends BaseSeleniumTest {

    @Test
    @DisplayName("CT02: Verificar se o usuário consegue realizar login com sucesso")
    void testLoginComSucesso() {
        navigateToHome();

        WebElement loginButton = driver.findElement(By.linkText("Cadastro/Login"));
        loginButton.click();

        WebElement emailField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("email"))
        );
        WebElement senhaField = driver.findElement(By.name("senha"));

        emailField.sendKeys("joao.silva@mail.com");
        senhaField.sendKeys("senha@123");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        WebElement perfilLink = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Perfil"))
        );
        assertTrue(perfilLink.isDisplayed(), "Link do perfil deveria estar visível após login");

        WebElement logoutButton = driver.findElement(By.linkText("Sair"));
        assertTrue(logoutButton.isDisplayed(), "Botão de logout deveria estar visível");
    }
}