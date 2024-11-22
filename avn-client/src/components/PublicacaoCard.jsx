import React from 'react';
import { Card, Button, Badge } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useAuth, authUtils } from "../auth";
import { useLogin } from "../auth/LoginContext";

function PublicacaoCard({ publicacao }) {
    const { usuario } = useAuth();
    const { openLoginCard } = useLogin();
    const isVipContent = publicacao.visibilidadeVip;
    const canViewContent = authUtils.canAccessContent(publicacao);

    const truncateText = (text, maxLength = 150) => {
        if (text.length <= maxLength) return text;
        return text.substr(0, text.lastIndexOf(' ', maxLength)) + '...';
    };

    return (
        <Card className="h-100 shadow-sm">
            <div className="position-relative">
                {publicacao.imagemUrl && (
                    <div className="card-img-container" style={{ height: '200px', overflow: 'hidden' }}>
                        <Card.Img
                            variant="top"
                            src={publicacao.imagemUrl}
                            alt={publicacao.titulo}
                            style={{
                                objectFit: 'cover',
                                height: '100%',
                                width: '100%'
                            }}
                        />
                    </div>
                )}
                {isVipContent && (
                    <Badge
                        bg="warning"
                        text="dark"
                        className="position-absolute top-0 end-0 m-2"
                    >
                        VIP
                    </Badge>
                )}
            </div>

            <Card.Body className="d-flex flex-column">
                <Card.Title className="font-weight-bold">
                    {publicacao.titulo}
                </Card.Title>

                <Card.Text>
                {!canViewContent ? (
                    <div className="text-muted">
                        Conteúdo exclusivo para assinantes VIP.
                        {!usuario ? (
                            <Button
                                variant="link"
                                className="p-0 mt-2 d-block"
                                onClick={openLoginCard}
                            >
                                Faça login para acessar
                            </Button>
                        ) : (
                            <Link to="/assinar" className="d-block mt-2">
                                Clique aqui para assinar
                            </Link>
                        )}
                    </div>
                ) : (
                    truncateText(publicacao.texto)
                )}
            </Card.Text>

                <div className="mt-auto">
                    {canViewContent && (
                        <Link
                            to={`/post/${publicacao.publicacaoId}`}
                            className="btn btn-primary w-100"
                        >
                            Ler Mais
                        </Link>
                    )}
                </div>
            </Card.Body>

            <Card.Footer>
                <div className="d-flex justify-content-between align-items-center">
                    <small className="text-muted">
                        {new Date(publicacao.data).toLocaleDateString()}
                    </small>
                    {publicacao.editor && (
                        <small className="text-muted">
                            Por: {publicacao.editor.nome} {publicacao.editor.sobrenome}
                        </small>
                    )}
                </div>
            </Card.Footer>
        </Card>
    );
}

export default PublicacaoCard;