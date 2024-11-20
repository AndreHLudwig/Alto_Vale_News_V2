import React, { useState } from 'react';
import { Heart, HeartOff } from 'lucide-react';

export default function LikeButton({ isLiked, likes = [], onLikeToggle, usuarioId }) {
    const [isProcessing, setIsProcessing] = useState(false);

    const handleLikeClick = async () => {
        if (!usuarioId || isProcessing) return;

        try {
            setIsProcessing(true);
            await onLikeToggle(isLiked);
        } catch (error) {
            // console.error(`Erro ao ${isLiked ? 'descurtir' : 'curtir'}:`, error);
        } finally {
            setIsProcessing(false);
        }
    };

    // console.log('LikeButton render:', { isLiked, likesCount: likes.length });

    return (
        <div className="d-flex align-items-center gap-2">
            <button
                onClick={handleLikeClick}
                className={`btn btn-sm ${isLiked ? 'btn-danger' : 'btn-outline-danger'}`}
                title={isLiked ? 'Descurtir' : 'Curtir'}
                disabled={!usuarioId || isProcessing}
            >
                {isLiked ? <HeartOff size={16} /> : <Heart size={16} />}
            </button>
            <span>{likes.length} curtidas</span>
        </div>
    );
}