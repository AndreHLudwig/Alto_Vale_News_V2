import React, { useState, useEffect } from "react";
import { comentariosAPI } from "../services/api";
import { useAuth, authUtils } from "../auth";
import CommentCard from "./CommentCard";
import Spinner from "react-bootstrap/Spinner";

export default function CommentsList({ publicacaoId, shouldReload }) {
  const { usuario } = useAuth();
  const [comentarios, setComentarios] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregarComentarios = async () => {
    try {
      setCarregando(true);
      setErro(null);
      const response = await comentariosAPI.listar(publicacaoId, usuario?.userId);

      // Ordena comentários do mais recente para o mais antigo
      const comentariosOrdenados = response.data.sort((a, b) =>
          new Date(b.data) - new Date(a.data)
      );

      setComentarios(comentariosOrdenados);
    } catch (error) {
      console.error("Erro ao carregar comentários:", error);
      setErro("Não foi possível carregar os comentários. Tente novamente mais tarde.");
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregarComentarios();
  }, [publicacaoId, usuario?.userId, shouldReload]);

  const atualizarComentario = (comentarioAtualizado) => {
    setComentarios(prevComentarios =>
        prevComentarios.map(c =>
            c.comentarioId === comentarioAtualizado.comentarioId
                ? comentarioAtualizado
                : c
        )
    );
  };

  if (carregando) {
    return (
        <div className="text-center py-4">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Carregando...</span>
          </Spinner>
        </div>
    );
  }

  if (erro) {
    return (
        <div className="alert alert-danger" role="alert">
          {erro}
        </div>
    );
  }

  const canModerateComments = authUtils.isAdmin();

  return (
      <div className="comments-section">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h3>Comentários ({comentarios.length})</h3>
          {canModerateComments && comentarios.length > 0 && (
              <button
                  className="btn btn-outline-danger btn-sm"
                  onClick={() => {/* Implementar moderação em massa */}}
              >
                Moderar Comentários
              </button>
          )}
        </div>

        {comentarios.length === 0 ? (
            <p className="text-muted">
              {authUtils.isAuthenticated()
                  ? "Nenhum comentário ainda. Seja o primeiro a comentar!"
                  : "Nenhum comentário ainda. Faça login para comentar!"
              }
            </p>
        ) : (
            <ul className="list-group">
              {comentarios.map((comment) => (
                  <CommentCard
                      key={comment.comentarioId}
                      comment={comment}
                      onUpdate={atualizarComentario}
                      onReload={carregarComentarios}
                  />
              ))}
            </ul>
        )}
      </div>
  );
}