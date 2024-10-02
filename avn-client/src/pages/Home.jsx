import React, { useState, useEffect } from "react";
import { Container, Row, Col, Card, Button } from "react-bootstrap";
import { Await, Link } from "react-router-dom";
import { listarPublicacoes } from "../services/api";

function Home() {
  const [publicacoes, setPublicacoes] = useState([]);
  useEffect(() => {
    carregarPublicacoes();
  }, []);

  const carregarPublicacoes = async () => {
    try {
      const response = await listarPublicacoes();
    } catch (error) {
      console.error("Erro ao carregar publicações!", error);
    }
  };

  return (
    <Container className="my-4">
      <h1 className="text-center mb-4">Alto Vale News</h1>
      <p className="lead text-center mb-5">
        {" "}
        Seu portal de Notícias do Alto Vale do Itajaí!
      </p>
      <Row>
        {publicacoes.map((publicacao) => (
          <Col key={publicacao.publicacaoId} md={4} className="mb-4">
            <Card>
              {publicacao.imagem && (
                <Card.Img
                  variant="top"
                  src={publicacao.imagem}
                  alt={publicacao.titulo}
                />
              )}
              <Card.Body>
                <Card.Title>{publicacao.titulo}</Card.Title>
                <Card.Text>{publicacao.texto.subString(0, 100)}...</Card.Text>
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
          </Col>
        ))}
      </Row>
    </Container>
  );
}

export default Home;
