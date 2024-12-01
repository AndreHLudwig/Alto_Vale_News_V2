import React, { useState, useEffect } from "react";
import { useParams, Navigate } from "react-router-dom";
import { Container, Alert } from "react-bootstrap";
import { publicacoesAPI, mediaAPI, comentariosAPI } from "../services/api";
import LikeButton from "../components/LikeButton";
import CommentsList from "../components/CommentsList";
import CommentForm from "../components/CommentForm";
import { useAuth, authUtils } from "../auth";
import parse from "html-react-parser";

function Post() {
  const { id } = useParams();
  const { usuario } = useAuth();
  const [publicacao, setPublicacao] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);
  const [comentariosReload, setComentariosReload] = useState(0);

  useEffect(() => {
    if (id) {
      carregarPublicacao();
    }
  }, [id, usuario?.userId]);

  const carregarPublicacao = async () => {
    try {
      setCarregando(true);
      const response = await publicacoesAPI.obter(id, usuario?.userId);
      let publicacaoComImagem = response.data;

      // Verificar acesso ao conteúdo VIP
      if (publicacaoComImagem.visibilidadeVip && !authUtils.canAccessVip()) {
        setErro("Conteúdo exclusivo para assinantes VIP");
        return;
      }

      if (publicacaoComImagem.imagem?.id) {
        try {
          const imagemResponse = await mediaAPI.obter(publicacaoComImagem.imagem.id);
          publicacaoComImagem.imagemUrl = URL.createObjectURL(imagemResponse.data);
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
      setErro("Falha ao carregar a publicação. Por favor, tente novamente mais tarde.");
    } finally {
      setCarregando(false);
    }
  };

  const handlePublicacaoLike = async (isLiked) => {
    if (!authUtils.isAuthenticated()) return;

    try {
      const response = await (isLiked
        ? publicacoesAPI.descurtir(publicacao.publicacaoId, usuario.userId)
        : publicacoesAPI.curtir(publicacao.publicacaoId, usuario.userId));

      if (response?.data) {
        setPublicacao((old) => ({
          ...old,
          curtidas: response.data.curtidas,
          likedByUser: response.data.likedByUser,
        }));
      } else {
        await carregarPublicacao();
      }
    } catch (error) {
      if (error.response?.status === 409) {
        await carregarPublicacao();
      } else {
        console.error("Erro ao atualizar curtidas:", error);
        await carregarPublicacao();
      }
    }
  };

  const handleComentarioLike = async (comentarioId, isLiked) => {
    if (!authUtils.isAuthenticated()) return;

    try {
      const response = await (isLiked
        ? comentariosAPI.descurtir(comentarioId, usuario.userId)
        : comentariosAPI.curtir(comentarioId, usuario.userId));

      if (response?.status === 200 && response.data) {
        setPublicacao((old) => ({
          ...old,
          comentarios: old.comentarios.map((c) =>
            c.comentarioId === comentarioId
              ? { ...c, curtidas: response.data.curtidas }
              : c
          ),
        }));
      } else {
        await carregarPublicacao();
      }
    } catch (error) {
      if (error.response?.status === 409) {
        await carregarPublicacao();
      } else {
        console.error("Erro ao atualizar curtidas do comentário:", error);
        await carregarPublicacao();
      }
    }
  };

  useEffect(() => {
    return () => {
      if (publicacao?.imagemUrl) {
        URL.revokeObjectURL(publicacao.imagemUrl);
      }
    };
  }, [publicacao]);

  if (carregando) {
    return (
      <Container className="py-4">
        <div className="text-center">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Carregando...</span>
          </div>
        </div>
      </Container>
    );
  }

  if (erro) {
    if (erro === "Conteúdo exclusivo para assinantes VIP") {
      return (
        <Container className="py-4">
          <Alert variant="warning">
            <Alert.Heading>Conteúdo Exclusivo VIP</Alert.Heading>
            <p>Esta publicação está disponível apenas para assinantes VIP.</p>
            {!authUtils.isAuthenticated() ? (
              <p>
                <a href="/cadastro" className="alert-link">
                  Faça login
                </a>{" "}
                ou{" "}
                <a href="/assinar" className="alert-link">
                  assine agora
                </a>{" "}
                para ter acesso a todo o conteúdo.
              </p>
            ) : (
              <p>
                <a href="/assinar" className="alert-link">
                  Clique aqui para atualizar sua assinatura
                </a>
              </p>
            )}
          </Alert>
        </Container>
      );
    }

    return (
      <Container className="py-4">
        <Alert variant="danger">{erro}</Alert>
      </Container>
    );
  }

  if (!publicacao) {
    return (
      <Container className="py-4">
        <Alert variant="info">Publicação não encontrada</Alert>
      </Container>
    );
  }

  const canEdit = authUtils.hasContentPermission(publicacao);

  return (
    <Container className="py-4">
      <div className="d-flex justify-content-between align-items-start mb-4">
        <h1>{publicacao.titulo}</h1>
      </div>

      <p>
        <strong>Publicado por:</strong> {publicacao.editor.nome}{" "}
        {publicacao.editor.sobrenome}
      </p>
      <p>
        <strong>Data de Publicação:</strong>{" "}
        {new Date(publicacao.data).toLocaleDateString()}
      </p>

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

      <p>{parse(publicacao.texto)}</p>

      <LikeButton
        isLiked={publicacao.likedByUser}
        likes={publicacao.curtidas}
        onLikeToggle={handlePublicacaoLike}
        disabled={!authUtils.isAuthenticated()}
      />

      <hr />

      <CommentForm
        publicacaoId={publicacao.publicacaoId}
        onCommentCreated={() => setComentariosReload((prev) => prev + 1)}
      />

      <div className="row">
        <div className="col-md-10">
          <CommentsList
            publicacaoId={publicacao.publicacaoId}
            shouldReload={comentariosReload}
            onLike={handleComentarioLike}
          />
        </div>
      </div>
    </Container>
  );
}

export default Post;
