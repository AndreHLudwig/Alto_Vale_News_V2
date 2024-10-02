import React from 'react';
import { Row, Col } from 'react-bootstrap';
import PublicacaoCard from './PublicacaoCard';

function ListaPublicacoes({ publicacoes }) {
    if (publicacoes.length === 0) {
        return <p>Nenhuma publicação encontrada.</p>;
    }

    return (
        <Row>
            {publicacoes.map((publicacao) => (
                <Col key={publicacao.publicacaoId} md={4} className="mb-4">
                    <PublicacaoCard publicacao={publicacao} />
                </Col>
            ))}
        </Row>
    );
}

export default ListaPublicacoes;