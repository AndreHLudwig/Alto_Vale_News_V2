import React, { useState } from "react";
import { Container, Form, Button } from "react-bootstrap";
import { useAuth } from "../auth";
import { useNavigate } from "react-router-dom";

function Cadastro() {
  const navigate = useNavigate();
  const { registro, error: authError } = useAuth();
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

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
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    if (formData.senha !== formData.confirmarSenha) {
      setError("As senhas não correspondem.");
      setLoading(false);
      return;
    }

    if (formData.senha.length < 6) {
      setError("A senha deve ter pelo menos 6 caracteres.");
      setLoading(false);
      return;
    }

    try {
      const dadosCadastro = {
        email: formData.email,
        nome: formData.nome,
        sobrenome: formData.sobrenome,
        cpf: formData.cpf,
        endereco: formData.endereco,
        cidade: formData.cidade,
        estado: formData.estado,
        cep: formData.cep,
        senha: formData.senha,
      };

      await registro(dadosCadastro);
      navigate("/");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
      <Container className="py-4">
        <h2 className="mb-4">Cadastro</h2>

        {error && <div className="alert alert-danger">{error}</div>}
        {authError && <div className="alert alert-danger">{authError}</div>}

        <Form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>Email</Form.Label>
                <Form.Control
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                />
              </Form.Group>
            </div>

            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>CPF</Form.Label>
                <Form.Control
                    type="text"
                    name="cpf"
                    value={formData.cpf}
                    onChange={handleChange}
                    required
                />
              </Form.Group>
            </div>

            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>Nome</Form.Label>
                <Form.Control
                    type="text"
                    name="nome"
                    value={formData.nome}
                    onChange={handleChange}
                    required
                />
              </Form.Group>
            </div>

            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>Sobrenome</Form.Label>
                <Form.Control
                    type="text"
                    name="sobrenome"
                    value={formData.sobrenome}
                    onChange={handleChange}
                    required
                />
              </Form.Group>
            </div>

            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>Endereço</Form.Label>
                <Form.Control
                    type="text"
                    name="endereco"
                    value={formData.endereco}
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
                    value={formData.cidade}
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
                    value={formData.estado}
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
                    value={formData.cep}
                    onChange={handleChange}
                    required
                />
              </Form.Group>
            </div>

            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>Senha</Form.Label>
                <Form.Control
                    type="password"
                    name="senha"
                    value={formData.senha}
                    onChange={handleChange}
                    minLength={6}
                    required
                />
              </Form.Group>
            </div>

            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>Confirmar Senha</Form.Label>
                <Form.Control
                    type="password"
                    name="confirmarSenha"
                    value={formData.confirmarSenha}
                    onChange={handleChange}
                    minLength={6}
                    required
                />
              </Form.Group>
            </div>
          </div>

          <Button
              variant="primary"
              type="submit"
              disabled={loading}
              className="w-100"
          >
            {loading ? "Cadastrando..." : "Cadastrar"}
          </Button>
        </Form>
      </Container>
  );
}

export default Cadastro;