import React, { useState } from "react";
import { registro } from "../services/api";

function Cadastro() {
  const [errorMessage, setErrorMessage] = useState("");

  const [formData, setFormData] = useState({
    email: "",
    nome: "",
    sobrenome: "",
    cpf: "",
    endereco: "",
    cidade: "",
    estado: "",
    cep: "",
    senha: "",
    confirmarSenha: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.senha !== formData.confirmarSenha) {
      setErrorMessage("As senhas não correspondem.");
      return;
    }

    try {
      await registro({
        email: formData.email,
        nome: formData.nome,
        sobrenome: formData.sobrenome,
        cpf: formData.cpf,
        endereco: formData.endereco,
        cidade: formData.cidade,
        estado: formData.estado,
        cep: formData.cep,
        senha: formData.senha,
      });

      alert(
        "Cadastro realizado com sucesso! Por favor, faça login para acessar sua conta."
      );
      window.location.href = "/";
    } catch (error) {
      console.error("Erro ao registrar usuário:", error);
      setErrorMessage(error.message);
    }
  };

  return (
    <div className="container my-5">
      <h2>Cadastro</h2>
      <form onSubmit={handleSubmit}>
        {/* Campos do formulário de cadastro */}
        <div className="form-group">
          <label htmlFor="email">E-mail:</label>
          <input
            type="email"
            className="form-control"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="nome">Nome:</label>
          <input
            type="text"
            className="form-control"
            id="nome"
            name="nome"
            value={formData.nome}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="sobrenome">Sobrenome:</label>
          <input
            type="text"
            className="form-control"
            id="sobrenome"
            name="sobrenome"
            value={formData.sobrenome}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="cpf">CPF:</label>
          <input
            type="text"
            className="form-control"
            id="cpf"
            name="cpf"
            value={formData.cpf}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="endereco">Endereço:</label>
          <input
            type="text"
            className="form-control"
            id="endereco"
            name="endereco"
            value={formData.endereco}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="cidade">Cidade:</label>
          <input
            type="text"
            className="form-control"
            id="cidade"
            name="cidade"
            value={formData.cidade}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="estado">Estado:</label>
          <input
            type="text"
            className="form-control"
            id="estado"
            name="estado"
            value={formData.estado}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="cep">CEP:</label>
          <input
            type="text"
            className="form-control"
            id="cep"
            name="cep"
            value={formData.cep}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="senha">Senha:</label>
          <input
            type="password"
            className="form-control"
            id="senha"
            name="senha"
            value={formData.senha}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="confirmarSenha">Confirmar Senha:</label>
          <input
            type="password"
            className="form-control"
            id="confirmarSenha"
            name="confirmarSenha"
            value={formData.confirmarSenha}
            onChange={handleChange}
            required
          />
        </div>
        {errorMessage && (
          <p style={{ color: "red", fontWeight: "bold" }}>{errorMessage}</p>
        )}
        <button type="submit" className="btn btn-primary mt-3">
          Registrar
        </button>
      </form>
    </div>
  );
}

export default Cadastro;
