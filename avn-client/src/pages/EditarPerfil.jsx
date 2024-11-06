import React, { useState, useEffect } from "react";
import { obterPerfilUsuario, atualizarPerfilUsuario } from "../services/api";
import { getUserIdFromToken } from "../utils/authUtils";

function EditarPerfil() {
  const [perfil, setPerfil] = useState({
    nome: "",
    sobrenome: "",
    email: "",
    cpf: "",
    endereco: "",
    cidade: "",
    estado: "",
    cep: "",
    senha: "",
    confirmarSenha: "",
  });

  const [userId, setUserId] = useState(null);

  useEffect(() => {
    const fetchPerfil = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          alert("Usuário não autenticado.");
          return;
        }

        const userIdFromToken = getUserIdFromToken(token);
        if (!userIdFromToken) {
          alert("ID do usuário não encontrado no token.");
          return;
        }
        setUserId(userIdFromToken);

        const response = await obterPerfilUsuario(userIdFromToken);
        setPerfil({
          ...perfil,
          nome: response.data.nome,
          sobrenome: response.data.sobrenome,
          email: response.data.email,
          cpf: response.data.cpf,
          endereco: response.data.endereco,
          cidade: response.data.cidade,
          estado: response.data.estado,
          cep: response.data.cep,
        });
      } catch (error) {
        console.error("Erro ao carregar perfil:", error);
        alert("Erro ao carregar o perfil do usuário.");
      }
    };

    fetchPerfil();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPerfil((prevPerfil) => ({
      ...prevPerfil,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (perfil.senha !== perfil.confirmarSenha) {
      alert("As senhas não correspondem.");
      return;
    }

    const dadosAtualizados = {
      endereco: perfil.endereco,
      cidade: perfil.cidade,
      estado: perfil.estado,
      cep: perfil.cep,
      // Envia a senha apenas se estiver preenchida
      ...(perfil.senha &&
        perfil.senha === perfil.confirmarSenha && {
          senha: perfil.senha,
        }),
    };

    try {
      if (!userId) {
        alert("Usuário não autenticado.");
        return;
      }

      const response = await atualizarPerfilUsuario(userId, dadosAtualizados);
      alert("Perfil atualizado com sucesso!");

      if (perfil.senha) {
        setPerfil({ ...perfil, senha: "", confirmarSenha: "" });
      }
    } catch (error) {
      console.error("Erro ao atualizar perfil:", error);
      alert("Erro ao atualizar o perfil.");
    }
  };

  return (
    <div className="container my-3">
      <h1>Editar Perfil</h1>
      <form onSubmit={handleSubmit}>
        {/* Campos readonly */}
        <div className="form-group">
          <label htmlFor="nome">Nome</label>
          <input
            type="text"
            className="form-control readonly-field"
            id="nome"
            name="nome"
            value={perfil.nome}
            readOnly
          />
        </div>
        <div className="form-group">
          <label htmlFor="sobrenome">Sobrenome</label>
          <input
            type="text"
            className="form-control readonly-field"
            id="sobrenome"
            name="sobrenome"
            value={perfil.sobrenome}
            readOnly
          />
        </div>
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            className="form-control readonly-field"
            id="email"
            name="email"
            value={perfil.email}
            readOnly
          />
        </div>
        <div className="form-group">
          <label htmlFor="cpf">CPF</label>
          <input
            type="text"
            className="form-control readonly-field"
            id="cpf"
            name="cpf"
            value={perfil.cpf}
            readOnly
          />
        </div>

        {/* Campos editáveis */}
        <div className="form-group">
          <label htmlFor="endereco">Endereço</label>
          <input
            type="text"
            className="form-control editable-field"
            id="endereco"
            name="endereco"
            value={perfil.endereco}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="cidade">Cidade</label>
          <input
            type="text"
            className="form-control editable-field"
            id="cidade"
            name="cidade"
            value={perfil.cidade}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="estado">Estado</label>
          <input
            type="text"
            className="form-control editable-field"
            id="estado"
            name="estado"
            value={perfil.estado}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="cep">CEP</label>
          <input
            type="text"
            className="form-control editable-field"
            id="cep"
            name="cep"
            value={perfil.cep}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="senha">Nova Senha</label>
          <input
            type="password"
            className="form-control editable-field"
            id="senha"
            name="senha"
            value={perfil.senha}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="confirmarSenha">Confirmar Nova Senha</label>
          <input
            type="password"
            className="form-control editable-field"
            id="confirmarSenha"
            name="confirmarSenha"
            value={perfil.confirmarSenha}
            onChange={handleChange}
          />
        </div>

        <button type="submit" className="btn btn-primary mt-3">
          Atualizar Perfil
        </button>
      </form>
    </div>
  );
}

export default EditarPerfil;
