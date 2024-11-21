import React, { useState } from "react";
import { criarComentario } from "../services/api";

export default function CommentForm({ publicacaoId, usuarioId, onCommentCreated }) {
  const [comentario, setComentario] = useState("");
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(false);

  const handleInputChange = (e) => {
    setComentario(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!comentario.trim()) {
      setErro("O comentário não pode estar vazio.");
      return;
    }

    setCarregando(true);
    setErro(null);

    try {
      const dataAtual = new Date().toISOString().split("T")[0];

      const payload = {
        publicacaoId: publicacaoId,
        texto: comentario,
        usuario: { userId: usuarioId },
        data: dataAtual,
      };

      const response = await criarComentario(payload);

      if (response.status === 201) {
        onCommentCreated(response.data);
        setComentario("");
      } else {
        setErro("Erro ao criar o comentário. Tente novamente.");
      }
    } catch (error) {
      console.error("Erro ao criar comentário:", error);
      setErro("Ocorreu um erro. Tente novamente.");
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="comment-form">
      <h4>Adicionar Comentário</h4>
      {erro && <p className="text-danger">{erro}</p>}
      <form onSubmit={handleSubmit}>
        <textarea
          value={comentario}
          onChange={handleInputChange}
          rows="4"
          placeholder="Escreva seu comentário..."
          className="form-control"
        />
        <div className="d-flex justify-content-between mt-2">
          <button type="submit" className="btn btn-primary" disabled={carregando}>
            {carregando ? "Carregando..." : "Comentar"}
          </button>
        </div>
      </form>
    </div>
  );
}
