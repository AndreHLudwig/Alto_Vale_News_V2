import React from 'react';
import LikeButton from './LikeButton';
import { curtirComentario, descurtirComentario } from '../services/api';

export default function CommentCard({ comment, usuarioId, onUpdate, onReload }) {
    const handleComentarioLike = async (isLiked) => {
        if (!usuarioId) return;

        try {
            console.log('Estado atual antes da operação:', {
                comentarioId: comment.comentarioId,
                isLiked: comment.likedByUser,
                curtidas: comment.curtidas
            });

            const response = await (isLiked
                ? descurtirComentario(comment.comentarioId, usuarioId)
                : curtirComentario(comment.comentarioId, usuarioId));

            console.log('Resposta da API:', response.data);

            if (response?.data) {
                onUpdate({
                    ...comment,
                    ...response.data
                });
            }
        } catch (error) {
            console.error('Erro na operação:', error);
            if (onReload) {
                await onReload();
            }
        }
    };

    return (
        <li className="mb-4 p-3 border rounded bg-light">
            <p>
                <strong>Por:</strong> {comment.usuario.nome} {comment.usuario.sobrenome}
            </p>
            <p>
                <strong>Data:</strong>{" "}
                {new Date(comment.data).toLocaleDateString()}
            </p>
            <p>{comment.texto}</p>

            <LikeButton
                isLiked={comment.likedByUser}
                likes={comment.curtidas || []}
                onLikeToggle={handleComentarioLike}
                usuarioId={usuarioId}
            />
        </li>
    );
}