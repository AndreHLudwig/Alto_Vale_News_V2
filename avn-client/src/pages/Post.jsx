import React, {useState, useEffect} from "react";
import {useParams} from "react-router-dom";
import {Container} from "react-bootstrap";
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
import CommentsList from "../components/CommentsList.jsx";

//TODO componente de categorias
function Post() {
    const {id} = useParams(); // Obtem o id da publicação via URL
    const [publicacao, setPublicacao] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    const [usuarioId, setUsuarioId] = useState(null);

    useEffect(() => {
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
    }, []);

    useEffect(() => {
        carregarPublicacao();
    }, [id, usuarioId]);

    const carregarPublicacao = async () => {
        if (!id) return;

        try {
            setCarregando(true);
            const response = await obterPublicacao(id, usuarioId);
            let publicacaoComImagem = response.data;

            if (publicacaoComImagem.imagem?.id) {
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

    const atualizarCurtidas = async (acao, id) => {
        try {
            if (acao === 'curtir') {
                await curtirPublicacao(id, usuarioId);
            } else {
                await descurtirPublicacao(id, usuarioId);
            }
            await carregarPublicacao();
        } catch (error) {
            console.error('Erro ao atualizar curtidas:', error);
        }
    };

    const atualizarCurtidasComentario = async (acao, comentarioId) => {
        try {
            if (acao === 'curtir') {
                await curtirComentario(comentarioId, usuarioId);
            } else {
                await descurtirComentario(comentarioId, usuarioId);
            }
            await carregarPublicacao();
        } catch (error) {
            console.error('Erro ao atualizar curtidas do comentário:', error);
        }
    };

    useEffect(() => {
        return () => {
            if (publicacao && publicacao.imagemUrl) {
                URL.revokeObjectURL(publicacao.imagemUrl);
            }
        };
    }, [publicacao]);

    if (erro) {
        return <Container><p className="text-danger">{erro}</p></Container>;
    }

    if (!publicacao) {
        return <Container><p></p></Container>;
    }

    const handlePublicacaoLike = async (isLiked) => {
        if (!usuarioId) return;

        try {
            const response = await (isLiked
                ? descurtirPublicacao(publicacao.publicacaoId, usuarioId)
                : curtirPublicacao(publicacao.publicacaoId, usuarioId));

            if (response?.data) {
                setPublicacao(old => ({
                    ...old,
                    curtidas: response.data.curtidas,
                    likedByUser: response.data.likedByUser
                }));
            } else {
                await carregarPublicacao();
            }
        } catch (error) {
            if (error.response?.status === 409) {
                await carregarPublicacao();
            } else {
                console.error('Erro ao atualizar curtidas:', error);
                await carregarPublicacao();
            }
        }
    };

    const handleComentarioLike = async (comentarioId, isLiked) => {
        if (!usuarioId) return;

        try {
            const response = await (isLiked
                ? descurtirComentario(comentarioId, usuarioId)
                : curtirComentario(comentarioId, usuarioId));

            if (response?.status === 200 && response.data) {
                setPublicacao(old => ({
                    ...old,
                    comentarios: old.comentarios.map(c =>
                        c.comentarioId === comentarioId
                            ? {...c, curtidas: response.data.curtidas}
                            : c
                    )
                }));
            } else {
                await carregarPublicacao();
            }
        } catch (error) {
            if (error.response?.status === 409) {
                await carregarPublicacao();
            } else {
                console.error('Erro ao atualizar curtidas do comentário:', error);
                await carregarPublicacao();
            }
        }
    };

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
                <div className="img-container mb-4" style={{textAlign: "center"}}>
                    <img
                        src={publicacao.imagemUrl}
                        alt={publicacao.titulo}
                        className="img-fluid rounded"
                        style={{maxHeight: "60vh", width: "auto"}}
                    />
                </div>
            )}

            <p>{publicacao.texto}</p>

            {/* Área de curtidas */}
            <LikeButton
                isLiked={publicacao.likedByUser}
                likes={publicacao.curtidas}
                onLikeToggle={handlePublicacaoLike}
                usuarioId={usuarioId}
            />

            <hr/>

            {/* Renderiza os comentários */}
            <div className="row">
                <div className="col-md-10">
                    <CommentsList
                        publicacaoId={publicacao.publicacaoId}
                        usuarioId={usuarioId}
                    />
                </div>
            </div>
        </Container>
    );
}

export default Post;
