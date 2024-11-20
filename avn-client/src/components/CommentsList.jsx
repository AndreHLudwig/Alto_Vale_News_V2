import React, { useState, useEffect } from 'react';
import { listarComentarios } from '../services/api';
import CommentCard from './CommentCard';

export default function CommentsList({ publicacaoId, usuarioId }) {
    const [comentarios, setComentarios] = useState([]);
    const [carregando, setCarregando] = useState(true);

    const carregarComentarios = async () => {
        try {
            setCarregando(true);
            const response = await listarComentarios(publicacaoId, usuarioId);
            setComentarios(response.data);
        } catch (error) {
            console.error('Erro ao carregar comentários:', error);
        } finally {
            setCarregando(false);
        }
    };

    useEffect(() => {
        carregarComentarios();
    }, [publicacaoId, usuarioId]);

    const atualizarComentario = (comentarioAtualizado) => {
        console.log('Atualizando comentário na lista:', comentarioAtualizado);
        setComentarios(prevComentarios => {
            const novosComentarios = prevComentarios.map(c =>
                c.comentarioId === comentarioAtualizado.comentarioId
                    ? comentarioAtualizado
                    : c
            );
            console.log('Nova lista de comentários:', novosComentarios);
            return novosComentarios;
        });
    };

    if (carregando) return <p>Carregando comentários...</p>;

    return (
        <div className="comments-section">
            <h3>Comentários</h3>
            <ul className="list-group">
                {comentarios.map(comment => (
                    <CommentCard
                        key={comment.comentarioId}
                        comment={comment}
                        usuarioId={usuarioId}
                        onUpdate={atualizarComentario}
                        onReload={carregarComentarios}
                    />
                ))}
            </ul>
        </div>
    );
}