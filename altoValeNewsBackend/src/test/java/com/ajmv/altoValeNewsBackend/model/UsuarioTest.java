package com.ajmv.altoValeNewsBackend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
    }

    @Nested
    @DisplayName("RT01: Testes de Definição do tipo de Usuário (RF05, RF06, RF07, RF08)")
    class DefinicaoTipoUsuarioTests {
        @Test @DisplayName("CT01: Deve lançar exceção para tipo de usuário < 0")
        void setTipoFromInteger_whenCodeIsNegative_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> usuario.setTipoFromInteger(-1));
        }
        @Test @DisplayName("CT02: Deve definir tipo como USUARIO para código 0")
        void setTipoFromInteger_whenCodeIs0_shouldSetTipoToUsuario() {
            usuario.setTipoFromInteger(0);
            assertEquals(TipoUsuario.USUARIO, usuario.getTipo());
        }
        @Test @DisplayName("CT03: Deve definir tipo como USUARIO_VIP para código 1")
        void setTipoFromInteger_whenCodeIs1_shouldSetTipoToUsuarioVip() {
            usuario.setTipoFromInteger(1);
            assertEquals(TipoUsuario.USUARIO_VIP, usuario.getTipo());
        }
        @Test @DisplayName("CT04: Deve definir tipo como EDITOR para código 2")
        void setTipoFromInteger_whenCodeIs2_shouldSetTipoToEditor() {
            usuario.setTipoFromInteger(2);
            assertEquals(TipoUsuario.EDITOR, usuario.getTipo());
        }
        @Test @DisplayName("CT05: Deve definir tipo como ADMINISTRADOR para código 3")
        void setTipoFromInteger_whenCodeIs3_shouldSetTipoToAdministrador() {
            usuario.setTipoFromInteger(3);
            assertEquals(TipoUsuario.ADMINISTRADOR, usuario.getTipo());
        }
        @Test @DisplayName("CT06: Deve lançar exceção para tipo de usuário > 3")
        void setTipoFromInteger_whenCodeIsGreaterThan3_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> usuario.setTipoFromInteger(4));
        }
    }

    @Nested
    @DisplayName("RT02: Testes de Validação de CPF (RF09)")
    class ValidacaoCpfTests {
        @Test @DisplayName("CT07: Deve retornar true para CPF com formato válido")
        void isCPF_whenCpfIsValid_shouldReturnTrue() {
            assertTrue(usuario.isCPF("26291000067"));
        }
        @Test @DisplayName("CT08: Deve retornar false para CPF com caracteres não numéricos")
        void isCPF_whenCpfHasNonNumericChars_shouldReturnFalse() {
            assertFalse(usuario.isCPF("2629100006a"));
        }
        @Test @DisplayName("CT09: Deve retornar false para CPF com tamanho diferente de 11")
        void isCPF_whenCpfLengthIsWrong_shouldReturnFalse() {
            assertFalse(usuario.isCPF("2629100006"));
        }
        @Test @DisplayName("CT10: Deve retornar false para CPF com primeiro dígito verificador inválido")
        void isCPF_whenFirstVerifierDigitIsInvalid_shouldReturnFalse() {
            assertFalse(usuario.isCPF("26291000007"));
        }
        @Test @DisplayName("CT11: Deve retornar false para CPF com segundo dígito verificador inválido")
        void isCPF_whenSecondVerifierDigitIsInvalid_shouldReturnFalse() {
            assertFalse(usuario.isCPF("26291000060"));
        }
        @Test @DisplayName("Deve retornar false para CPF com todos os números iguais")
        void isCPF_whenAllDigitsAreEqual_shouldReturnFalse() {
            assertFalse(usuario.isCPF("11111111111"));
        }
    }

    @Nested
    @DisplayName("RT03: Testes de Criação de Assinatura (RF10)")
    class CriacaoAssinaturaTests {
        @Test @DisplayName("CT12: Deve criar uma assinatura inativa para um novo usuário")
        void criarAssinatura_whenUserHasNoSubscription_shouldCreateInactiveSubscription() {
            assertNull(usuario.getAssinatura());
            usuario.criarAssinatura();
            assertNotNull(usuario.getAssinatura());
            assertFalse(usuario.getAssinatura().isAtivo());
            assertEquals(usuario, usuario.getAssinatura().getUsuario());
        }
        @Test @DisplayName("CT13: Não deve alterar uma assinatura já existente")
        void criarAssinatura_whenUserAlreadyHasSubscription_shouldNotChangeIt() {
            Assinatura assinaturaExistente = new Assinatura();
            assinaturaExistente.setAtivo(true);
            usuario.setAssinatura(assinaturaExistente);
            usuario.criarAssinatura();
            assertSame(assinaturaExistente, usuario.getAssinatura());
            assertTrue(usuario.getAssinatura().isAtivo());
        }
    }

    @Nested
    @DisplayName("RT04: Testes de Validação de CEP (RF11)")
    class ValidacaoCepTests {

        @Test
        @DisplayName("CT14: Deve retornar true para CEP Válido")
        void validaCEP_whenCepIsValid_shouldReturnTrue() {
            assertTrue(usuario.validaCEP("89160000"), "Deveria retornar true para um CEP válido.");
        }

        @Test
        @DisplayName("CT15: Deve retornar false para CEP com caracteres não numéricos")
        void validaCEP_whenCepHasNonNumericChars_shouldReturnFalse() {
            assertFalse(usuario.validaCEP("8916000Y"), "Deveria retornar false para CEP com não numéricos.");
        }

        @Test
        @DisplayName("CT16: Deve retornar false para CEP com tamanho diferente de 8")
        void validaCEP_whenCepLengthIsWrong_shouldReturnFalse() {
            assertFalse(usuario.validaCEP("8916000"), "Deveria retornar false para CEP com tamanho inválido.");
        }

        @Test
        @DisplayName("Deve retornar false para CEP nulo")
        void validaCEP_whenCepIsNull_shouldReturnFalse() {
            assertFalse(usuario.validaCEP(null), "Deveria retornar false para CEP nulo.");
        }
    }

    @Nested
    @DisplayName("RT05: Testes de Validação de E-mail (RF12)")
    class ValidacaoEmailTests {

        @ParameterizedTest
        @ValueSource(strings = { 
            "testes.unitarios@gmail.com",
            "usuario+tag@edu.udesc.br"
        })
        @DisplayName("Deve retornar true para e-mails válidos (CT17, CT18)")
        void isEmail_whenEmailIsValid_shouldReturnTrue(String email) {
            assertTrue(usuario.isEmail(email), "Deveria retornar true para o email: " + email);
        }

        @ParameterizedTest
        @ValueSource(strings = { 
            "emailsemarroba.com",
            "@@gmail.com"
        })
        @DisplayName("Deve retornar false para e-mails inválidos (CT19)")
        void isEmail_whenEmailIsInvalid_shouldReturnFalse(String email) {
            assertFalse(usuario.isEmail(email), "Deveria retornar false para o email: " + email);
        }

        @Test
        @DisplayName("Deve retornar false para e-mail nulo ou vazio (CT20)")
        void isEmail_whenEmailIsNullOrEmpty_shouldReturnFalse() {
            assertFalse(usuario.isEmail(null), "Deveria retornar false para email nulo.");
            assertFalse(usuario.isEmail(""), "Deveria retornar false para email vazio.");
        }
    }
}