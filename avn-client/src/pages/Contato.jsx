import React, { useState } from "react";
import "../styles/Index.css";

const ContactPage = () => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    message: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (formData.message.trim() === "") {
      alert("Por favor, escreva algo antes de enviar a mensagem.");
      return;
    }

    const data = {
      nome: formData.name,
      email: formData.email,
      data: new Date().toISOString().split("T")[0],
      message: formData.message,
    };

    fetch("http://localhost:8080/contato", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    })
      .then((response) => {
        if (response.ok) {
          return response.json();
        }
        throw new Error("Erro ao enviar a mensagem!");
      })
      .then((data) => {
        console.log("Mensagem enviada!:", data);
        alert("Mensagem enviada com sucesso!");
        setFormData({ name: "", email: "", message: "" }); // Limpa o formulário
      })
      .catch((error) => {
        console.error("Erro:", error);
      });
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
            <label htmlFor="name">Nome</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
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
            <label htmlFor="message">Mensagem</label>
            <textarea
              id="message"
              name="message"
              rows="5"
              value={formData.message}
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
