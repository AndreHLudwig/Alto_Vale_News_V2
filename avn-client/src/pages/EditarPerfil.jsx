import React, { useState, useEffect } from "react";
import { Container, Form, Button } from "react-bootstrap";
import { useAuth } from "../auth";

function EditarPerfil() {
  const { usuario, updateUserData, error: authError } = useAuth();
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
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  useEffect(() => {
    if (usuario) {
      setPerfil((prev) => ({
        ...prev,
        nome: usuario.nome || "",
        sobrenome: usuario.sobrenome || "",
        email: usuario.email || "",
        cpf: usuario.cpf || "",
        endereco: usuario.endereco || "",
        cidade: usuario.cidade || "",
        estado: usuario.estado || "",
        cep: usuario.cep || "",
      }));
    }
  }, [usuario]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPerfil((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (perfil.senha || perfil.confirmarSenha) {
      if (perfil.senha !== perfil.confirmarSenha) {
        setError("As senhas não correspondem.");
        return;
      }
      if (perfil.senha.length < 6) {
        setError("A senha deve ter pelo menos 6 caracteres.");
        return;
      }
    }

    const dadosAtualizados = {
      endereco: perfil.endereco,
      cidade: perfil.cidade,
      estado: perfil.estado,
      cep: perfil.cep,
      ...(perfil.senha && { senha: perfil.senha }),
    };

    try {
      await updateUserData(dadosAtualizados);
      setSuccess("Perfil atualizado com sucesso!");
      setPerfil((prev) => ({
        ...prev,
        senha: "",
        confirmarSenha: "",
      }));
    } catch (err) {
      setError(err.message || "Erro ao atualizar o perfil.");
    }
  };

  return (
    <Container className="py-4">
      <h2>Editar Perfil</h2>

      {error && <div className="alert alert-danger">{error}</div>}
      {authError && <div className="alert alert-danger">{authError}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <Form onSubmit={handleSubmit}>
        <div className="row">
          {/* Campos readonly */}
          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>Nome</Form.Label>
              <Form.Control
                type="text"
                value={perfil.nome}
                readOnly
                className="read-only-perfil form-control"
              />
            </Form.Group>
          </div>

          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>Sobrenome</Form.Label>
              <Form.Control
                type="text"
                value={perfil.sobrenome}
                readOnly
                className="read-only-perfil form-control"
              />
            </Form.Group>
          </div>

          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                value={perfil.email}
                readOnly
                className="read-only-perfil form-control"
              />
            </Form.Group>
          </div>

          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>CPF</Form.Label>
              <Form.Control
                type="text"
                value={perfil.cpf}
                readOnly
                className="read-only-perfil form-control"
              />
            </Form.Group>
          </div>

          {/* Campos editáveis */}
          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>Endereço</Form.Label>
              <Form.Control
                type="text"
                name="endereco"
                value={perfil.endereco}
                onChange={handleChange}
                required
              />
            </Form.Group>
          </div>

          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>Cidade</Form.Label>
              <Form.Control
                type="text"
                name="cidade"
                value={perfil.cidade}
                onChange={handleChange}
                required
              />
            </Form.Group>
          </div>

          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>Estado</Form.Label>
              <Form.Control
                type="text"
                name="estado"
                value={perfil.estado}
                onChange={handleChange}
                required
              />
            </Form.Group>
          </div>

          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>CEP</Form.Label>
              <Form.Control
                type="text"
                name="cep"
                value={perfil.cep}
                onChange={handleChange}
                required
              />
            </Form.Group>
          </div>

          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>Nova Senha</Form.Label>
              <Form.Control
                type="password"
                name="senha"
                value={perfil.senha}
                onChange={handleChange}
                minLength={6}
              />
            </Form.Group>
          </div>

          <div className="col-md-6">
            <Form.Group className="mb-3">
              <Form.Label>Confirmar Nova Senha</Form.Label>
              <Form.Control
                type="password"
                name="confirmarSenha"
                value={perfil.confirmarSenha}
                onChange={handleChange}
                minLength={6}
              />
            </Form.Group>
          </div>
        </div>

        <Button type="submit" variant="primary">
          Atualizar Perfil
        </Button>
      </Form>
    </Container>
  );
}

export default EditarPerfil;
