import React from 'react';
import { Row, Col } from 'react-bootstrap';
import PublicacaoCard from './PublicacaoCard';

function ListaPublicacoes({ publicacoes, loading, error }) {
    if (loading) {
        return (
            <div className="text-center py-4">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Carregando...</span>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="alert alert-danger" role="alert">
                {error}
            </div>
        );
    }

    if (!publicacoes?.length) {
        return (
            <div className="text-center py-4">
                <p className="text-muted">Nenhuma publicação encontrada.</p>
            </div>
        );
    }

    return (
        <Row className="g-4">
            {publicacoes.map((publicacao) => (
                <Col key={publicacao.publicacaoId} xs={12} md={6} lg={4}>
                    <PublicacaoCard publicacao={publicacao} />
                </Col>
            ))}
        </Row>
    );
}

export default ListaPublicacoes;