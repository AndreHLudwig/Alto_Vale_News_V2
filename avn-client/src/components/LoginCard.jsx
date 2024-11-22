import React, { useState } from "react";
import { Card, Form, Button } from "react-bootstrap";
import { Link } from "react-router-dom";
import { useAuth } from "../auth";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTimes } from "@fortawesome/free-solid-svg-icons";

function LoginCard({ onClose, onLoginSuccess }) {
  const { login, error: authError } = useAuth();
  const [formData, setFormData] = useState({
    email: "",
    senha: ""
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await login(formData);
      onLoginSuccess?.();
      onClose();
    } catch (error) {
      console.error("Erro no login:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
      <Card style={{ minWidth: '300px', position: 'relative' }}>
        <Button
            variant="link"
            className="position-absolute top-0 end-0 text-secondary"
            style={{ padding: '0.5rem' }}
            onClick={onClose}
            aria-label="Fechar"
        >
          <FontAwesomeIcon icon={faTimes} />
        </Button>

        <Card.Body>
          <Card.Title className="mb-4">Login</Card.Title>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Control
                  type="email"
                  name="email"
                  placeholder="Email"
                  value={formData.email}
                  onChange={handleChange}
                  required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Control
                  type="password"
                  name="senha"
                  placeholder="Senha"
                  value={formData.senha}
                  onChange={handleChange}
                  required
              />
            </Form.Group>

            {authError && (
                <p className="text-danger small">{authError}</p>
            )}

            <Button
                variant="primary"
                type="submit"
                className="w-100"
                disabled={loading}
            >
              {loading ? "Entrando..." : "Entrar"}
            </Button>
          </Form>

          <div className="mt-3 text-center">
            <Link to="/cadastro" onClick={onClose}>
              Criar nova conta
            </Link>
          </div>
        </Card.Body>
      </Card>
  );
}

export default LoginCard;