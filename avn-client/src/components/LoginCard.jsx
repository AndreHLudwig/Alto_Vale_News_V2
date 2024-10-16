import React, { useState } from 'react';
import { Card, Form, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import api from '../services/api';

function LoginCard({ onClose, onLogin }) {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/usuario/login', { email, senha });
      localStorage.setItem('token', response.data.jwt);
      onLogin(response.data.usuario);
      onClose();
    } catch (error) {
      setErro('Usuário ou senha inválidos');
    }
  };

  return (
    <Card>
      <Card.Body>
        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Control
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Control
              type="password"
              placeholder="Senha"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
            />
          </Form.Group>
          {erro && <p className="text-danger">{erro}</p>}
          <Button variant="primary" type="submit" className="w-100">
            Entrar
          </Button>
        </Form>
        <div className="mt-3 text-center">
          <Link to="/cadastro">Criar nova conta</Link>
        </div>
      </Card.Body>
    </Card>
  );
}

export default LoginCard;