import React, { useState } from "react";
import { Container, Form, Button } from "react-bootstrap";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import { publicacoesAPI } from "../services/api";
import { useAuth } from "../auth";

const PainelEditor = () => {
  const { usuario } = useAuth();
  const [form, setForm] = useState({
    titulo: "",
    texto: "",
    categorias: [""],
    visibilidadeVip: false,
    imagem: null,
    video: null,
  });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleInputChange = (event) => {
    const { name, value, type, checked, files, dataset } = event.target;
    const index = dataset?.index;

    setForm((prevForm) => {
      if (name === "categorias") {
        const newCategories = [...prevForm.categorias];
        newCategories[index] = value;
        return { ...prevForm, categorias: newCategories };
      }
      return {
        ...prevForm,
        [name]: type === "checkbox" ? checked : files ? files[0] : value,
      };
    });
  };

  const addCategoria = () => {
    setForm((prevForm) => ({
      ...prevForm,
      categorias: [...prevForm.categorias, ""],
    }));
  };

  const removeCategoria = (index) => {
    setForm((prevForm) => {
      const newCategories = [...prevForm.categorias];
      newCategories.splice(index, 1);
      return { ...prevForm, categorias: newCategories };
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setSuccess(null);

    const formData = new FormData();
    formData.append("editorId", usuario.userId);
    formData.append("titulo", form.titulo);
    formData.append("texto", form.texto); // Agora inclui formatações HTML
    formData.append("visibilidadeVip", form.visibilidadeVip);
    formData.append("data", new Date().toISOString());
    formData.append("imagem", form.imagem);
    formData.append("video", form.video);

    form.categorias.forEach((categoria) => {
      formData.append("categorias", categoria);
    });

    try {
      const response = await publicacoesAPI.criar(formData);
      if (response?.data?.publicacaoId) {
        setSuccess("Publicação criada com sucesso!");
        setForm({
          titulo: "",
          texto: "",
          categorias: [""],
          visibilidadeVip: false,
          imagem: null,
          video: null,
        });
      }
    } catch (error) {
      console.error("Erro ao criar a publicação:", error);
      setError("Erro ao criar a publicação. Verifique os campos e tente novamente.");
    }
  };

  return (
    <Container className="py-4">
      <h2 className="mb-4">Criar Nova Publicação</h2>

      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <Form onSubmit={handleSubmit} encType="multipart/form-data">
        <Form.Group className="mb-3">
          <Form.Label>Título</Form.Label>
          <Form.Control
            type="text"
            name="titulo"
            value={form.titulo}
            onChange={handleInputChange}
            required
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>Texto</Form.Label>
          <ReactQuill
            value={form.texto}
            onChange={(value) => setForm({ ...form, texto: value })}
            theme="snow"
            className="mb-3"
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>Imagem</Form.Label>
          <Form.Control
            type="file"
            name="imagem"
            accept="image/*"
            onChange={handleInputChange}
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>Vídeo</Form.Label>
          <Form.Control
            type="file"
            name="video"
            accept="video/*"
            onChange={handleInputChange}
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>Categorias</Form.Label>
          {form.categorias.map((categoria, index) => (
            <div key={index} className="d-flex gap-2 mb-2">
              <Form.Control
                type="text"
                name="categorias"
                value={categoria}
                data-index={index}
                onChange={handleInputChange}
                required
              />
              <Button
                variant="danger"
                onClick={() => removeCategoria(index)}
                disabled={form.categorias.length <= 1}>
                Remover
              </Button>
            </div>
          ))}
          <Button
            variant="secondary"
            type="button"
            onClick={addCategoria}
            className="mt-2">
            Adicionar Categoria
          </Button>
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Check
            type="checkbox"
            label="Visibilidade VIP"
            name="visibilidadeVip"
            checked={form.visibilidadeVip}
            onChange={handleInputChange}
          />
        </Form.Group>

        <Button variant="primary" type="submit">
          Publicar
        </Button>
      </Form>
    </Container>
  );
};

export default PainelEditor;
