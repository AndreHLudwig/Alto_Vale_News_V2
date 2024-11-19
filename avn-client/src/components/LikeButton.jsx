import React, { useState } from 'react';
import { Heart, HeartOff } from 'lucide-react'; // Utilizando os ícones do Lucide

export default function LikeButton({
                                       initialLikes = [],
                                       onLike,
                                       onUnlike,
                                       usuarioId,
                                       likeType // 'publicacao' ou 'comentario'
                                   }) {
    const [likes, setLikes] = useState(initialLikes);
    const isLiked = likes.some(like => like.usuario?.userId === usuarioId);

    const handleLikeClick = async () => {
        try {
            if (!usuarioId) {
                alert("Você precisa estar logado para curtir.");
                return;
            }

            if (isLiked) {
                await onUnlike();
                setLikes(likes.filter(like => like.usuario?.userId !== usuarioId));
            } else {
                await onLike();
                setLikes([...likes, { usuario: { userId: usuarioId } }]);
            }
        } catch (error) {
            console.error(`Erro ao ${isLiked ? 'descurtir' : 'curtir'} ${likeType}:`, error);
        }
    };

    return (
        <div className="d-flex align-items-center gap-2">
            <button
                onClick={handleLikeClick}
                className={`btn btn-sm ${isLiked ? 'btn-danger' : 'btn-outline-danger'}`}
                title={isLiked ? 'Descurtir' : 'Curtir'}
            >
                {isLiked ? <HeartOff size={16} /> : <Heart size={16} />}
            </button>
            <span>{likes.length} curtidas</span>
        </div>
    );
}