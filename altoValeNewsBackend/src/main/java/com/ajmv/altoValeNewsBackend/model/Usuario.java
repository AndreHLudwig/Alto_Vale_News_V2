package com.ajmv.altoValeNewsBackend.model;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "usuario")
@Entity(name = "usuario")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "userId")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Integer userId;

    private String nome;
    private String sobrenome;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String cpf;

    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private String senhahash;

    @Enumerated(EnumType.ORDINAL)
    private TipoUsuario tipo;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Assinatura assinatura;

    @Transient
    private String senha;

    public Assinatura getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(Assinatura assinatura) {
        this.assinatura = assinatura;
    }

    public void criarAssinatura() {
        if (this.assinatura == null) {
            this.assinatura = new Assinatura();
            this.assinatura.setUsuario(this);
            this.assinatura.setVencimento(LocalDateTime.now().plusMonths(1));
            this.assinatura.setAtivo(false);
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getSenhahash() {
        return senhahash;
    }

    public void setSenhahash(String senhahash) {
        this.senhahash = senhahash;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    @JsonProperty("tipo")
    public void setTipoFromInteger(Integer tipoCode) {
        if (tipoCode != null) {
            this.tipo = TipoUsuario.getTipoUsuario(tipoCode);
        }
    }

    protected boolean isCPF(String CPF) {
        if (CPF == null || CPF.length() != 11 || CPF.matches(CPF.charAt(0) + "{11}")) return false;
        try {
            char dig10, dig11;
            int sm, i, r, num, peso;
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) dig10 = '0';
            else dig10 = (char)(r + 48);
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) dig11 = '0';
            else dig11 = (char)(r + 48);
            return (dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }
    
    protected boolean isEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }
    
    protected boolean validaCEP(String cep) {
        if (cep == null || cep.length() != 8) {
            return false;
        }
        for (char c : cep.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}