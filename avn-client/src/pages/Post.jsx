import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Container } from "react-bootstrap";
import {
  obterPublicacao,
  obterMediaFile,
  curtirPublicacao,
  descurtirPublicacao,
  curtirComentario,
  descurtirComentario,
} from "../services/api";
import {getUserIdFromToken} from "../utils/authUtils.js";
import LikeButton from "../components/LikeButton.jsx";

//TODO criar componente de caixa de comentário e card de comentários
//TODO componente de categorias
function Post() {
  const { id } = useParams(); // Obtem o id da publicação via URL
  const [publicacao, setPublicacao] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);
  const [usuarioId, setUsuarioId] = useState(null);

  useEffect(() => {
    carregarPublicacao();
    getUsuarioIdFromToken();
  }, [id]);

  const getUsuarioIdFromToken = async () => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const id = getUserIdFromToken(token);
        setUsuarioId(id);
      } catch (error) {
        console.error("Erro ao decodificar o token:", error);
        setUsuarioId(null);
      }
    }
  };

  const carregarPublicacao = async () => {
    try {
      setCarregando(true);
      const response = await obterPublicacao(id);

      let publicacaoComImagem = response.data;

      // Verifica se a publicação tem imagem e faz o request para obter a imagem
      if (publicacaoComImagem.imagem && publicacaoComImagem.imagem.id) {
        try {
          const imagemResponse = await obterMediaFile(
            publicacaoComImagem.imagem.id
          );
          publicacaoComImagem.imagemUrl = URL.createObjectURL(
            imagemResponse.data
          );
        } catch (error) {
          console.error(
            `Erro ao carregar imagem para a publicação ${publicacaoComImagem.publicacaoId}:`,
            error
          );
          publicacaoComImagem.imagemUrl = null;
        }
      }

      setPublicacao(publicacaoComImagem);
      setErro(null);
    } catch (error) {
      console.error("Erro ao carregar a publicação:", error);
      setErro(
        "Falha ao carregar a publicação. Por favor, tente novamente mais tarde."
      );
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    return () => {
      if (publicacao && publicacao.imagemUrl) {
        URL.revokeObjectURL(publicacao.imagemUrl);
      }
    };
  }, [publicacao]);

  if (carregando) {
    return (
      <Container>
        <p>Carregando publicação...</p>
      </Container>
    );
  }

  if (erro) {
    return (
      <Container>
        <p className="text-danger">{erro}</p>
      </Container>
    );
  }

  if (!publicacao) {
    return (
      <Container>
        <p>Nenhuma publicação encontrada.</p>
      </Container>
    );
  }

  return (
    <Container className="py-4">
      <h1 className="mb-4">{publicacao.titulo}</h1>
      <p>
        <strong>Publicado por:</strong> {publicacao.editor.nome}{" "}
        {publicacao.editor.sobrenome}
      </p>
      <p>
        <strong>Data de Publicação:</strong>{" "}
        {new Date(publicacao.data).toLocaleDateString()}
      </p>

      {/* Exibe a imagem da publicação, se existir */}
      {publicacao.imagemUrl && (
        <div className="img-container mb-4" style={{ textAlign: "center" }}>
          <img
            src={publicacao.imagemUrl}
            alt={publicacao.titulo}
            className="img-fluid rounded"
            style={{ maxHeight: "60vh", width: "auto" }}
          />
        </div>
      )}

      <p>{publicacao.texto}</p>

      {/* Área de curtidas */}
      <LikeButton
          initialLikes={publicacao.curtidas || []}
          onLike={() => curtirPublicacao(publicacao.publicacaoId, usuarioId)}
          onUnlike={() => descurtirPublicacao(publicacao.publicacaoId, usuarioId)}
          usuarioId={usuarioId}
          likeType="publicacao"
      />

      <hr />

      {/* Renderiza os comentários */}
      <div className="row">
        <div className="col-md-10">
          <h3>Comentários</h3>
          <ul className="list-group">
            {publicacao.comentarios.map((comment) => (
              <li
                key={comment.comentarioId}
                className="mb-4 p-3 border rounded bg-light"
              >
                <p>
                  <strong>Por:</strong> {comment.usuario.nome}{" "}
                  {comment.usuario.sobrenome}
                </p>
                <p>
                  <strong>Data:</strong>{" "}
                  {new Date(comment.data).toLocaleDateString()}
                </p>
                <p>{comment.texto}</p>

                <LikeButton
                    initialLikes={comment.curtidas || []}
                    onLike={() => curtirComentario(comment.comentarioId, usuarioId)}
                    onUnlike={() => descurtirComentario(comment.comentarioId, usuarioId)}
                    usuarioId={usuarioId}
                    likeType="comentario"
                />
              </li>
            ))}
          </ul>
        </div>
      </div>
    </Container>
  );
}

export default Post;
