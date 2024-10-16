package com.ajmv.altoValeNewsBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

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
    private String email;
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
    // indica ao JPA que este campo não deve ser persistido - somente usado no objeto java para fazer comparações na hora do login com o hash.
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
        this.tipo = TipoUsuario.getTipoUsuario(tipoCode);
    }
}