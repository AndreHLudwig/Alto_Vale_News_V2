import React, { useState } from "react";
import { Form, Button, Alert } from "react-bootstrap";
import { comentariosAPI } from "../services/api";
import { useAuth, authUtils } from "../auth";

export default function CommentForm({ publicacaoId, onCommentCreated }) {
  const { usuario } = useAuth();
  const [comentario, setComentario] = useState("");
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!authUtils.isAuthenticated()) {
      setErro("Você precisa estar logado para comentar.");
      return;
    }

    if (!comentario.trim()) {
      setErro("O comentário não pode estar vazio.");
      return;
    }

    setCarregando(true);
    setErro(null);

    try {
      const payload = {
        publicacaoId,
        texto: comentario,
        usuario: { userId: usuario.userId },
        data: new Date().toISOString().split("T")[0],
      };

      const response = await comentariosAPI.criar(payload);
      onCommentCreated(response.data);
      setComentario("");
    } catch (error) {
      console.error("Erro ao criar comentário:", error);
      setErro("Ocorreu um erro ao enviar o comentário. Tente novamente.");
    } finally {
      setCarregando(false);
    }
  };

  if (!authUtils.isAuthenticated()) {
    return (
        <Alert variant="info">
          <Alert.Heading>Faça login para comentar</Alert.Heading>
          <p>
            Para participar da discussão, você precisa estar logado.
            <br />
            <a href="/cadastro" className="alert-link">Clique aqui para fazer login ou criar uma conta</a>
          </p>
        </Alert>
    );
  }

  return (
      <div className="comment-form mb-4">
        <h4>Adicionar Comentário</h4>

        {erro && (
            <Alert variant="danger" dismissible onClose={() => setErro(null)}>
              {erro}
            </Alert>
        )}

        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Control
                as="textarea"
                rows={4}
                value={comentario}
                onChange={(e) => setComentario(e.target.value)}
                placeholder="Escreva seu comentário..."
                disabled={carregando}
            />
          </Form.Group>

          <div className="d-flex justify-content-between align-items-center">
            <small className="text-muted">
              Comentando como <strong>{usuario.nome}</strong>
            </small>
            <Button
                type="submit"
                variant="primary"
                disabled={carregando}
            >
              {carregando ? "Enviando..." : "Comentar"}
            </Button>
          </div>
        </Form>
      </div>
  );
}