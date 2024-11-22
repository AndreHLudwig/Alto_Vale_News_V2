import React, { useState } from "react";
import { Container, Form, Button } from "react-bootstrap";
import { contatoAPI } from "../services/api";

const Contato = () => {
  const [formData, setFormData] = useState({
    nome: "",
    email: "",
    mensagem: "",
  });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (formData.mensagem.trim() === "") {
      setError("Por favor, escreva uma mensagem antes de enviar.");
      return;
    }

    try {
      await contatoAPI.enviar(formData);
      setSuccess("Mensagem enviada com sucesso!");
      setFormData({ nome: "", email: "", mensagem: "" });
    } catch (error) {
      console.error("Erro ao enviar mensagem:", error);
      setError("Erro ao enviar a mensagem. Por favor, tente novamente.");
    }
  };

  return (
      <main className="contact-page">
        <Container>
          <section className="contact-info mb-5">
            <h1>Entre em contato</h1>
            <p>Entre em contato conosco através do formulário abaixo:</p>
            <div className="info">
              <div className="info-item">
                <i className="fas fa-phone"></i>
                <span>(47) 91234-5678</span>
              </div>
              <div className="info-item">
                <i className="fas fa-envelope"></i>
                <span>contato@altovalenews.com.br</span>
              </div>
              <div className="info-item">
                <i className="fas fa-map-marker-alt"></i>
                <span>Rua Ponto Chic, 1001, Ibirama - SC</span>
              </div>
            </div>
          </section>

          <section className="contact-form mb-5">
            <h2>Envie uma Mensagem</h2>

            {error && <div className="alert alert-danger">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            <Form onSubmit={handleSubmit}>
              <Form.Group className="mb-3">
                <Form.Label>Nome</Form.Label>
                <Form.Control
                    type="text"
                    name="nome"
                    value={formData.nome}
                    onChange={handleInputChange}
                    required
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Email</Form.Label>
                <Form.Control
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Mensagem</Form.Label>
                <Form.Control
                    as="textarea"
                    rows={5}
                    name="mensagem"
                    value={formData.mensagem}
                    onChange={handleInputChange}
                    required
                />
              </Form.Group>

              <Button variant="primary" type="submit">
                Enviar
              </Button>
            </Form>
          </section>

          <section className="map mb-5">
            <h2>Localização</h2>
            <div className="embed-responsive embed-responsive-16by9">
              <iframe
                  className="embed-responsive-item"
                  src="https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d113701.98471047379!2d-49.5296759!3d-27.055844!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x94dfb20d8f598fab%3A0x8871264e819c7ac2!2sUDESC%20-%20Alto%20Vale%20-%20CEAVI!5e0!3m2!1spt-BR!2sus!4v1716161699391!5m2!1spt-BR!2sus"
                  allowFullScreen
                  loading="lazy"
                  referrerPolicy="no-referrer-when-downgrade"
                  title="Localização no mapa"
              />
            </div>
          </section>
        </Container>
      </main>
  );
};

export default Contato;