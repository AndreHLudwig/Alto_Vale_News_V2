import React from 'react';
import { useAuth, authUtils } from "../auth";
import LikeButton from './LikeButton';
import { comentariosAPI } from '../services/api';

export default function CommentCard({ comment, onUpdate, onReload }) {
    const { usuario } = useAuth();

    const handleComentarioLike = async (isLiked) => {
        if (!usuario?.userId) return;

        try {
            const response = await (isLiked
                ? comentariosAPI.descurtir(comment.comentarioId, usuario.userId)
                : comentariosAPI.curtir(comment.comentarioId, usuario.userId));

            if (response?.data) {
                onUpdate({
                    ...comment,
                    ...response.data
                });
            }
        } catch (error) {
            console.error('Erro ao processar curtida:', error);
            if (onReload) {
                await onReload();
            }
        }
    };

    const handleDelete = async () => {
        if (!window.confirm('Tem certeza que deseja excluir este comentário?')) {
            return;
        }

        try {
            await comentariosAPI.deletar(comment.comentarioId);
            if (onReload) {
                await onReload();
            }
        } catch (error) {
            console.error('Erro ao deletar comentário:', error);
            alert('Erro ao deletar comentário. Tente novamente.');
        }
    };

    const canDelete = authUtils.isContentOwner(comment.usuario.userId) || authUtils.isAdmin();

    return (
        <li className="list-group-item bg-light p-3 mb-3">
            <div className="d-flex justify-content-between align-items-start mb-2">
                <div>
                    <strong>{comment.usuario.nome} {comment.usuario.sobrenome}</strong>
                    <small className="text-muted d-block">
                        {new Date(comment.data).toLocaleDateString()}
                    </small>
                </div>
                {canDelete && (
                    <button
                        className="btn btn-sm btn-outline-danger"
                        onClick={handleDelete}
                        title="Excluir comentário"
                    >
                        <i className="fas fa-trash"></i>
                    </button>
                )}
            </div>

            <p className="mb-3">{comment.texto}</p>

            <LikeButton
                isLiked={comment.likedByUser}
                likes={comment.curtidas || []}
                onLikeToggle={handleComentarioLike}
                disabled={!usuario}
            />
        </li>
    );
}