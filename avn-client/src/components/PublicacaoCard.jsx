import React from 'react';
import { Card, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';

function PublicacaoCard({ publicacao }) {
    return (
        <Card>
            {publicacao.imagemUrl && (
                <Card.Img
                    variant="top"
                    src={publicacao.imagemUrl}
                    alt={publicacao.titulo}
                />
            )}
            <Card.Body>
                <Card.Title>{publicacao.titulo}</Card.Title>
                <Card.Text>{publicacao.texto.substring(0, 100)}...</Card.Text>
                <Link to={`/post/${publicacao.publicacaoId}`}>
                    <Button variant="primary">Ler Mais</Button>
                </Link>
            </Card.Body>
            <Card.Footer>
                <small className="text-muted">
                    Publicado em: {new Date(publicacao.data).toLocaleDateString()}
                </small>
            </Card.Footer>
        </Card>
    );
}

export default PublicacaoCard;