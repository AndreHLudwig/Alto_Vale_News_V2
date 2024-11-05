import React, { useState } from "react";
import { enviarContato } from "../services/api";
import "../styles/Index.css";

const ContactPage = () => {
  const [formData, setFormData] = useState({
    nome: "",
    email: "",
    mensagem: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.mensagem.trim() === "") {
      alert("Por favor, escreva algo antes de enviar a mensagem.");
      return;
    }

    try {
      const response = await enviarContato(formData);
      if (response.status === 200) {
        console.log("Mensagem enviada!:", response.data);
        alert("Mensagem enviada com sucesso!");
        setFormData({ nome: "", email: "", mensagem: "" }); // Limpa o formulário
      }
    } catch (error) {
      console.error("Erro ao enviar a mensagem:", error);
      console.log(formData);
      alert("Erro ao enviar a mensagem!");
    }
  };

  return (
    <main className="contact-page">
      <section className="contact-info">
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

      <section className="contact-form">
        <h2>Envie uma Mensagem</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="nome">Nome</label>
            <input
              type="text"
              id="nome"
              name="nome"
              value={formData.nome}
              onChange={handleInputChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="mensagem">Mensagem</label>
            <textarea
              id="mensagem"
              name="mensagem"
              rows="5"
              value={formData.mensagem}
              onChange={handleInputChange}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary">
            Enviar
          </button>
        </form>
      </section>

      <section className="map">
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
    </main>
  );
};

export default ContactPage;
