import React, { useState } from 'react';
import { Heart, HeartOff } from 'lucide-react';
import { useAuth, authUtils } from '../auth';

export default function LikeButton({ isLiked, likes = [], onLikeToggle, disabled }) {
    const { usuario } = useAuth();
    const [isProcessing, setIsProcessing] = useState(false);
    const isUserAuthenticated = authUtils.isAuthenticated();

    const handleLikeClick = async () => {
        if (!isUserAuthenticated || isProcessing || disabled) return;

        try {
            setIsProcessing(true);
            await onLikeToggle(isLiked);
        } catch (error) {
            console.error(`Erro ao ${isLiked ? 'descurtir' : 'curtir'}:`, error);
        } finally {
            setIsProcessing(false);
        }
    };

    const buttonTitle = !isUserAuthenticated
        ? 'Faça login para curtir'
        : isLiked
            ? 'Descurtir'
            : 'Curtir';

    const likesCount = Array.isArray(likes) ? likes.length : 0;

    return (
        <div className="d-flex align-items-center gap-2">
            <button
                onClick={handleLikeClick}
                className={`btn btn-sm ${isLiked ? 'btn-danger' : 'btn-outline-danger'} ${isProcessing ? 'opacity-50' : ''}`}
                title={buttonTitle}
                disabled={!isUserAuthenticated || isProcessing || disabled}
            >
                {isLiked ? <HeartOff size={16} /> : <Heart size={16} />}
            </button>
            <span className="text-muted small">
                {likesCount} {likesCount === 1 ? 'curtida' : 'curtidas'}
            </span>
            {!isUserAuthenticated && (
                <small className="text-muted">
                    (Faça <a href="/cadastro">login</a> para curtir)
                </small>
            )}
        </div>
    );
}